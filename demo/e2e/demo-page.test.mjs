// Playwright checks for the bundled demo page. Run it through `./gradlew :demo:e2eTest`, or
// directly with `node demo/e2e/demo-page.test.mjs` after a build; Playwright and Chromium come
// from the Kotlin/JS npm workspace under build/js.
import { readFile } from 'node:fs/promises'
import { createServer } from 'node:http'
import { createRequire } from 'node:module'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const repoRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..')
const distDir = process.env.DEMO_DIST ?? path.join(repoRoot, 'demo/build/dist/js/productionExecutable')
const { chromium } = createRequire(path.join(repoRoot, 'build/js/package.json'))('playwright')

const mimeTypes = {
    '.css': 'text/css; charset=utf-8',
    '.html': 'text/html; charset=utf-8',
    '.js': 'text/javascript; charset=utf-8',
    '.png': 'image/png',
    '.svg': 'image/svg+xml',
}

const server = createServer(async (request, response) => {
    const pathname = decodeURIComponent(new URL(request.url, 'http://localhost').pathname)
    const relative = pathname === '/' ? 'index.html' : pathname.replace(/^\/+/, '')
    const filePath = path.join(distDir, relative)
    if (!filePath.startsWith(distDir + path.sep)) {
        response.writeHead(403).end()
        return
    }
    try {
        const body = await readFile(filePath)
        response.writeHead(200, { 'content-type': mimeTypes[path.extname(filePath)] ?? 'application/octet-stream' })
        response.end(body)
    } catch {
        response.writeHead(404).end('not found')
    }
})

await new Promise(resolve => server.listen(0, '127.0.0.1', resolve))
const base = `http://127.0.0.1:${server.address().port}/`

const failures = []
const check = (label, condition, detail = '') => {
    if (!condition) failures.push(`${label}${detail ? ` (${detail})` : ''}`)
    console.log(`${condition ? 'ok  ' : 'FAIL'} ${label}${detail ? `: ${detail}` : ''}`)
}

const browser = await chromium.launch()
try {
    const context = await browser.newContext({ locale: 'en-US', viewport: { width: 1280, height: 1000 } })
    const page = await context.newPage()

    const consoleErrors = []
    const failedRequests = []
    page.on('console', message => {
        if (message.type() === 'error') consoleErrors.push(message.text())
    })
    page.on('requestfailed', request => failedRequests.push(`${request.url()} ${request.failure()?.errorText}`))

    const waitForIcons = () => page.waitForFunction(() =>
        [...document.querySelectorAll('iconify-icon')].every(element => element.status === 'rendered'))

    const snapshot = () => page.evaluate(() => ({
        lang: document.documentElement.lang,
        title: document.title,
        h1: document.querySelector('.hero h1')?.textContent,
        switchLabel: document.querySelector('.lang-toggle')?.textContent,
        switchAria: document.querySelector('.lang-toggle')?.getAttribute('aria-label'),
        sectionTitles: [...document.querySelectorAll('.section-head h2')].map(element => element.textContent),
        inline: document.querySelector('.inline-demo')?.textContent,
        modes: [...document.querySelectorAll('.row .card-name')].slice(5).map(element => element.textContent),
        storageInfo: [...document.querySelectorAll('.row .muted')].map(element => element.textContent),
        loaderStatus: document.querySelector('.loader-status')?.textContent,
        loaderButton: document.querySelector('.loader .button')?.textContent,
        loaderAria: document.querySelector('.loader-input')?.getAttribute('aria-label'),
        icons: document.querySelectorAll('iconify-icon').length,
        renderedIcons: [...document.querySelectorAll('iconify-icon')].filter(element => element.status === 'rendered').length,
        stored: localStorage.getItem('ktjs-iconify-demo-language'),
    }))

    await page.goto(base, { waitUntil: 'networkidle' })
    await waitForIcons()

    const en = await snapshot()
    check('initial html lang is en', en.lang === 'en', en.lang)
    check('initial title is English', en.title.includes('Kotlin/JS wrappers for Iconify'), en.title)
    check('initial h1 is English', en.h1 === 'Typed Kotlin/JS wrappers for Iconify', en.h1)
    check('switch button offers Chinese', en.switchLabel === '中文', en.switchLabel)
    check('switch aria is English', en.switchAria === 'Switch to Chinese', en.switchAria)
    check('loader idle is English', en.loaderStatus === 'Loading uses loadIcon() from the Iconify API.', en.loaderStatus)
    check('loader button is English', en.loaderButton === 'Load icon', en.loaderButton)
    check('all icons rendered', en.icons === en.renderedIcons && en.icons > 0, `${en.renderedIcons}/${en.icons}`)

    await page.click('.lang-toggle')
    await page.waitForFunction(() => document.documentElement.lang === 'zh')
    const zh = await snapshot()
    check('html lang switches to zh', zh.lang === 'zh', zh.lang)
    check('title is Chinese', zh.title === 'ktjs-iconify · Iconify 的 Kotlin/JS 封装', zh.title)
    check('h1 is Chinese', zh.h1 === '为 Iconify 打造的 Kotlin/JS 类型化封装', zh.h1)
    check('switch button offers English', zh.switchLabel === 'English', zh.switchLabel)
    check('switch aria is Chinese', zh.switchAria === '切换到英文', zh.switchAria)
    check('section titles are Chinese', zh.sectionTitles[0] === '来自 Iconify API 的图标' && zh.sectionTitles.length === 5, JSON.stringify(zh.sectionTitles))
    check('inline copy is Chinese', zh.inline.startsWith('传入 ') && zh.inline.endsWith('，让图标与周围文字对齐。'), zh.inline)
    check('mode captions are Chinese', JSON.stringify(zh.modes) === JSON.stringify(['默认 <svg> 渲染', '背景图', 'CSS mask，跟随 currentColor']), JSON.stringify(zh.modes))
    check('storage info is Chinese', zh.storageInfo[0]?.includes('字符'), zh.storageInfo[0])
    check('loader idle is Chinese', zh.loaderStatus === '加载使用 Iconify API 的 loadIcon()。', zh.loaderStatus)
    check('loader button is Chinese', zh.loaderButton === '加载图标', zh.loaderButton)
    check('loader aria is Chinese', zh.loaderAria === '图标名称', zh.loaderAria)
    check('language persisted', zh.stored === 'zh', zh.stored)
    check('icons still rendered after switch', zh.icons === zh.renderedIcons, `${zh.renderedIcons}/${zh.icons}`)

    await page.fill('.loader-input', 'mdi:home')
    await page.click('.loader .button')
    await page.waitForFunction(() => document.querySelector('.loader-status')?.textContent?.startsWith('已加载'), null, { timeout: 15000 })
    const loaded = await page.evaluate(() => ({
        status: document.querySelector('.loader-status')?.textContent,
        outputIcons: document.querySelectorAll('.loader-output iconify-icon').length,
    }))
    check('loader status localized after load', loaded.status.startsWith('已加载 mdi:home'), loaded.status)
    check('loader output icon present', loaded.outputIcons === 1, String(loaded.outputIcons))

    await page.reload({ waitUntil: 'networkidle' })
    await waitForIcons()
    const reloaded = await snapshot()
    check('language survives reload', reloaded.lang === 'zh' && reloaded.h1 === '为 Iconify 打造的 Kotlin/JS 类型化封装', `${reloaded.lang} / ${reloaded.h1}`)
    check('icons rendered after reload', reloaded.icons === reloaded.renderedIcons && reloaded.icons > 0, `${reloaded.renderedIcons}/${reloaded.icons}`)

    await page.click('.lang-toggle')
    await page.waitForFunction(() => document.documentElement.lang === 'en')
    const enAgain = await snapshot()
    check('switching back restores English', enAgain.h1 === 'Typed Kotlin/JS wrappers for Iconify' && enAgain.loaderStatus === 'Loading uses loadIcon() from the Iconify API.', enAgain.loaderStatus)
    check('language persisted back to en', enAgain.stored === 'en', enAgain.stored)
    await context.close()

    // A browser with no stored choice that prefers Chinese lands on Chinese.
    const zhContext = await browser.newContext({ locale: 'zh-CN', viewport: { width: 1280, height: 1000 } })
    const zhPage = await zhContext.newPage()
    await zhPage.goto(base, { waitUntil: 'networkidle' })
    const autoZh = await zhPage.evaluate(() => ({
        lang: document.documentElement.lang,
        h1: document.querySelector('.hero h1')?.textContent,
    }))
    check('zh-CN browser defaults to Chinese', autoZh.lang === 'zh' && autoZh.h1 === '为 Iconify 打造的 Kotlin/JS 类型化封装', `${autoZh.lang} / ${autoZh.h1}`)
    await zhContext.close()

    check('no console errors', consoleErrors.length === 0, consoleErrors.join(' | '))
    check('no failed requests', failedRequests.length === 0, failedRequests.join(' | '))
} finally {
    await browser.close()
    server.close()
}

console.log(failures.length === 0 ? '\nALL CHECKS PASSED' : `\n${failures.length} FAILURES`)
process.exit(failures.length === 0 ? 0 : 1)

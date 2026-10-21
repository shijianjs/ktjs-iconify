# ktjs-iconify-tools

`@iconify/tools` 的 Kotlin/JS wrapper，仅面向 Node.js（浏览器侧请用 `ktjs-iconify-utils`）。

## ESM-only 包，必须走 `lib/index.js` 子路径

`@iconify/tools` 是纯 ESM 包，其 `package.json` 的 `exports["."]` 只声明了 `types` 和 `import` 条件，没有 `require`/`default`。wrapper 发布的是 klib，消费方可能按 CommonJS 或 ESM 链接，因此：

- 所有 `@file:JsModule` 必须写成 `@file:JsModule("@iconify/tools/lib/index.js")`，不能写包名；子路径对两种模块格式都可用。
- 该包另有 `"./*": "./*"` 导出，所以上面的子路径是公开可用的。
- 本模块自身输出 ESM（根约定 `useEsModules()`）；CommonJS 消费者依赖 Node 20.19+ / 22.12+ / 24 的 `require(ESM)`，无需 `--experimental-require-module`。

## 与上游语义不一致的地方

- `IconSet.list(types?)` 默认只返回 `icon` 和 `variation`，不包含 `alias`；要列别名需显式传 `arrayOf("icon", "alias")` 或 `arrayOf("icon", "variation", "alias")`。
- `SVG.viewBox` 是 `{left, top, width, height}` 对象（`IconViewBox`），而 `@iconify/utils` 的 `SVGViewBox` 是四元组数组，两者不要混用。

## 测试

```bash
./gradlew :ktjs-iconify-tools:jsNodeTest
```

测试依赖 `kotlin-js-store/package-lock.json`；新增/升级 npm 依赖后先跑 `./gradlew kotlinUpgradePackageLock`。

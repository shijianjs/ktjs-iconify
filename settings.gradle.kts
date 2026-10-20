pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        // 阿里云镜像的 kotlin-wrappers 版本经常滞后，wrapper 直接走 Maven Central。
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                includeGroup("org.jetbrains.kotlin-wrappers")
                // 阿里云有 POM 但缺 klib，Gradle 不会回退到下一个仓库。
                includeModule("org.jetbrains.kotlin", "kotlin-js-plain-objects")
            }
        }
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        mavenCentral()
    }
}

rootProject.name = "ktjs-iconify"

include(":ktjs-iconify-types")
include(":ktjs-iconify-utils")
include(":ktjs-iconify-tools")
include(":ktjs-iconify-icon")

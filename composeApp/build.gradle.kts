import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.androidKmpLibrary)
}

kotlin {

    androidLibrary{
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.targetSdk.get().toInt()
        namespace = "com.bashkevich.counteroverlay.composeApp"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                configDirectory = file("src/webpack.config.d")
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.material.icons.core)
            implementation(libs.material3.adaptive)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.websockets)

            implementation(libs.androidx.room3.runtime)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.sqlite.bundled)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.androidx.sqlite.web)
            implementation(
                npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile)
            )
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspDesktop", libs.androidx.room3.compiler)
    add("kspWasmJs", libs.androidx.room3.compiler)
}



compose.desktop {
    application {
        mainClass = "com.bashkevich.counteroverlay.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bashkevich.counteroverlay"
            packageVersion = "1.0.0"
        }
    }
}

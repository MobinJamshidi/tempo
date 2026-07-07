plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy {
            force("androidx.navigation:navigation-compose:2.9.8")
            force("androidx.navigation:navigation-common:2.9.8")
            force("androidx.navigation:navigation-runtime:2.9.8")
        }
    }
}
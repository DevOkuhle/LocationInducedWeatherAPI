buildscript {
    dependencies {
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.perf.plugin)
        classpath(libs.hilt.android.gradle.plugin)
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.gitlab.detekt)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
}
import org.jetbrains.kotlin.fir.declarations.builder.buildScript

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories{
        google()
    }
    dependencies{
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}
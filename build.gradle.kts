plugins {
    id("com.android.application") version "8.10.1" apply false
    id("com.android.library") version "8.10.1" apply false
    kotlin("android") version "1.9.10" apply false
    kotlin("kapt") version "1.9.10" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

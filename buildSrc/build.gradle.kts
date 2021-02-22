import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.4.30"
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    google()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-dev/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.gradle:api:1.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.31-alpha")
    implementation("com.android.tools.build:gradle:4.1.2")
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("junit:junit:4.13.1")
    testImplementation("io.mockk:mockk:1.10.4")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("LibraryChargerPlugin") {
            id = "LibraryChargerPlugin"
            implementationClass = "com.oscarg798.amiibowiki.plugin.LibraryChargerPlugin"
            version = "1.0.0"
        }
    }
}

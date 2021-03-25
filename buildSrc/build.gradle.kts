import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.4.31"
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.gradle:api:1.0")
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
        PluginDeclaration("myPlugin").apply {
            id = "com.oscarg798.myPlugin"
            implementationClass = "MyPlugin"
            version="1.0.0"
        }
    }
}

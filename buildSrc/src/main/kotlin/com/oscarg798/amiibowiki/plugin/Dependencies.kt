/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.plugin


object Versions {
    const val kotlin = "1.4.30"
    const val dagger = "2.30.1"
    const val hilt = "2.31-alpha"
    const val hiltAndroidX = "1.0.0-alpha02"
    const val appCompat = "1.2.0"
    const val recyclerView = "1.1.0"
    const val cardView = "1.0.0"
    const val material = "1.3.0-beta01"
    const val constraintLayout = "2.0.4"
    const val multidex = "2.0.1"
    const val activityX = "1.2.0-alpha06"
    const val airbnb = "5.2.0"
    const val coroutines = "1.4.2"
    const val gson = "2.8.5"
    const val okHttp = "4.9.0"
    const val retrofit = "2.9.0"
    const val retrofitAdapter = "2.2.0"
    const val retrofitLogginInterceptor = "4.9.0"
    const val retrofitConverterScalars = "2.1.0"
    const val viewModelLifecycleScope = "2.3.0-alpha06"
    const val lifecycle = "2.3.0-rc01"
    const val archLifecycle = "1.1.1"
    const val navigation = "2.3.0"
    const val room = "2.2.6"
    const val androidSupport = "28.0.0"
    const val mockk = "1.10.4"
    const val espresso = "3.3.0"
    const val junit = "4.13.1"
    const val kluent = "1.61"
    const val testRunner = "1.3.0"
    const val mockWebServer = "4.9.0"
    const val uiAutomator = "2.2.0"
}

object Hilt {
    const val name = "hilt"
    const val Android = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val AndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
    const val Compiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val ViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hiltAndroidX}"
    const val Common = "androidx.hilt:hilt-common:${Versions.hiltAndroidX}"
}

object Dagger {
    const val Main = "com.google.dagger:dagger:${Versions.dagger}"
    const val Compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val Processor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
}

object Kotlin {
    const val name = "kotlin"
    const val jdk = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
}

object Android {
    const val name = "android"
    const val pluginName = "android_plugin"

    object Libraries {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
        const val cardView = "androidx.cardview:cardview:${Versions.cardView}"
        const val design = "com.google.android.material:material:${Versions.material}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val multidex = "androidx.multidex:multidex:${Versions.multidex}"
        const val activityX = "androidx.activity:activity-ktx:${Versions.activityX}"
        const val annotations = "com.android.support:support-annotations:${Versions.androidSupport}"
        const val uiAutomator = "androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val testRules = "androidx.test:rules:${Versions.testRunner}"
    }
}

object Airbnb {
    const val name = "airbnb"

    object Libraries {
        const val dispatcher = "com.airbnb:deeplinkdispatch:${Versions.airbnb}"
        const val processor = "com.airbnb:deeplinkdispatch-processor:${Versions.airbnb}"
    }
}

object Coroutines {
    const val name = "coroutines"

    object Libraries {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    }
}

object Retrofit {

    const val name = "retrofit"

    object Libraries {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        const val okHttp3 = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
        const val loggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLogginInterceptor}"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
    }
}

object Lifecycle {

    const val name = "lifecycle"

    object Libraries {
        const val viewModel =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelLifecycleScope}"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
        const val extensions = "android.arch.lifecycle:extensions:${Versions.archLifecycle}"
    }
}

object Navigation {

    const val name = "navigation"

    object Libraries {
        const val fragment = "androidx.navigation:navigation-fragment:${Versions.navigation}"
        const val ui = "androidx.navigation:navigation-ui:${Versions.navigation}"
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    }
}

object Room {

    const val name = "room"

    object Libraries {
        const val runtime = "androidx.room:room-runtime:${Versions.room}"
        const val compiler = "androidx.room:room-compiler:${Versions.room}"
        const val ktx = "androidx.room:room-ktx:${Versions.room}"
        const val testing = "androidx.room:room-testing:${Versions.room}"
    }
}

object Mockk {

    object Libraries {
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val core = "org.mockito:mockito-core:${Versions.mockk}"
        const val android = "io.mockk:mockk-android:${Versions.mockk}"
    }
}

object Espresso {

    object Libraries {
        const val contrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
        const val core = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        const val intents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    }
}

object Junit {

    object Libraries {
        const val junit = "junit:junit:${Versions.junit}"
    }
}

object UnitTesting {

    const val name = "unit_tests"

    object Libraries {
        const val kluent = "org.amshove.kluent:kluent:${Versions.kluent}"
    }
}

object AndroidTesting {

    const val name = "android_test"
}

sealed class ModuleType(val value: String) {

    object Library : ModuleType("library")
    object Application : ModuleType("application")

   companion object {
       fun getFromName(type: String): ModuleType = when (type) {
           "library" -> Library
           else -> Application
       }
   }
}

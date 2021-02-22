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

package com.oscarg798.amiibowiki.plugin/*
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

import com.oscarg798.amiibowiki.plugin.charger.IMPLEMENTATION
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.junit.Before
import org.junit.Test

class LibraryChargerPluginTest {

    private val project = mockk<Project>()
    private val plugin = mockk<Plugin<*>>()
    private val pluginContainer = mockk<PluginContainer>()
    private val extensionContainer = mockk<ExtensionContainer>()
    private val dependencyHandler = mockk<DependencyHandler>(relaxed = true)

    private val extension = LibraryChargerPluginExtension()
    private lateinit var libraryChargerPlugin: LibraryChargerPlugin

    @Before
    fun setup() {
        every { project.extensions } answers { extensionContainer }
        every { project.plugins } answers { pluginContainer }
        every { pluginContainer.apply(any<String>()) } answers { plugin }
        every { extensionContainer.create(any<String>(), any<Class<*>>()) } answers { extension }
        every { project.dependencies } answers { dependencyHandler }

        libraryChargerPlugin = LibraryChargerPlugin()
    }

    @Test
    fun `when plugin is applied with default values then it should apply the right plugins`() {
        extension.plugins = listOf()
        libraryChargerPlugin.apply(project)

        verify {
            pluginContainer.apply("com.android.library")
            pluginContainer.apply("kotlin-android")
            pluginContainer.apply("kotlin-kapt")
            pluginContainer.apply("kotlin-parcelize")
        }
    }

    @Test
    fun `given extension with coroutines when plugin applied then it should add libraries from charger plus defaults ones`() {
        extension.libraries = listOf(Coroutines.name)

        libraryChargerPlugin.apply(project)

        verify(ordering = Ordering.SEQUENCE) {
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.appCompat)
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.recyclerView)
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.design)
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.constraintLayout)
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.multidex)
            dependencyHandler.add(IMPLEMENTATION, Android.Libraries.activityX)
            dependencyHandler.add(IMPLEMENTATION, Kotlin.jdk)
            dependencyHandler.add(IMPLEMENTATION, Coroutines.Libraries.core)
            dependencyHandler.add(IMPLEMENTATION, Coroutines.Libraries.android)
        }

        verify(exactly = 9) {
            dependencyHandler.add(any(), any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given a charger named added to the extension when charger name does not exist and plugin applied then it should crash`() {
        extension.libraries = listOf("any")

        libraryChargerPlugin.apply(project)
    }

    @Test
    fun `given plugins to apply when plugin applied then it should add the plugins`() {
        extension.plugins = listOf(Hilt.name, Kotlin.name)

        libraryChargerPlugin.apply(project)

        verify(ordering = Ordering.SEQUENCE) {
            pluginContainer.apply("com.android.library")
            pluginContainer.apply("kotlin-android")
            pluginContainer.apply("kotlin-kapt")
            pluginContainer.apply("kotlin-parcelize")
            pluginContainer.apply("dagger.hilt.android.plugin")
        }
    }
}

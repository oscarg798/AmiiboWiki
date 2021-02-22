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

import com.oscarg798.amiibowiki.plugin.charger.Charger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.ChargerCreator
import com.oscarg798.amiibowiki.plugin.charger.dependencies.creator.ChargerName
import com.oscarg798.amiibowiki.plugin.charger.dependencies.creator.DependencyChargerCreator
import com.oscarg798.amiibowiki.plugin.charger.plugin.AndroidPluginCharger
import com.oscarg798.amiibowiki.plugin.charger.plugin.creator.PluginChargerCreator
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.Locale


open class LibraryChargerPlugin : Plugin<Project> {

    private val locale = Locale.getDefault()
    private lateinit var pluginChargerCreator: ChargerCreator
    private val dependenciesChargerCreator: ChargerCreator = DependencyChargerCreator

    override fun apply(target: Project) {
        val extension = target.extensions.create(
            EXTENSION_NAME,
            LibraryChargerPluginExtension::class.java
        )

        pluginChargerCreator = PluginChargerCreator(extension)

        with(target) {
            val pluginChargers = pluginChargerCreator.getAllChargesInCreator()
            getChargerNames(
                extension.plugins,
                pluginChargerCreator
            ).forEach { pluginChargerName ->
                pluginChargers.getChargerByName(pluginChargerName).charge(this)

            }

            val dependenciesCharger = dependenciesChargerCreator.getAllChargesInCreator()
            getChargerNames(
                extension.libraries,
                dependenciesChargerCreator
            ).forEach { chargerName ->
                dependenciesCharger.getChargerByName(chargerName).charge(this)
            }
        }
    }

    private fun ChargerCreator.getAllChargesInCreator() =
        HashMap<ChargerName, Charger>().apply {
            putAll(getDefaultChargers())
            putAll(getChargers())
        }

    private fun getChargerNames(
        userChargers: List<ChargerName>,
        chargerCreator: ChargerCreator
    ): List<ChargerName> {
        val defaultChargers = chargerCreator.getDefaultChargers().keys
        val plugins = userChargers.toMutableList()
        plugins.removeAll(defaultChargers)
        plugins.addAll(DEFAULT_CHARGER_NAMES_INSERT_POSITION, defaultChargers)
        return plugins
    }

    private fun Map<ChargerName, Charger>.getChargerByName(
        chargerName: ChargerName
    ): Charger =
        this[chargerName]
            ?: throw IllegalArgumentException("There is no applier for $chargerName")
}

private const val DEFAULT_CHARGER_NAMES_INSERT_POSITION = 0
private const val KOTLIN_CHARGER_POSITION = 0
private const val ANDROID_CHARGER_POSITION = 0
private const val EXTENSION_NAME = "charger"

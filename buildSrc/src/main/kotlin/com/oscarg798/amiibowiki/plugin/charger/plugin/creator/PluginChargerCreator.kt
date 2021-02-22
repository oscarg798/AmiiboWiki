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

package com.oscarg798.amiibowiki.plugin.charger.plugin.creator

import com.oscarg798.amiibowiki.plugin.Android
import com.oscarg798.amiibowiki.plugin.Hilt
import com.oscarg798.amiibowiki.plugin.Kotlin
import com.oscarg798.amiibowiki.plugin.LibraryChargerPluginExtension
import com.oscarg798.amiibowiki.plugin.Navigation
import com.oscarg798.amiibowiki.plugin.charger.Charger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.AndroidCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.ChargerCreator
import com.oscarg798.amiibowiki.plugin.charger.dependencies.KotlinCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.creator.ChargerName
import com.oscarg798.amiibowiki.plugin.charger.plugin.AndroidPluginCharger
import com.oscarg798.amiibowiki.plugin.charger.plugin.HiltPluginCharger
import com.oscarg798.amiibowiki.plugin.charger.plugin.KotlionPluginCharger
import com.oscarg798.amiibowiki.plugin.charger.plugin.NavigationPluginCharger


class PluginChargerCreator(private val libraryChargerPluginExtension: LibraryChargerPluginExtension) :
    ChargerCreator {

    override fun getDefaultChargers(): Map<ChargerName, Charger> =
        HashMap<ChargerName, Charger>().apply {
            put(Android.name, AndroidPluginCharger(com.oscarg798.amiibowiki.plugin.ModuleType.getFromName(libraryChargerPluginExtension.moduleType)))
            put(Kotlin.name, KotlionPluginCharger())
        }

    override fun getChargers(): Map<ChargerName, Charger> = HashMap<ChargerName, Charger>().apply {
        put(Hilt.name, HiltPluginCharger())
        put(Navigation.name, NavigationPluginCharger())
    }
}

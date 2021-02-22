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

package com.oscarg798.amiibowiki.plugin.charger.dependencies.creator

import com.oscarg798.amiibowiki.plugin.Airbnb
import com.oscarg798.amiibowiki.plugin.Android
import com.oscarg798.amiibowiki.plugin.AndroidTesting
import com.oscarg798.amiibowiki.plugin.Coroutines
import com.oscarg798.amiibowiki.plugin.Hilt
import com.oscarg798.amiibowiki.plugin.Kotlin
import com.oscarg798.amiibowiki.plugin.Lifecycle
import com.oscarg798.amiibowiki.plugin.Navigation
import com.oscarg798.amiibowiki.plugin.Retrofit
import com.oscarg798.amiibowiki.plugin.Room
import com.oscarg798.amiibowiki.plugin.UnitTesting
import com.oscarg798.amiibowiki.plugin.charger.Charger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.AirBnbCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.AndroidCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.AndroidTestingCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.ChargerCreator
import com.oscarg798.amiibowiki.plugin.charger.dependencies.CoroutinesCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.HiltCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.KotlinCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.LifecycleCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.NavigationCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.RetrofitCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.RoomCharger
import com.oscarg798.amiibowiki.plugin.charger.dependencies.UnitTestingCharger

object DependencyChargerCreator : ChargerCreator {

    override fun getDefaultChargers(): Map<ChargerName, Charger> {
        return HashMap<ChargerName, Charger>().apply {
            put(Android.name, AndroidCharger())
            put(Kotlin.name, KotlinCharger())
        }
    }

    override fun getChargers(): Map<ChargerName, Charger> {
        return HashMap<ChargerName, Charger>().apply {
            putAll(getDefaultChargers())
            put(Hilt.name, HiltCharger())
            put(Airbnb.name, AirBnbCharger())
            put(Coroutines.name, CoroutinesCharger())
            put(Retrofit.name, RetrofitCharger())
            put(Lifecycle.name, LifecycleCharger())
            put(Navigation.name, NavigationCharger())
            put(Room.name, RoomCharger())
            put(UnitTesting.name, UnitTestingCharger())
            put(AndroidTesting.name, AndroidTestingCharger())
        }
    }


}

typealias ChargerName = String


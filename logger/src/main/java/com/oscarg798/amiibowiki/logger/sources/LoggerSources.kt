/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.logger.sources

import com.oscarg798.lomeno.event.LogSource
import com.oscarg798.lomeno.sources.NETWORK_LOG_SOURCE_NAME
import com.oscarg798.lomeno.sources.NetworkLogSource

object MixPanelSource: LogSource {

    override val name: String =  MIXPANEL_LOG_SOURCE_NAME
}
object FirebaseSource : LogSource {
    override val name: String = FIREBASE_LOG_SOURCE_NAME
}

object CrashlyticsSource : LogSource {
    override val name: String = CRASHLYTICS_LOG_SOURCE_NAME
}


fun String.toLogSource() = when(this){
    FIREBASE_LOG_SOURCE_NAME-> FirebaseSource
    CRASHLYTICS_LOG_SOURCE_NAME -> CrashlyticsSource
    NETWORK_LOG_SOURCE_NAME -> NetworkLogSource
    else -> throw IllegalArgumentException("There not LogSource with name: $this")
}

const val MIXPANEL_LOG_SOURCE_NAME = "MIXPANEL"
const val FIREBASE_LOG_SOURCE_NAME = "FIREBASE"
const val CRASHLYTICS_LOG_SOURCE_NAME = "CRASHLYTICS"

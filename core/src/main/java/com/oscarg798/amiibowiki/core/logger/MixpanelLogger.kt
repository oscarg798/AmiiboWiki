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

package com.oscarg798.amiibowiki.core.logger

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.oscarg798.lomeno.event.LogEvent
import com.oscarg798.lomeno.logger.Logger
import org.json.JSONObject

class MixpanelLogger(context: Context, apiKey: String) : Logger {

    private val mixpanel: MixpanelAPI = MixpanelAPI.getInstance(context, apiKey)

    override fun identify(id: String) {
        mixpanel.identify(id)
    }

    override fun log(logEvent: LogEvent) {
        val properties = JSONObject()
        logEvent.properties.map { entry ->
            properties.put(entry.key, entry.value)
        }

        mixpanel.track(logEvent.name, properties)
    }

    fun flush() {
    }
}

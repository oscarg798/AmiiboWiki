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

package com.oscarg798.amiibowiki.logger.events

import com.oscarg798.amiibowiki.logger.sources.FirebaseSource
import com.oscarg798.lomeno.event.LogEvent
import com.oscarg798.lomeno.event.LogSource

class ScreenViewEvent(
    screenName: String,
    private val supportedSources: Set<LogSource> = setOf<LogSource>(FirebaseSource),
    private val extraProperties: Map<String, String>? = null
) : LogEvent {

    override val name: String =
        SCREEN_VIEW_EVENT_NAME

    override val properties: Map<String, String> = mutableMapOf<String, String>().apply {
        put(SCREEN_NAME_EVENT_NAME, screenName)
        extraProperties?.let {
            putAll(extraProperties)
        }
    }

    override fun isSourceSupported(logSource: LogSource): Boolean =
        supportedSources.contains(FirebaseSource)
}

private const val SCREEN_NAME_EVENT_NAME = "SCREEN_NAME"
private const val SCREEN_VIEW_EVENT_NAME = "SCREEN_VIEW"

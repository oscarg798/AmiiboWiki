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

package com.oscarg798.amiibowiki.settings.featurepoint

import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.models.Flavor
import com.oscarg798.amiibowiki.core.utils.ResourceProvider
import com.oscarg798.amiibowiki.settings.R
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.flagly.featurepoint.SuspendFeatureFactory
import javax.inject.Inject

class DevelopmentActivityFeatureFactory @Inject constructor(
    private val config: Config,
    private val stringResourceProvider: ResourceProvider<String>
) : SuspendFeatureFactory<PreferenceBuilder, Unit> {

    override suspend fun create(): PreferenceBuilder = PreferenceBuilder(
        DEVELOPMENT_ACTIVITY_PREFERENCE_KEY,
        stringResourceProvider.provide(R.string.development_activity_preference_title)
    )

    override suspend fun isApplicable(params: Unit): Boolean = config.flavor is Flavor.Debug
}

const val DEVELOPMENT_ACTIVITY_PREFERENCE_KEY = "development_activity"

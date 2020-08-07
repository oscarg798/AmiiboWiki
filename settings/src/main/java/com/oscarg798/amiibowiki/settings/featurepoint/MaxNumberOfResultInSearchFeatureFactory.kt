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

import com.oscarg798.amiibowiki.core.constants.MAX_NUMBER_OF_SEARCH_RESULTS_PREFERENCE_KEY
import com.oscarg798.amiibowiki.core.utils.ResourceProvider
import com.oscarg798.amiibowiki.settings.R
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.models.PreferenceType
import com.oscarg798.flagly.featurepoint.SuspendFeatureFactory
import javax.inject.Inject

class MaxNumberOfResultInSearchFeatureFactory @Inject constructor(
    private val stringResourceProvider: ResourceProvider<String>
) : SuspendFeatureFactory<PreferenceBuilder, Unit> {

    override suspend fun create(): PreferenceBuilder = PreferenceBuilder(
        MAX_NUMBER_OF_SEARCH_RESULTS_PREFERENCE_KEY,
        preferenceType = PreferenceType.Text(
            DEFAULT_MAX_NUMBER_OF_RESULTS
        ),
        title = stringResourceProvider.provide(R.string.max_number_of_search_results_preference_title)
    )

    override suspend fun isApplicable(params: Unit): Boolean = true
}

private const val DEFAULT_MAX_NUMBER_OF_RESULTS = "20"

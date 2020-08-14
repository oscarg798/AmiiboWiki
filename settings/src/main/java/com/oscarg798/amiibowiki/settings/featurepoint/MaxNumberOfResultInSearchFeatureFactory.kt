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

import android.text.TextWatcher
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import com.oscarg798.amiibowiki.core.constants.MAXIMUM_ALLOWED_SEARCH_LIMIT
import com.oscarg798.amiibowiki.core.constants.MAX_NUMBER_OF_SEARCH_RESULTS_PREFERENCE_KEY
import com.oscarg798.amiibowiki.core.constants.MINIMUN_ALLOWED_ALLOWED_SEARCH_LIMIT
import com.oscarg798.amiibowiki.core.utils.ResourceProvider
import com.oscarg798.amiibowiki.settings.R
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.models.TextPreferenceChangeListener
import com.oscarg798.flagly.featurepoint.SuspendFeatureFactory
import javax.inject.Inject

class MaxNumberOfResultInSearchFeatureFactory @Inject constructor(
    private val stringResourceProvider: ResourceProvider<String>
) : SuspendFeatureFactory<PreferenceBuilder, Unit>, TextPreferenceChangeListener {

    override suspend fun create(): PreferenceBuilder = PreferenceBuilder.Text(
        key = MAX_NUMBER_OF_SEARCH_RESULTS_PREFERENCE_KEY,
        title = stringResourceProvider.provide(R.string.max_number_of_search_results_preference_title),
        defaultValue = stringResourceProvider.provide(R.string.search_limit_default_value),
        textPreferenceChangeListener = this,
        inputType = PreferenceBuilder.Text.InputType.Number(
            MINIMUN_ALLOWED_ALLOWED_SEARCH_LIMIT,
            MAXIMUM_ALLOWED_SEARCH_LIMIT
        )
    )

    override suspend fun isApplicable(params: Unit): Boolean = true

    override fun onPreferenceChange(
        newPreferenceText: String?,
        editText: EditText,
        watcher: TextWatcher
    ) {
        if (newPreferenceText.isNullOrEmpty() || !newPreferenceText.isDigitsOnly()) {
            return
        }

        editText.removeTextChangedListener(watcher)
        val limit = newPreferenceText.toInt()
        when {
            limit < MINIMUN_ALLOWED_ALLOWED_SEARCH_LIMIT -> editText.setError(
                String.format(
                    stringResourceProvider.provide(R.string.search_filter_lower_limit_error),
                    MINIMUN_ALLOWED_ALLOWED_SEARCH_LIMIT
                )
            )

            limit > MAXIMUM_ALLOWED_SEARCH_LIMIT -> editText.setError(
                String.format(
                    stringResourceProvider.provide(R.string.search_filter_upper_limit_error),
                    MAXIMUM_ALLOWED_SEARCH_LIMIT
                )
            )
        }

        editText.addTextChangedListener(watcher)
    }
}

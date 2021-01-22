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

package com.oscarg798.amiibowiki.splash.mvi

import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.core.mvi.ReducerCompat
import javax.inject.Inject

class SplashReducer @Inject constructor() : ReducerCompat<SplashResult, SplashViewStateCompat> {

    override suspend fun reduce(state: SplashViewStateCompat, from: SplashResult): SplashViewStateCompat =
        when (from) {
            is SplashResult.TypesFetched -> state.copy(
                isIdling = false,
                navigatingToFirstScreen = true,
                error = null
            )
            is SplashResult.Error -> state.copy(
                isIdling = false,
                navigatingToFirstScreen = false,
                error = AmiiboTypeFailure.FetchTypesFailure(from.error.message, from.error.cause as? Exception)
            )
        }
}

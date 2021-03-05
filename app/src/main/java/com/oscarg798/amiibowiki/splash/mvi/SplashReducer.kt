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
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.splash.failures.OutdatedAppException

import javax.inject.Inject

class SplashReducer @Inject constructor() : Reducer<SplashResult, SplashViewState> {

    override suspend fun reduce(state: SplashViewState, result: SplashResult): SplashViewState =
        when (result) {
            is SplashResult.TypesFetched -> SplashViewState.NavigatingToFirstscreen
            is SplashResult.Error -> if (result.error is OutdatedAppException) {
                SplashViewState.Error(
                    result.error
                )
            } else {
                SplashViewState.Error(
                    AmiiboTypeFailure.FetchTypesFailure(
                        result.error.message,
                        result.error.cause as? Exception
                    )
                )
            }
        }
}

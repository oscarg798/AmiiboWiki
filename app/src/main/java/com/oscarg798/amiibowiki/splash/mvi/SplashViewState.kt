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

import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.splash.failures.FetchTypesFailure

data class SplashViewState(
    val loading: ViewState.LoadingState,
    val status: FetchStatus,
    val error: FetchTypesFailure?
) : ViewState<SplashResult> {

    sealed class FetchStatus {
        object None : FetchStatus()
        object Success : FetchStatus()
    }

    override fun reduce(result: SplashResult): ViewState<SplashResult> {
        return when (result) {
            is SplashResult.Loading -> copy(
                loading = ViewState.LoadingState.Loading,
                status = FetchStatus.None,
                error = null
            )
            is SplashResult.TypesFetched -> copy(
                loading = ViewState.LoadingState.None,
                status = FetchStatus.Success,
                error = null
            )
            is SplashResult.Error -> copy(
                loading = ViewState.LoadingState.None,
                status = FetchStatus.None,
                error = FetchTypesFailure(result.exception.message, result.exception)
            )
        }
    }

    companion object {
        fun init() = SplashViewState(
            loading = ViewState.LoadingState.None,
            status = FetchStatus.None,
            error = null
        )
    }
}
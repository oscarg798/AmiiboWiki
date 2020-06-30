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

package com.oscarg798.amiibowiki.splash

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewState
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.base.onException
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.failures.FetchTypesFailure
import com.oscarg798.amiibowiki.splash.mvi.SplashResult
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SplashViewModel @Inject constructor(
    private val getAmiiboTypeUseCase: GetAmiiboTypeUseCase,
    private val coroutineContextProvider: CoroutineContextProvider
) :
    AbstractViewModel<SplashWish, SplashResult>() {

    init {
        process()
            .launchIn(viewModelScope)
    }

    override fun initState(): ViewState<SplashResult> = SplashViewState.init()

    override fun process(): Flow<ViewState<SplashResult>> = wishProcessor.asFlow()
        .flatMapMerge {
            fetchTypes()
        }.scan(_state.value) { state, result ->
            state.reduce(result) as SplashViewState
        }
        .onEach {
            _state.value = it
        }

    private fun fetchTypes() = flow<SplashResult> {
        emit(SplashResult.Loading)
        getAmiiboTypeUseCase.execute().map {
            emit(SplashResult.TypesFetched)
        }.onException {
            emit(SplashResult.Error(it))
        }
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
}
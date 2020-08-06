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

import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.extensions.onException
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.mvi.SplashResult
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.splash.usecases.ActivateRemoteConfigUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject



class SplashViewModel @Inject constructor(
    private val updateAmiiboTypeUseCase: UpdateAmiiboTypeUseCase,
    private val splashLogger: SplashLogger,
    private val activateRemoteConfigUseCase: ActivateRemoteConfigUseCase,
    private val coroutineContextProvider: CoroutineContextProvider
) :
    AbstractViewModel<SplashWish, SplashResult, SplashViewState>(SplashViewState.init()) {

    override fun onScreenShown() {
        splashLogger.trackScreenShown()
    }

    override suspend fun getResult(wish: SplashWish): Flow<SplashResult> = fetchTypes()

    private fun fetchTypes() = flow<SplashResult> {
        activateRemoteConfigUseCase.execute()
        updateAmiiboTypeUseCase.execute().map {
            emit(SplashResult.TypesFetched)
        }.onException {
            emit(SplashResult.Error(it))
        }
    }
        .flowOn(coroutineContextProvider.backgroundDispatcher)
}

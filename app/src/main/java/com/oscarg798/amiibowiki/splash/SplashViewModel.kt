/*
 * Copyright 2021 Oscar David Gallon Rosero
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
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.splash.mvi.UiEffect
import com.oscarg798.amiibowiki.splash.usecases.InitializeApplicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    private val splashLogger: SplashLogger,
    private val initializeApplicationUseCase: InitializeApplicationUseCase,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<SplashViewState, UiEffect, SplashWish>(SplashViewState()) {

    override fun processWish(wish: SplashWish) {
        fetchTypes()
    }

    private fun fetchTypes() {
        onScreenShown()
        initializeApplicationUseCase.execute()
            .onEach {
                _uiEffect.value = UiEffect.Navigate
            }
            .catch { cause ->
                if (cause !is Exception) {
                    throw cause
                }

                updateState { state ->
                    state.copy(loading = false, error = cause)
                }
            }.flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)
    }

    private fun onScreenShown() {
        splashLogger.trackScreenShown()
    }
}


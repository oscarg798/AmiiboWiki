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

import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.extensions.onException
import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.mvi.SplashResult
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.splash.usecases.ActivateRemoteConfigUseCase
import com.oscarg798.amiibowiki.splash.usecases.AuthenticateApplicationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import java.lang.NullPointerException
import javax.inject.Inject


class SplashViewModel @Inject constructor(
    private val updateAmiiboTypeUseCase: UpdateAmiiboTypeUseCase,
    private val splashLogger: SplashLogger,
    private val activateRemoteConfigUseCase: ActivateRemoteConfigUseCase,
    private val authenticateApplicationUseCase: AuthenticateApplicationUseCase,
    override val reducer: Reducer<@JvmSuppressWildcards SplashResult, @JvmSuppressWildcards SplashViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) :
    AbstractViewModel<SplashWish, SplashResult, SplashViewState>(SplashViewState.init()) {

    override fun onScreenShown() {
        splashLogger.trackScreenShown()
    }

    override suspend fun getResult(wish: SplashWish): Flow<SplashResult> = fetchTypes()

    private fun fetchTypes() = getAuthFlow().zip(getRemoteConfigActivationFlow()) { _, _ ->
        Unit
    }.flatMapConcat {
        getRefreshTypesFlow()
    }.catch { cause ->
        if (cause !is AmiiboTypeFailure) {
            throw cause
        }
    }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun getRefreshTypesFlow(): Flow<SplashResult.TypesFetched> = flow {
        updateAmiiboTypeUseCase.execute()
        emit(SplashResult.TypesFetched)
    }

    private fun getAuthFlow() = callToUnitFlow { authenticateApplicationUseCase.execute() }

    private fun getRemoteConfigActivationFlow() =
        callToUnitFlow { activateRemoteConfigUseCase.execute() }

    private fun callToUnitFlow(call: suspend () -> Any) = flow<Unit> {
        call.invoke()
        emit(Unit)
    }

}


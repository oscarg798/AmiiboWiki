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

package com.oscarg798.amiibowiki.navigation

import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.navigation.mvi.DashboardResult
import com.oscarg798.amiibowiki.navigation.mvi.DashboardViewState
import com.oscarg798.amiibowiki.navigation.mvi.DashboardWish
import com.oscarg798.amiibowiki.updatechecker.UpdateCheckerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usecase: UpdateCheckerUseCase,
    override val reducer: Reducer<@JvmSuppressWildcards DashboardResult, @JvmSuppressWildcards DashboardViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<DashboardWish, DashboardResult, DashboardViewState>(DashboardViewState.Idling) {

    override suspend fun getResult(wish: DashboardWish): Flow<DashboardResult> = flow {
        val status = usecase.execute()
        emit(status)
    }.map {
        DashboardResult.UpdateStatusFound(it)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
}

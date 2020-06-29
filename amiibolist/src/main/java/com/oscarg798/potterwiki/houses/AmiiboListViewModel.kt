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

package com.oscarg798.potterwiki.house

import androidx.lifecycle.viewModelScope
import com.oscarg798.potterwiki.core.CoroutineContextProvider
import com.oscarg798.potterwiki.core.base.AbstractViewModel
import com.oscarg798.potterwiki.core.models.AmiiboType
import com.oscarg798.potterwiki.core.mvi.ViewState
import com.oscarg798.potterwiki.houses.AmiiboListViewState
import com.oscarg798.potterwiki.houses.ViewAmiiboType
import com.oscarg798.potterwiki.houses.mvi.AmiiboListFailure
import com.oscarg798.potterwiki.houses.mvi.AmiiboListResult
import com.oscarg798.potterwiki.houses.mvi.AmiiboListWish
import com.oscarg798.potterwiki.houses.usecases.GetAmiiboFilteredUseCase
import com.oscarg798.potterwiki.houses.usecases.GetAmiiboTypeUseCase
import com.oscarg798.potterwiki.houses.usecases.GetAmiibosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.handleCoroutineException
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AmiiboListViewModel @Inject constructor(
    private val getAmiibosUseCase: GetAmiibosUseCase,
    private val getAmiiboFilteredUseCase: GetAmiiboFilteredUseCase,
    private val getAmiiboTypeUseCase: GetAmiiboTypeUseCase,
    private val coroutinesContextProvider: CoroutineContextProvider
) :
    AbstractViewModel<AmiiboListWish, AmiiboListResult>() {

    override fun initState(): ViewState<AmiiboListResult> = AmiiboListViewState.init()

    init {
        process()
            .launchIn(viewModelScope)
    }

    override fun process(): Flow<ViewState<AmiiboListResult>> = wishProcessor.asFlow()
        .flatMapMerge {
            when (it) {
                is AmiiboListWish.RefreshAmiibos,
                is AmiiboListWish.GetAmiibos -> fetchAmiibos()
                is AmiiboListWish.FilterAmiibos -> filterAmiibos(it.filter)
                is AmiiboListWish.ShowFilters -> getFilters()
            }

        }.scan(_state.value) { state, result ->
            state.reduce(result) as AmiiboListViewState
        }
        .onEach {
            _state.value = it
        }

    private fun getFilters(): Flow<AmiiboListResult> {
        return flow<AmiiboListResult> {
            getAmiiboTypeUseCase.getAmiiboType().map {
                emit(AmiiboListResult.FiltersFetched(it))
            }
                .onFailure { handleFailure(it) }
        }.flowOn(coroutinesContextProvider.backgroundDispatcher)
    }

    private fun filterAmiibos(filter: ViewAmiiboType): Flow<AmiiboListResult> {
        return flow<AmiiboListResult> {
            emit(AmiiboListResult.Loading)
            getAmiiboFilteredUseCase.execute(filter.map()).map {
                emit(AmiiboListResult.AmiibosFiltered(it))
            }.onFailure {
                handleFailure(it)
            }
        }.flowOn(coroutinesContextProvider.backgroundDispatcher)
    }

    private fun fetchAmiibos(): Flow<AmiiboListResult> {
        return flow<AmiiboListResult> {
            emit(AmiiboListResult.Loading)
            getAmiibosUseCase.execute().map {
                emit(AmiiboListResult.FetchSuccess(it))
            }.onFailure {
                handleFailure(it)
            }
        }.flowOn(coroutinesContextProvider.backgroundDispatcher)
    }

    private suspend fun FlowCollector<AmiiboListResult>.handleFailure(failure: Throwable) {
        emit(AmiiboListResult.Error(AmiiboListFailure.FetchError(failure.message!!)))
    }
}
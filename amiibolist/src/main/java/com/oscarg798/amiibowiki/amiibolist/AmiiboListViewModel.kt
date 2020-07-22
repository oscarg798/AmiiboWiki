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

package com.oscarg798.amiibowiki

import com.oscarg798.amiibowiki.amiibolist.AmiiboListLogger
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewState
import com.oscarg798.amiibowiki.amiibolist.ViewAmiiboType
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListFailure
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListResult
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiiboFilteredUseCase
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiibosUseCase
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@FlowPreview
@ExperimentalCoroutinesApi
class AmiiboListViewModel @Inject constructor(
    private val getAmiibosUseCase: GetAmiibosUseCase,
    private val getAmiiboFilteredUseCase: GetAmiiboFilteredUseCase,
    private val getAmiiboTypeUseCase: GetAmiiboTypeUseCase,
    private val amiiboListLogger: AmiiboListLogger,
    private val coroutinesContextProvider: CoroutineContextProvider
) :
    AbstractViewModel<AmiiboListWish, AmiiboListResult, AmiiboListViewState>(AmiiboListViewState.init()) {

    override fun onScreenShown() {
        amiiboListLogger.trackScreenViewed()
    }

    override suspend fun getResult(wish: AmiiboListWish): Flow<AmiiboListResult> {
        return when (wish) {
            is AmiiboListWish.RefreshAmiibos,
            is AmiiboListWish.GetAmiibos -> fetchAmiibos()
            is AmiiboListWish.FilterAmiibos -> {
                trackFilterApplied(wish)
                filterAmiibos(wish.filter)
            }
            is AmiiboListWish.ShowFilters -> {
                trackShowFiltersWish()
                getFilters()
            }
            is AmiiboListWish.ShowAmiiboDetail -> {
                trackShowDetailsAmiiboWish(wish)
                showDetail(wish.viewAmiibo.tail)
            }
        }
    }

    private fun trackFilterApplied(wish: AmiiboListWish.FilterAmiibos) {
        amiiboListLogger.trackFilterApplied(mapOf(APPLIED_FILTER_PROPERTY to wish.filter.name))
    }

    private fun trackShowFiltersWish() {
        amiiboListLogger.trackShownFiltersClicked()
    }

    private fun trackShowDetailsAmiiboWish(wish: AmiiboListWish.ShowAmiiboDetail) {
        amiiboListLogger.trackAmiiboClicked(
            mapOf(
                TAIL_TRACKING_PROPERTY to wish.viewAmiibo.tail,
                NAME_TRACKING_PROPERTY to wish.viewAmiibo.name,
                GAME_SERIES_TRACKING_PROPERTY to wish.viewAmiibo.serie
            )
        )
    }

    private fun showDetail(tail: String) = flowOf(AmiiboListResult.ShowAmiiboDetail(tail))

    private fun getFilters(): Flow<AmiiboListResult> = getAmiiboTypeUseCase.execute()
        .map {
            AmiiboListResult.FiltersFetched(it) as AmiiboListResult
        }.catch { cause ->
            handleFailure(cause)
        }
        .flowOn(coroutinesContextProvider.backgroundDispatcher)

    private suspend fun filterAmiibos(filter: ViewAmiiboType) =
        getAmiiboFilteredUseCase.execute(filter.map()).map {
            AmiiboListResult.AmiibosFiltered(it) as AmiiboListResult
        }.onStart {
            emit(AmiiboListResult.Loading)
        }.catch { cause ->
            handleFailure(cause)
        }.flowOn(coroutinesContextProvider.backgroundDispatcher)

    private suspend fun fetchAmiibos(): Flow<AmiiboListResult> = getAmiibosUseCase.execute().map {
        AmiiboListResult.FetchSuccess(it) as AmiiboListResult
    }.onStart {
        emit(AmiiboListResult.Loading)
    }.catch { cause ->
        handleFailure(cause)
    }.flowOn(coroutinesContextProvider.backgroundDispatcher)

    private suspend fun FlowCollector<AmiiboListResult>.handleFailure(failure: Throwable) {
        if (failure !is Exception) {
            throw failure
        }

        emit(AmiiboListResult.Error(AmiiboListFailure.FetchError(failure.message!!)))
    }
}

private const val APPLIED_FILTER_PROPERTY = "APPLIED_AMIIBO_TYPE_FILTER"
private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"

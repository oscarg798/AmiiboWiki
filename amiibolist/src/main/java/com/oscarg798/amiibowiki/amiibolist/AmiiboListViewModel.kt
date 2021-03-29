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

package com.oscarg798.amiibowiki.amiibolist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.amiibolist.exceptions.AmiiboListFailure
import com.oscarg798.amiibowiki.amiibolist.logger.AmiiboListLogger
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.amiibolist.mvi.UiEffect
import com.oscarg798.amiibowiki.amiibolist.mvi.ViewState
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiiboFilteredUseCase
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiibosUseCase
import com.oscarg798.amiibowiki.amiibolist.usecases.SearchAmiibosUseCase
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
internal class AmiiboListViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val getAmiibosUseCase: GetAmiibosUseCase,
    private val getAmiiboFilteredUseCase: GetAmiiboFilteredUseCase,
    private val getAmiiboTypeUseCase: GetAmiiboTypeUseCase,
    private val searchAmiibosUseCase: SearchAmiibosUseCase,
    private val amiiboListLogger: AmiiboListLogger,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<ViewState, UiEffect, AmiiboListWish>(ViewState()) {

    init {
        _state.onEach {
            stateHandle.set(STATE_KEY, it)
        }.launchIn(viewModelScope)
        processWish(AmiiboListWish.GetAmiibos)
    }

    override fun processWish(wish: AmiiboListWish) {
        when (wish) {
            is AmiiboListWish.RefreshAmiibos,
            is AmiiboListWish.GetAmiibos -> fetchAmiibos(wish is AmiiboListWish.RefreshAmiibos)
            is AmiiboListWish.FilterAmiibos -> filterAmiibos(wish.filter)
            is AmiiboListWish.Search -> searchAmiibos(wish.query)
            is AmiiboListWish.ShowFilters -> getFilters()
            is AmiiboListWish.ShowAmiiboDetail -> showDetail(wish.viewAmiibo)
        }
    }

    private fun searchAmiibos(query: String) {
        searchAmiibosUseCase.execute(query)
            .map(::onAmiibosFound)
            .onStart { updateStateToLoading() }
            .catch { cause -> handleFailure(cause) }
            .flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)
    }

    private fun getFilters() {
        trackShowFiltersWish()
        viewModelScope.launch {
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    getAmiiboTypeUseCase.execute()
                }
            }.fold(
                { amiiboTypes ->
                    _uiEffect.emit(UiEffect.ShowFilters(amiiboTypes.map { ViewAmiiboType(it) }))
                },
                { handleFailure(it) }
            )
        }
    }

    private fun filterAmiibos(filter: ViewAmiiboType) {
        trackFilterApplied(filter.name)
        viewModelScope.launch {
            updateStateToLoading()
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    getAmiiboFilteredUseCase.execute(filter.toAmiiboType())
                }
            }.fold({ onAmiibosFound(it) }, { handleFailure(it) })
        }
    }

    private fun fetchAmiibos(isRefreshing: Boolean) {
        val state = stateHandle.get<ViewState>(STATE_KEY)

        if (!isRefreshing && state?.amiibos != null) {
            viewModelScope.launch { updateState { it.copy(amiibos = state.amiibos) } }
        } else {
            getAmiibosUseCase.execute()
                .map(::onAmiibosFound)
                .onStart { updateStateToLoading() }
                .catch { cause -> handleFailure(cause) }
                .flowOn(coroutineContextProvider.backgroundDispatcher)
                .launchIn(viewModelScope)
        }
    }

    private suspend fun updateStateToLoading() {
        updateState { it.copy(loading = true) }
    }

    private fun trackFilterApplied(filter: String) {
        amiiboListLogger.trackFilterApplied(mapOf(APPLIED_FILTER_PROPERTY to filter))
    }

    private fun trackShowFiltersWish() {
        amiiboListLogger.trackShownFiltersClicked()
    }

    private fun trackShowDetailsAmiiboWish(amiibo: ViewAmiibo) {
        amiiboListLogger.trackAmiiboClicked(
            mapOf(
                TAIL_TRACKING_PROPERTY to amiibo.tail,
                NAME_TRACKING_PROPERTY to amiibo.name,
                GAME_SERIES_TRACKING_PROPERTY to amiibo.serie
            )
        )
    }

    private fun showDetail(amiibo: ViewAmiibo) {
        trackShowDetailsAmiiboWish(amiibo)
        if (isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowAmiiboDetail)) {
            _uiEffect.tryEmit(UiEffect.ShowAmiiboDetails(amiibo.tail))
        }
    }

    private suspend fun onAmiibosFound(amiibos: Collection<Amiibo>) {
        updateState { state ->
            state.copy(loading = false, amiibos = amiibos.map { ViewAmiibo(it) }, error = null)
        }
    }

    private suspend fun handleFailure(failure: Throwable) {
        if (failure !is Exception) {
            throw failure
        }

        amiiboListLogger.logCrash(failure)

        when (failure) {
            is AmiiboListFailure -> updateState { it.copy(loading = false, error = failure) }
            else -> updateState {
                it.copy(
                    loading = false,
                    error = AmiiboListFailure.UnknowError(failure)
                )
            }
        }
    }
}

private const val STATE_KEY = "state"
private const val APPLIED_FILTER_PROPERTY = "APPLIED_AMIIBO_TYPE_FILTER"
private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"

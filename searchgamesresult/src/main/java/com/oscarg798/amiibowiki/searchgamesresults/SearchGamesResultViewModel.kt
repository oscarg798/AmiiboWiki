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

package com.oscarg798.amiibowiki.searchgamesresults

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.searchgamesresults.logger.SearchGamesResultLogger
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import com.oscarg798.amiibowiki.searchgamesresults.mvi.UIEffect
import com.oscarg798.amiibowiki.searchgamesresults.mvi.ViewState
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGameResult
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByAmiiboUseCase
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByQueryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchGamesResultViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    @Assisted private val shownAsRelatedGames: Boolean,
    private val searchGamesByAmiiboUseCase: SearchGamesByAmiiboUseCase,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    private val searchGamesByQueryUseCase: SearchGamesByQueryUseCase,
    private val searchGamesLogger: SearchGamesResultLogger,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<ViewState, UIEffect, SearchResultWish>(ViewState()) {

    init {
        state.onEach {
            handle.set(STATE_KEY, it)
        }.launchIn(viewModelScope)

        if (!shownAsRelatedGames) {
            _uiEffect.tryEmit(UIEffect.ObserveSearchResults)
        }
    }

    override fun processWish(wish: SearchResultWish) {
        when {
            wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.AmiiboGameSearchParam -> searchByAmiiboId(
                wish.gameSearchGameQueryParam.amiiboId
            )

            wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.StringQueryGameSearchParam -> searchByQuery(
                wish.gameSearchGameQueryParam.query
            )

            wish is SearchResultWish.ShowGameDetail -> getShowGamesFlow(wish)
        }
    }

    private fun getShowGamesFlow(wish: SearchResultWish.ShowGameDetail) {
        viewModelScope.launch {
            trackSearchResultClick(wish.gameId)
            val canBeShown = withContext(coroutineContextProvider.backgroundDispatcher) {
                isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowGameDetail)
            }

            if (canBeShown) {
                _uiEffect.tryEmit(UIEffect.ShowGameDetails(wish.gameId))
            }
        }
    }

    private fun searchByQuery(query: String) {
        viewModelScope.launch {
            showLoading()

            val result = withContext(coroutineContextProvider.backgroundDispatcher) {
                searchGamesByQueryUseCase.execute(query)
            }

            when (result) {
                is SearchGameResult.Allowed -> subscribeToSearch(result)
                is SearchGameResult.NotAllowed -> updateState {
                    it.copy(
                        idling = true,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun subscribeToSearch(
        result: SearchGameResult.Allowed
    ) {
        result.flow.onEach { results ->
            updateState {
                it.copy(
                    isLoading = false,
                    idling = false,
                    error = null,
                    gamesResult = results.map { searchResult ->
                        ViewGameSearchResult(
                            searchResult
                        )
                    }
                )
            }
        }.onStart {
            showLoading()
        }.catch { cause ->
            handleFailure(cause)
        }.flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)
    }

    private fun searchByAmiiboId(amiiboId: String) {
        val savedState = handle.get<ViewState>(STATE_KEY)

        if (savedState?.gamesResult != null) {
            updateFromSavedState(savedState)
            return
        }

        searchGamesByAmiiboUseCase.execute(amiiboId)
            .onEach { results ->
                updateState {
                    it.copy(
                        isLoading = false,
                        error = null,
                        gamesResult = results.map { searchResult ->
                            ViewGameSearchResult(
                                searchResult
                            )
                        }
                    )
                }
            }
            .onStart {
                showLoading()
            }.catch { cause ->
                handleFailure(cause)
            }.flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)
    }

    private suspend fun showLoading() {
        updateState { it.copy(isLoading = true, error = null, idling = false) }
    }

    private fun updateFromSavedState(savedState: ViewState) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = false,
                    idling = false,
                    error = null,
                    gamesResult = savedState.gamesResult
                )
            }
        }
    }

    private suspend fun handleFailure(
        cause: Throwable
    ) {
        if (cause !is SearchGameFailure) {
            throw cause
        }

        searchGamesLogger.logCrash(cause)
        updateState { it.copy(isLoading = false, error = cause, idling = false) }
    }

    private fun trackSearchResultClick(gameId: Int) {
        searchGamesLogger.trackGameSearchResultClicked(mapOf(GAME_ID_KEY to gameId.toString()))
    }

    @AssistedFactory
    interface Factory {

        fun create(
            shownAsRelatedGames: Boolean,
            stateHandle: SavedStateHandle
        ): SearchGamesResultViewModel
    }
}

private const val STATE_KEY = "state"
private const val GAME_ID_KEY = "GAME_ID_KEY"

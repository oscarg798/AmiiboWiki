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
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultViewState
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import com.oscarg798.amiibowiki.searchgamesresults.mvi.UIEffect
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByAmiiboUseCase
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByQueryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchGamesResultViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val searchGamesByAmiiboUseCase: SearchGamesByAmiiboUseCase,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    private val searchGamesByQueryUseCase: SearchGamesByQueryUseCase,
    private val searchGamesLogger: SearchGamesResultLogger,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<SearchResultViewState, UIEffect, SearchResultWish>(SearchResultViewState()) {

    init {
        state.onEach {
            handle.set(STATE_KEY, it)
        }.launchIn(viewModelScope)
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
                _uiEffect.value = UIEffect.ShowGameDetails(wish.gameId)
            }
        }
    }

    private fun searchByQuery(query: String) =
        searchGamesByQueryUseCase.execute(query)
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
                updateState { it.copy(isLoading = true, error = null) }
            }.catch { cause ->
                handleFailure(cause)
            }.flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)

    private fun searchByAmiiboId(amiiboId: String) {
        val savedState = handle.get<SearchResultViewState>(STATE_KEY)
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
                updateState { it.copy(isLoading = true, error = null) }
            }.catch { cause ->
                handleFailure(cause)
            }.flowOn(coroutineContextProvider.backgroundDispatcher)
            .launchIn(viewModelScope)
    }

    private fun updateFromSavedState(savedState: SearchResultViewState) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = false,
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

        updateState { it.copy(isLoading = false, error = cause) }
    }

    private fun trackSearchResultClick(gameId: Int) {
        searchGamesLogger.trackGameSearchResultClicked(mapOf(GAME_ID_KEY to gameId.toString()))
    }

    @AssistedFactory
    interface Factory {

        fun create(stateHandle: SavedStateHandle): SearchGamesResultViewModel
    }
}

private const val STATE_KEY = "state"
private const val GAME_ID_KEY = "GAME_ID_KEY"

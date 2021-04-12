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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SearchGamesResultViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val searchGamesByAmiiboUseCase: SearchGamesByAmiiboUseCase,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    private val searchGamesByQueryUseCase: SearchGamesByQueryUseCase,
    private val searchGamesLogger: SearchGamesResultLogger,
    override val coroutineContextProvider: CoroutineContextProvider,
    /**
     * Workaround to test the search, I tried with runBlockingTest and advancing
     * but does not work
     */
    private val searchDebounce: Long
) : AbstractViewModel<ViewState, UIEffect, SearchResultWish>(ViewState()) {

    val _searchFlow: MutableStateFlow<String> = MutableStateFlow(INITIAL_QUERY)

    val searchFlow: StateFlow<String> = _searchFlow

    init {
        cacheState()
        verifyCachedQuery()
    }

    private fun verifyCachedQuery() {
        if (handle.contains(QUERY_KEY)) {
            _searchFlow.value = handle.get<String>(QUERY_KEY)!!
        }
    }

    private fun cacheState() {
        state.onEach {
            handle.set(STATE_KEY, it)
        }.launchIn(viewModelScope)
    }

    override fun processWish(wish: SearchResultWish) {
        when {
            wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.AmiiboGameSearchParam -> searchByAmiiboId(
                wish.gameSearchGameQueryParam.amiiboId
            )

            wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.StringQueryGameSearchParam ->
                _searchFlow.value =
                    wish.gameSearchGameQueryParam.query

            wish is SearchResultWish.ShowGameDetail -> getShowGamesFlow(wish)
            wish is SearchResultWish.Init -> {
                if (wish.searchBoxEnabled) {
                    observeSearchFlow()
                }

                _uiEffect.tryEmit(UIEffect.InitializationCompleted)
            }
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

    private fun observeSearchFlow() {
        _searchFlow.debounce(searchDebounce)
            .onEach { query ->
                val currentState = _state.replayCache.firstOrNull()
                if (currentStateContainsResultsForQuery(
                        currentState = currentState,
                        query = query
                    )
                ) {
                    return@onEach
                }

                search(query)
            }.launchIn(viewModelScope)
    }

    private fun currentStateContainsResultsForQuery(
        currentState: ViewState?,
        query: String
    ) = currentState?.currentQuery == query && currentState.gamesResult != null

    private fun search(query: String) {
        handle.set(QUERY_KEY, query)
        viewModelScope.launch {
            showLoading()
            updateState { it.copy(currentQuery = query) }

            val result = withContext(coroutineContextProvider.backgroundDispatcher) {
                searchGamesByQueryUseCase.execute(query)
            }

            when (result) {
                is SearchGameResult.Allowed -> {
                    subscribeToSearchUpdates(result)
                }
                is SearchGameResult.NotAllowed -> updateState {
                    it.copy(
                        idling = true,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun subscribeToSearchUpdates(
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
}

private const val STATE_KEY = "state"
private const val GAME_ID_KEY = "GAME_ID_KEY"
private const val QUERY_KEY = "query_key"
private const val INITIAL_QUERY = ""

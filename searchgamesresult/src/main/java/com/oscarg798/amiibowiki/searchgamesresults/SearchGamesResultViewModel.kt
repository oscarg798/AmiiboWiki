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

import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.searchgamesresults.logger.SearchGamesResultLogger
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultResult
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultViewState
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByAmiiboUseCase
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class SearchGamesResultViewModelCompat @Inject constructor(
    private val searchGamesByAmiiboUseCase: SearchGamesByAmiiboUseCase,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    private val searchGamesByQueryUseCase: SearchGamesByQueryUseCase,
    private val searchGamesLogger: SearchGamesResultLogger,
    override val reducer: Reducer<@JvmSuppressWildcards SearchResultResult, @JvmSuppressWildcards SearchResultViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<SearchResultWish, SearchResultResult, SearchResultViewState>(
    SearchResultViewState.Idling
) {
    override suspend fun getResult(wish: SearchResultWish): Flow<SearchResultResult> = when {
        wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.AmiiboGameSearchParam -> {
            getSearchGamesByAmiiboIdFlow(
                wish.gameSearchGameQueryParam.amiiboId
            )
        }
        wish is SearchResultWish.SearchGames && wish.gameSearchGameQueryParam is GameSearchParam.StringQueryGameSearchParam -> {
            getSearchGamesByQueryFlow(wish.gameSearchGameQueryParam.query)
        }
        wish is SearchResultWish.ShowGameDetail -> getShowGamesFlow(wish)
        else -> emptyFlow()
    }

    private fun getShowGamesFlow(wish: SearchResultWish.ShowGameDetail) = flow<SearchResultResult> {
        trackSearchResultClick(wish.gameId)
        if (isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowGameDetail)) {
            emit(SearchResultResult.ShowGameDetails(wish.gameId))
        } else {
            emit(SearchResultResult.None)
        }
    }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun getSearchGamesByQueryFlow(query: String) =
        searchGamesByQueryUseCase.execute(query).map {
            SearchResultResult.GamesFound(it) as SearchResultResult
        }.onStart {
            emit(SearchResultResult.Loading)
        }.catch { cause ->
            handleFailure(cause)
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private suspend fun getSearchGamesByAmiiboIdFlow(amiiboId: String) =
        searchGamesByAmiiboUseCase.execute(amiiboId).map {
            SearchResultResult.GamesFound(it) as SearchResultResult
        }.onStart {
            emit(SearchResultResult.Loading)
        }.catch { cause ->
            handleFailure(cause)
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private suspend fun FlowCollector<SearchResultResult>.handleFailure(
        cause: Throwable
    ) {
        if (cause !is SearchGameFailure) {
            throw cause
        }

        emit(SearchResultResult.Error(cause))
    }

    private fun trackSearchResultClick(gameId: Int) {
        searchGamesLogger.trackGameSearchResultClicked(mapOf(GAME_ID_KEY to gameId.toString()))
    }
}

private const val GAME_ID_KEY = "GAME_ID_KEY"

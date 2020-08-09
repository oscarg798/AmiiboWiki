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

package com.oscarg798.amiibowiki.amiibodetail

import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.amiibodetail.logger.AmiiboDetailLogger
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailResult
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailViewState
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.amiibodetail.usecase.SearchRelatedGamesUseCase
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class AmiiboDetailViewModel @Inject constructor(
    private val amiiboDetailTail: String,
    private val getAmiiboDetailUseCase: GetAmiiboDetailUseCase,
    private val searchRelatedGamesUseCase: SearchRelatedGamesUseCase,
    private val amiiboDetailLogger: AmiiboDetailLogger,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    override val reducer: Reducer<@JvmSuppressWildcards AmiiboDetailResult, @JvmSuppressWildcards AmiiboDetailViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<AmiiboDetailWish, AmiiboDetailResult, AmiiboDetailViewState>(
    AmiiboDetailViewState.init()
) {

    override suspend fun getResult(wish: AmiiboDetailWish): Flow<AmiiboDetailResult> = when (wish) {
        is AmiiboDetailWish.ShowAmiiboDetail -> getAmiiboDetail()
        is AmiiboDetailWish.ShowGameDetail -> onShowGameDetailWish(wish)
    }

    private fun onShowGameDetailWish(wish: AmiiboDetailWish.ShowGameDetail) =
        flow<AmiiboDetailResult> {
            trackGameResultClicked(wish.gameId)
            if (isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowGameDetail)) {
                emit(
                    AmiiboDetailResult.ShowGameDetails(
                        wish.gameId,
                        wish.gameSeries
                    )
                )
            } else {
                emit(AmiiboDetailResult.None)
            }
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private suspend fun getAmiiboDetail(): Flow<AmiiboDetailResult> = flow {
        emit(getAmiiboDetailUseCase.execute(amiiboDetailTail))
    }.flatMapConcat { amiibo ->
        trackViewShown(amiibo)

        val isRelatedGamesSectionEnabled =
            isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames)

        val searchResult = getRelatedGames(isRelatedGamesSectionEnabled, amiibo)
        flowOf(
            AmiiboDetailResult.DetailFetched(
                ViewAmiiboDetails(
                    amiibo,
                    searchResult
                ),
                isRelatedGamesSectionEnabled
            )
        ) as Flow<AmiiboDetailResult>
    }.catch { cause ->
        if (cause !is AmiiboDetailFailure.AmiiboNotFoundByTail && cause !is SearchGameFailure.DateSourceError) {
            throw cause
        }
        emit(AmiiboDetailResult.Error(cause as AmiiboDetailFailure))
    }.onStart {
        emit(AmiiboDetailResult.Loading)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private suspend fun getRelatedGames(
        isRelatedGamesSectionEnabled: Boolean,
        amiibo: Amiibo
    ): Collection<GameSearchResult> = if (isRelatedGamesSectionEnabled) {
        getRelatedGames(amiibo)
    } else {
        setOf()
    }

    private suspend fun getRelatedGames(amiibo: Amiibo) = searchRelatedGamesUseCase.execute(amiibo)

    private fun trackGameResultClicked(gameId: Int) {
        amiiboDetailLogger.trackGameSearchResultClicked(mapOf(GAME_ID to gameId.toString()))
    }

    private fun trackViewShown(amiibo: Amiibo) {
        amiiboDetailLogger.trackScreenShown(
            mapOf(
                TAIL_TRACKING_PROPERTY to amiibo.tail,
                HEAD_TRACKING_PROPERTY to amiibo.head,
                TYPE_TRACKING_PROPERTY to amiibo.type,
                NAME_TRACKING_PROPERTY to amiibo.name,
                GAME_SERIES_TRACKING_PROPERTY to amiibo.gameSeries
            )
        )
    }
}

private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"
private const val GAME_ID = "GAME_ID"

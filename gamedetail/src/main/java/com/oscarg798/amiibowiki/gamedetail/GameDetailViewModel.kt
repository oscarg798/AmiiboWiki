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

package com.oscarg798.amiibowiki.gamedetail

import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.gamedetail.logger.GameDetailLogger
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailResult
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class GameDetailViewModel @Inject constructor(
    private val getGameUseCase: GetGamesUseCase,
    private val gameDetailLogger: GameDetailLogger,
    private val coroutinesContextProvider: CoroutineContextProvider
) : AbstractViewModel<GameDetailWish, GameDetailResult, GameDetailViewState>(GameDetailViewState.init()) {

    private lateinit var game: Game

    override suspend fun getResult(wish: GameDetailWish): Flow<GameDetailResult> = when (wish) {
        is GameDetailWish.ShowGameDetail -> getGame(wish)
        is GameDetailWish.PlayGameTrailer -> getGameTrailer()
    }

    private fun getGameTrailer(): Flow<GameDetailResult> = flow {
        trackTrailerClick(game.id.toString())
        if (game.videosId.isNullOrEmpty()) {
            throw GameDetailFailure.GameDoesNotIncludeTrailer(game.id)
        }

        val trailerId =
            game.videosId?.firstOrNull()
                ?: throw GameDetailFailure.GameDoesNotIncludeTrailer(game.id)
        emit(GameDetailResult.GameTrailerFound(trailerId) as GameDetailResult)
    }.onStart {
        emit(GameDetailResult.Loading)
    }.flowOn(coroutinesContextProvider.backgroundDispatcher)

    private fun getGame(wish: GameDetailWish.ShowGameDetail) = flow<GameDetailResult> {
        trackScreenShown(wish)
        game = getGameUseCase.execute(wish.gameSeries, wish.gameId)
        emit(GameDetailResult.GameFetched(game))
    }.onStart {
        emit(GameDetailResult.Loading)
    }.catch { cause ->
        if (cause !is GameDetailFailure) {
            throw cause
        }

        emit(GameDetailResult.Error(cause))
    }.flowOn(coroutinesContextProvider.backgroundDispatcher)

    private fun trackScreenShown(wish: GameDetailWish.ShowGameDetail) {
        gameDetailLogger.trackScreenShown(mapOf(GAME_ID_PROPERTY_NAME to wish.gameId.toString()))
    }

    private fun trackTrailerClick(gameId: String) {
        gameDetailLogger.trackTrailerClicked(mapOf(GAME_ID_PROPERTY_NAME to gameId))
    }
}

private const val GAME_ID_PROPERTY_NAME = "GAME_ID"

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

import com.oscarg798.amiibowiki.core.base.AbstractViewModelCompat
import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.gamedetail.di.GameId
import com.oscarg798.amiibowiki.gamedetail.logger.GameDetailLogger
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailResult
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.usecases.ExpandGameImagesUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGameTrailerUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class GameDetailViewModel @Inject constructor(
    @GameId
    private val gameId: Id,
    private val getGameUseCase: GetGamesUseCase,
    private val expandGameImagesUseCase: ExpandGameImagesUseCase,
    private val getGameTrailerUseCase: GetGameTrailerUseCase,
    private val gameDetailLogger: GameDetailLogger,
    override val reducer: Reducer<@JvmSuppressWildcards GameDetailResult, @JvmSuppressWildcards GameDetailViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModelCompat<GameDetailWish, GameDetailResult, GameDetailViewState>(
    GameDetailViewState.Idling
) {

    override suspend fun getResult(wish: GameDetailWish): Flow<GameDetailResult> = when (wish) {
        is GameDetailWish.ShowGameDetail -> getGame()
        is GameDetailWish.PlayGameTrailer -> getGameTrailer(wish)
        is GameDetailWish.ExpandImages -> expandImages(wish.expandableImageParams)
    }

    private fun expandImages(expandableImageParam: Collection<ExpandableImageParam>): Flow<GameDetailResult> =
        flow {
            val expandedImages = expandGameImagesUseCase.execute(expandableImageParam)
            emit(GameDetailResult.ImagesExpanded(expandedImages))
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun getGameTrailer(wish: GameDetailWish.PlayGameTrailer): Flow<GameDetailResult> =
        flow {
            trackTrailerClick(gameId)
            emit(GameDetailResult.GameTrailerFound(getGameTrailerUseCase.execute(gameId)) as GameDetailResult)
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun getGame() = flow<GameDetailResult> {
        trackScreenShown()
        emit(GameDetailResult.GameFetched(getGameUseCase.execute(gameId)))
    }.onStart {
        emit(GameDetailResult.Loading)
    }.catch { cause ->
        if (cause !is GameDetailFailure) {
            throw cause
        }

        emit(GameDetailResult.Error(cause))
    }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun trackScreenShown() {
        gameDetailLogger.trackScreenShown(mapOf(GAME_ID_PROPERTY_NAME to gameId.toString()))
    }

    private fun trackTrailerClick(gameId: Id) {
        gameDetailLogger.trackTrailerClicked(mapOf(GAME_ID_PROPERTY_NAME to gameId.toString()))
    }
}

private const val GAME_ID_PROPERTY_NAME = "GAME_ID"

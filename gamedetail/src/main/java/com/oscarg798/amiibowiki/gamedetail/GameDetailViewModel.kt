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

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.gamedetail.logger.GameDetailLogger
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.mvi.UiEffect
import com.oscarg798.amiibowiki.gamedetail.usecases.ExpandGameImagesUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGameTrailerUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

internal class GameDetailViewModel @AssistedInject constructor(
    @Assisted private val gameId: Id,
    private val getGameUseCase: GetGamesUseCase,
    private val expandGameImagesUseCase: ExpandGameImagesUseCase,
    private val getGameTrailerUseCase: GetGameTrailerUseCase,
    private val gameDetailLogger: GameDetailLogger,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<GameDetailViewState, UiEffect, GameDetailWish>(GameDetailViewState()) {

    override fun processWish(wish: GameDetailWish) {
        when (wish) {
            is GameDetailWish.ShowGameDetail -> getGame()
            is GameDetailWish.PlayGameTrailer -> getGameTrailer()
            is GameDetailWish.ExpandImages -> expandImages(wish.expandableImageParams)
        }
    }

    private fun expandImages(expandableImageParam: Collection<ExpandableImageParam>) = flow {
        val expandedImages = expandGameImagesUseCase.execute(expandableImageParam)
        emit(expandedImages)
    }.onEach { images ->
        _uiEffect.value = UiEffect.ShowingGameImages(images)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
        .launchIn(viewModelScope)

    private fun getGameTrailer() = flow {
        trackTrailerClick(gameId)
        emit(getGameTrailerUseCase.execute(gameId))
    }.onEach {
        _uiEffect.value = UiEffect.ShowingGameTrailer(it)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
        .launchIn(viewModelScope)

    private fun getGame() = flow {
        trackScreenShown()
        emit(getGameUseCase.execute(gameId))
    }.onStart {
        updateState { it.copy(loading = true, error = null) }
    }.onEach { game ->
        updateState { it.copy(loading = false, error = null, game = game) }
    }.catch { cause ->
        if (cause !is GameDetailFailure) {
            throw cause
        }
        updateState { it.copy(loading = false, error = cause) }
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
        .launchIn(viewModelScope)

    private fun trackScreenShown() {
        gameDetailLogger.trackScreenShown(mapOf(GAME_ID_PROPERTY_NAME to gameId.toString()))
    }

    private fun trackTrailerClick(gameId: Id) {
        gameDetailLogger.trackTrailerClicked(mapOf(GAME_ID_PROPERTY_NAME to gameId.toString()))
    }

    @AssistedFactory
    interface Factory {

        fun create(gameId: Id): GameDetailViewModel
    }
}

private const val GAME_ID_PROPERTY_NAME = "GAME_ID"

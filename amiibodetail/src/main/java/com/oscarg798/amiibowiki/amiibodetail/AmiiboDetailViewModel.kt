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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.amiibodetail.logger.AmiiboDetailLogger
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UiEffect
import com.oscarg798.amiibowiki.amiibodetail.mvi.ViewState
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
internal class AmiiboDetailViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val getAmiiboDetailUseCase: GetAmiiboDetailUseCase,
    private val amiiboDetailLogger: AmiiboDetailLogger,
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<ViewState, UiEffect, AmiiboDetailWish>(ViewState()) {

    private lateinit var tail: String

    init {
        state.onEach {
            handle.set(STATE_KEY, it)
        }.launchIn(viewModelScope)
    }

    override fun processWish(wish: AmiiboDetailWish) {
        when (wish) {
            is AmiiboDetailWish.ExpandAmiiboImage ->
                _uiEffect.tryEmit(UiEffect.ShowAmiiboImage(wish.image))

            is AmiiboDetailWish.ShowAmiiboDetail -> onShowDetailsRequest(wish.tail)
            is AmiiboDetailWish.ShowRelatedGames -> {
                _uiEffect.tryEmit(UiEffect.ShowRelatedGames(tail))
            }
        }
    }

    private fun onShowDetailsRequest(tail: String) {
        this.tail = tail
        viewModelScope.launch {
            updateState { it.copy(loading = true, error = null) }
            getDetailsAsync(tail)
            shouldShowRelatedGamesSectionAsync()
        }
    }

    private fun CoroutineScope.shouldShowRelatedGamesSectionAsync() = async {
        runCatching {
            withContext(coroutineContextProvider.backgroundDispatcher) {
                isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames)
            }
        }.fold(
            { relatedGamesEnabled ->
                updateState {
                    it.copy(
                        error = null,
                        relatedGamesSectionEnabled = relatedGamesEnabled
                    )
                }
            },
            { onError(it) }
        )
    }

    private fun CoroutineScope.getDetailsAsync(tail: String) = async {
        val savedState = handle.get<ViewState>(STATE_KEY)

        if (savedState?.amiibo != null) {
            updateState {
                it.copy(
                    loading = false,
                    error = null,
                    amiibo = savedState.amiibo
                )
            }
            return@async
        }

        runCatching {
            withContext(coroutineContextProvider.backgroundDispatcher) {
                getAmiiboDetailUseCase.execute(tail)
            }
        }.fold({ amiibo -> onAmiiboFound(amiibo) }, { onError(it) })
    }

    private suspend fun onAmiiboFound(amiibo: Amiibo) {
        trackViewShown(amiibo)

        updateState {
            it.copy(
                loading = false,
                error = null,
                amiibo = ViewAmiiboDetails(amiibo)
            )
        }
    }

    private suspend fun onError(exception: Throwable) {
        if (exception !is Exception) {
            throw exception
        }

        amiiboDetailLogger.onDetailCrash(exception)

        updateState { state ->
            state.copy(
                loading = false,
                error = when (exception) {
                    is AmiiboDetailFailure.AmiiboNotFoundByTail -> exception
                    else -> AmiiboDetailFailure.UnknowError(exception)
                }
            )
        }
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
//
//    @AssistedFactory
//    interface Factory {
//        fun create(params: String): AmiiboDetailViewModel
//    }
}

private const val STATE_KEY = "state"
private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"
private const val GAME_ID = "GAME_ID"

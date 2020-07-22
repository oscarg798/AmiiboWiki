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
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.models.Amiibo
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
class AmiiboDetailViewModel @Inject constructor(
    private val amiiboDetailTail: String,
    private val getAmiiboDetailUseCase: GetAmiiboDetailUseCase,
    private val amiiboDetailLogger: AmiiboDetailLogger,
    private val coroutinesContextProvider: CoroutineContextProvider
) : AbstractViewModel<AmiiboDetailWish, AmiiboDetailResult, AmiiboDetailViewState>(
    AmiiboDetailViewState.init()
) {

    override suspend fun getResult(wish: AmiiboDetailWish): Flow<AmiiboDetailResult> = getDetail().onEach {
        if (it is AmiiboDetailResult.DetailFetched) {
            trackViewShown(it.amiibo)
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

    private suspend fun getDetail(): Flow<AmiiboDetailResult> = flow {
        val result = getAmiiboDetailUseCase.execute(amiiboDetailTail)
        emit(AmiiboDetailResult.DetailFetched(result) as AmiiboDetailResult)
    }.catch { cause ->
        if (cause !is AmiiboDetailFailure.AmiiboNotFoundByTail) {
            throw cause
        }
        emit(AmiiboDetailResult.Error(cause))
    }.flowOn(coroutinesContextProvider.backgroundDispatcher)
}

private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"

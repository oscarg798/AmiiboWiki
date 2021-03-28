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

package com.oscarg798.amiibowiki.nfcreader

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.logger.NFCReaderLogger
import com.oscarg798.amiibowiki.nfcreader.mvi.ReadTagWish
import com.oscarg798.amiibowiki.nfcreader.mvi.ShowAmiiboDetailsUiEffect
import com.oscarg798.amiibowiki.nfcreader.mvi.ViewState
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class NFCReaderViewModel @Inject constructor(
    private val readTagUseCase: ReadTagUseCase,
    private val logger: NFCReaderLogger,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<ViewState, ShowAmiiboDetailsUiEffect, ReadTagWish>(ViewState()) {

    override fun processWish(wish: ReadTagWish) {
        readTag(wish)
    }

    private fun readTag(wish: ReadTagWish) {
        viewModelScope.launch {
            updateState { it.copy(loading = true, error = null) }
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    readTagUseCase.execute(wish.tag)
                }
            }.fold(
                { amiiboIdentifier ->
                    updateState { it.copy(loading = false, error = null) }
                    _uiEffect.emit(ShowAmiiboDetailsUiEffect(amiiboIdentifier))
                },
                { cause ->
                    if (cause !is Exception) {
                        throw cause
                    }
                    logger.logException(cause)

                    updateState {
                        it.copy(
                            loading = false,
                            error = when (cause) {
                                is NFCReaderFailure -> cause
                                else -> NFCReaderFailure.Unknow(
                                    cause
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}

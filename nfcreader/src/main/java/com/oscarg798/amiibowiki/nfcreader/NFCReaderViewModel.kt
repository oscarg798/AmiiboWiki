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

import com.oscarg798.amiibowiki.core.base.AbstractViewModelCompat
import com.oscarg798.amiibowiki.core.failures.GameAPIAuthenticationFailure
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.logger.NFCReaderLogger
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderResult
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderWish
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import com.oscarg798.amiibowiki.nfcreader.usecase.ValidateAdapterAvailabilityUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class NFCReaderViewModel @Inject constructor(
    private val validateAdapterAvailabilityUseCase: ValidateAdapterAvailabilityUseCase,
    private val readTagUseCase: ReadTagUseCase,
    override val reducer: Reducer<@JvmSuppressWildcards NFCReaderResult, @JvmSuppressWildcards NFCReaderViewState>,
    private val logger: NFCReaderLogger,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModelCompat<NFCReaderWish, NFCReaderResult,
    NFCReaderViewState>(NFCReaderViewState.Idling) {

    override suspend fun getResult(wish: NFCReaderWish): Flow<NFCReaderResult> {
        return when (wish) {
            is NFCReaderWish.Read -> readTag(wish)
            is NFCReaderWish.ValidateAdapterAvailability -> checkAdapterAvailability()
            is NFCReaderWish.StopAdapter -> flowOf(NFCReaderResult.AdapterStoped)
        }
    }

    private fun checkAdapterAvailability() = flowOf(validateAdapterAvailabilityUseCase.execute())
        .map { isAvailable ->
            if (isAvailable) {
                NFCReaderResult.AdapterReady
            } else {
                NFCReaderResult.AdapterDisabled
            }
        }
        .flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun readTag(wish: NFCReaderWish.Read): Flow<NFCReaderResult> = flow {
        val result = readTagUseCase.execute(wish.tag)
        emit(NFCReaderResult.ReadSuccessful(result) as NFCReaderResult)
    }.catch { cause ->

        if (cause !is NFCReaderFailure && cause !is GameAPIAuthenticationFailure) {
            throw cause
        }

        logger.logException(cause as Exception)

        emit(
            when (cause) {
                is NFCReaderFailure -> NFCReaderResult.Error(cause)
                is GameAPIAuthenticationFailure -> NFCReaderResult.Error(
                    NFCReaderFailure.Unknow(
                        cause
                    )
                )
                else -> throw cause
            }
        )
    }.onStart {
        emit(NFCReaderResult.Reading)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
}

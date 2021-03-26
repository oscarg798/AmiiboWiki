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

import android.nfc.Tag
import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.logger.NFCReaderLogger
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.ReadTagWish
import com.oscarg798.amiibowiki.nfcreader.mvi.ShowAmiiboDetailsUiEffect
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@InternalCoroutinesApi

internal class NFCReaderViewModelTest :
    ViewModelTestRule.ViewModelCreator<NFCReaderViewState, NFCReaderViewModel> {

    @get: Rule
    val viewModelTestRule = ViewModelTestRule(this)

    private val readTagUseCase = mockk<ReadTagUseCase>()
    private val tag = mockk<Tag>()
    private val logger = relaxedMockk<NFCReaderLogger>()

    @Before
    fun setup() {
        coEvery { readTagUseCase.execute(tag) } answers { AMIIBO_IDENTIFIER }
    }

    override fun create(): NFCReaderViewModel = NFCReaderViewModel(
        readTagUseCase,
        logger,
        viewModelTestRule.coroutineContextProvider
    )

    @Test
    fun `given read tag wish when wish is process then it should return the amiibo identifier in status NFCReaderResult_ReadSuccessful`() {
        viewModelTestRule.viewModel.onWish(ReadTagWish(tag))

        val initialState = NFCReaderViewState()

        viewModelTestRule.stateCollector wereValuesEmitted listOf(
            initialState,
            initialState.copy(loading = true),
            initialState
        )

        viewModelTestRule.effectCollector.wereValuesEmitted(
            listOf(
                ShowAmiiboDetailsUiEffect(
                    AMIIBO_IDENTIFIER
                )
            )
        ) { o1, o2 ->
            if (o1.amiiboIdentifier == o2.amiiboIdentifier) {
                EQUALS
            } else {
                DIFFERENT
            }

        }

        coVerify {
            readTagUseCase.execute(tag)
        }
    }

    @Test
    fun `given read tag wish when wish is process and there is an NFCReader Failure then  it should return it in status as error`() {
        val error = NFCReaderFailure.TagNotSupported(Exception())
        coEvery { readTagUseCase.execute(tag) } answers { throw error }
        viewModelTestRule.viewModel.onWish(ReadTagWish(tag))

        val initialState = NFCReaderViewState()
        viewModelTestRule.stateCollector wereValuesEmitted listOf(
            initialState,
            initialState.copy(loading = true),
            initialState.copy(loading = false, error = error)
        )

        coVerify {
            readTagUseCase.execute(tag)
            logger.logException(error)
        }
    }
}

private const val EQUALS = 0
private const val DIFFERENT = 0
private val AMIIBO_IDENTIFIER = AmiiboIdentifier("1", "2")

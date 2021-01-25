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
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderResult
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderWish
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReducer
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import com.oscarg798.amiibowiki.nfcreader.usecase.ValidateAdapterAvailabilityUseCase
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@InternalCoroutinesApi

class NFCReaderViewModelTest :
    ViewModelTestRule.ViewModelCreator<NFCReaderViewState, NFCReaderViewModel> {

    @get: Rule
    val viewModelTestRule = ViewModelTestRule<NFCReaderViewState, NFCReaderViewModel>(this)

    private val validateAdapterAvailabilityUseCase = mockk<ValidateAdapterAvailabilityUseCase>()
    private val readTagUseCase = mockk<ReadTagUseCase>()
    private val tag = mockk<Tag>()
    private val reducer = spyk(NFCReducer())

    @Before
    fun setup() {
        every { validateAdapterAvailabilityUseCase.execute() } answers { true }
        every { readTagUseCase.execute(tag) } answers { AMIIBO_IDENTIFIER }
    }

    override fun create(): NFCReaderViewModel = NFCReaderViewModel(
        validateAdapterAvailabilityUseCase,
        readTagUseCase,
        reducer,
        viewModelTestRule.coroutineContextProvider
    )

    @Test
    fun `given validate adapter availability wish and nfc adapter is enabled when wish is process then is should return the status will be reflect this`() {
        viewModelTestRule.viewModel.onWish(NFCReaderWish.ValidateAdapterAvailability)

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            NFCReaderViewState.Idling,
            NFCReaderViewState.AdapterStatusFound(NFCReaderViewState.AdapterStatus.AdapterAvailable)
        )

        verify {
            validateAdapterAvailabilityUseCase.execute()
        }
        coVerify(exactly = 1) {
            reducer.reduce(
                NFCReaderViewState.Idling,
                NFCReaderResult.AdapterReady
            )
        }
    }

    @Test
    fun `given validate adapter availability wish and nfc adapter is disabled when wish is process then is should return the status will be reflect this`() {
        every { validateAdapterAvailabilityUseCase.execute() } answers { false }
        viewModelTestRule.viewModel.onWish(NFCReaderWish.ValidateAdapterAvailability)

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            NFCReaderViewState.Idling,
            NFCReaderViewState.AdapterStatusFound(NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped)
        )

        coVerify {
            validateAdapterAvailabilityUseCase.execute()
            reducer.reduce(
                NFCReaderViewState.Idling,
                NFCReaderResult.AdapterDisabled
            )
        }
    }

    @Test
    fun `given stop adapter wish when wish is process then it should return the state that reflect this in the adapter status`() {
        viewModelTestRule.viewModel.onWish(NFCReaderWish.StopAdapter)

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            NFCReaderViewState.Idling,
            NFCReaderViewState.AdapterStatusFound(NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped)
        )

        coVerify {
            reducer.reduce(
                NFCReaderViewState.Idling,
                NFCReaderResult.AdapterStoped
            )
        }
    }

    @Test
    fun `given read tag wish when wish is process then it should return the amiibo identifier in status NFCReaderResult_ReadSuccessful`() {
        viewModelTestRule.viewModel.onWish(NFCReaderWish.Read(tag))

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            NFCReaderViewState.Idling,
            NFCReaderViewState.Loading,
            NFCReaderViewState.ShowingAmiibo(AMIIBO_IDENTIFIER)
        )

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given read tag wish when wish is process and there is an NFCReader Failure then  it should return it in status as error`() {
        val error = NFCReaderFailure.TagNotSupported(Exception())
        every { readTagUseCase.execute(tag) } answers { throw error }
        viewModelTestRule.viewModel.onWish(NFCReaderWish.Read(tag))

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            NFCReaderViewState.Idling,
            NFCReaderViewState.Loading,
            NFCReaderViewState.Error(error)
        )

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }
}

private val AMIIBO_IDENTIFIER =
    AmiiboIdentifier("1", "2")

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
import com.oscarg798.amiibowiki.core.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderWish
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import com.oscarg798.amiibowiki.nfcreader.usecase.ValidateAdapterAvailabilityUseCase
import com.oscarg798.amiibowiki.testutils.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.TestCollector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@InternalCoroutinesApi
@FlowPreview
@ExperimentalCoroutinesApi
class NFCReaderViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val validateAdapterAvailabilityUseCase = mockk<ValidateAdapterAvailabilityUseCase>()
    private val readTagUseCase = mockk<ReadTagUseCase>()
    private val tag = mockk<Tag>()
    private lateinit var viewModel: NFCReaderViewModel
    private lateinit var testCollector: TestCollector<NFCReaderViewState>

    @Before
    fun setup() {
        every { validateAdapterAvailabilityUseCase.execute() } answers { true }
        every { readTagUseCase.execute(tag) } answers { AMIIBO_IDENTIFIER }

        testCollector = TestCollector()
        viewModel = NFCReaderViewModel(
            validateAdapterAvailabilityUseCase,
            readTagUseCase,
            coroutinesRule.coroutineContextProvider
        )
    }

    @Test
    fun `given validate adapter availability wish and nfc adapter is enabled when wish is process then is should return the status will be reflect this`() {
        viewModel.onWish(NFCReaderWish.ValidateAdapterAvailability)

        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.AdapterAvailable,
                null
            )
        )
        verify {
            validateAdapterAvailabilityUseCase.execute()
        }
    }

    @Test
    fun `given validate adapter availability wish and nfc adapter is disabled when wish is process then is should return the status will be reflect this`() {
        every { validateAdapterAvailabilityUseCase.execute() } answers { false }
        viewModel.onWish(NFCReaderWish.ValidateAdapterAvailability)

        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                NFCReaderFailure.AdapterDisabled
            )
        )
        verify {
            validateAdapterAvailabilityUseCase.execute()
        }
    }

    @Test
    fun `given stop adapter wish when wish is process then it should return the state that reflect this in the adapter status`() {
        viewModel.onWish(NFCReaderWish.StopAdapter)

        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped,
                null
            )
        )
    }

    @Test
    fun `given read tag wish when wish is process then it should return the amiibo identifier in status NFCReaderResult_ReadSuccessful`() {
        viewModel.onWish(NFCReaderWish.Read(tag))

        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.Loading,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.ReadSuccessful(AMIIBO_IDENTIFIER),
                NFCReaderViewState.AdapterStatus.Idle,
                null
            )
        )
    }

    @Test
    fun `given read tag wish when wish is process and there is an NFCReader Failure then  it should return it in status as error`() {
        val error = NFCReaderFailure.TagNotSupported(Exception())
        every { readTagUseCase.execute(tag) } answers { throw  error }
        viewModel.onWish(NFCReaderWish.Read(tag))

        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.Loading,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                null
            ),
            NFCReaderViewState(
                ViewState.LoadingState.None,
                NFCReaderViewState.Status.None,
                NFCReaderViewState.AdapterStatus.Idle,
                error
            )
        )
    }
}

private val AMIIBO_IDENTIFIER = AmiiboIdentifier("1", "2")
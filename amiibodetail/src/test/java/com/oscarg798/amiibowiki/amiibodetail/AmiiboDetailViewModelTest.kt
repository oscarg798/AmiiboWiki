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

import androidx.lifecycle.viewModelScope
import com.ibm.icu.impl.Assert.fail
import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.testutils.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.TestCollector
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class AmiiboDetailViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val getAmiiboDetailUseCase = mockk<GetAmiiboDetailUseCase>()
    private lateinit var viewModel: AmiiboDetailViewModel
    private lateinit var testCollector: TestCollector<AmiiboDetailViewState>

    @Before
    fun setup() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } answers { AMIIBO }
        testCollector = TestCollector()
        viewModel = AmiiboDetailViewModel(
            TAIL,
            getAmiiboDetailUseCase,
            coroutinesRule.coroutineContextProvider
        )
    }

    @Test
    fun `given ShowDetail wish when view model process it then it should update the state with the amiibo result`() {
        viewModel.onWish(AmiiboDetailWish.ShowDetail)
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            AmiiboDetailViewState(
                status = AmiiboDetailViewState.Status.None,
                error = null
            ),
            AmiiboDetailViewState(
                status = AmiiboDetailViewState.Status.ShowingDetail(AMIIBO),
                error = null
            )
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test
    fun `given ShowDetail wish when view model process and there is an AmiiboNotFoundByTail failure it then it should update the state with the error`() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws AmiiboDetailFailure.AmiiboNotFoundByTail(
            TAIL
        )
        viewModel.onWish(AmiiboDetailWish.ShowDetail)
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.assertValues(
            AmiiboDetailViewState(
                status = AmiiboDetailViewState.Status.None,
                error = null
            ),
            AmiiboDetailViewState(
                status = AmiiboDetailViewState.Status.None,
                error = AmiiboDetailFailure.AmiiboNotFoundByTail(TAIL)
            )
        )
        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test(expected = NullPointerException::class)
    fun `given ShowDetail wish when view model process and there is an Exception failure it then it should throw it`() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws NullPointerException()
        runBlockingTest {
            viewModel.onWish(AmiiboDetailWish.ShowDetail)
            viewModel.state.launchIn(this).cancelAndJoin()
        }
    }
}

private val AMIIBO = Amiibo(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    AmiiboReleaseDate("7", "8", "9", "10"),
    "11", "12"
)
private const val TAIL = "1"
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
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class AmiiboDetailViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val logger = relaxedMockk<AmiiboDetailLogger>()
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
            logger,
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

    @Test
    fun `when amiibo detail view state is emitted  then it should track the view as shown with the properties`() {
        viewModel.onWish(AmiiboDetailWish.ShowDetail)
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        verify {
            logger.trackScreenShown(
                mapOf(
                    TAIL_TRACKING_PROPERTY to AMIIBO.tail,
                    HEAD_TRACKING_PROPERTY to AMIIBO.head,
                    TYPE_TRACKING_PROPERTY to AMIIBO.type,
                    NAME_TRACKING_PROPERTY to AMIIBO.name,
                    GAME_SERIES_TRACKING_PROPERTY to AMIIBO.gameSeries
                )
            )
        }
    }
}

private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"
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

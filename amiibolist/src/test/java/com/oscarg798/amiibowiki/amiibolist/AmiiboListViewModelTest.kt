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

package com.oscarg798.amiibowiki.amiibolist

import com.oscarg798.amiibowiki.amiibolist.logger.AmiiboListLogger
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListFailure
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListReducer
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiiboFilteredUseCase
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiibosUseCase
import com.oscarg798.amiibowiki.amiibolist.usecases.SearchAmiibosUseCase
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class AmiiboListViewModelTest :
    ViewModelTestRule.ViewModelCreator<AmiiboListViewState, AmiiboListViewModel> {

    private val getAmiibosUseCase = relaxedMockk<GetAmiibosUseCase>()
    private val getAmiibosFilteredUseCase = relaxedMockk<GetAmiiboFilteredUseCase>()
    private val getAmiiboTypeUseCase = relaxedMockk<GetAmiiboTypeUseCase>()
    private val searchAmiiboUseCase = relaxedMockk<SearchAmiibosUseCase>()
    private val amiiboListLogger = relaxedMockk<AmiiboListLogger>()
    private val isFeatureEnableUseCase = relaxedMockk<IsFeatureEnableUseCase>()
    private val reducer = AmiiboListReducer()

    @get:Rule
    val viewModelRule: ViewModelTestRule<AmiiboListViewState, AmiiboListViewModel> =
        ViewModelTestRule(this)

    @Before
    fun setup() {
        every { getAmiiboTypeUseCase.execute() } answers { flowOf(listOf(AMIIBO_TYPE)) }
        every { getAmiibosFilteredUseCase.execute(AMIIBO_TYPE) } answers { flowOf(listOf(AMIIBO)) }
        every { getAmiibosUseCase.execute() } answers { flowOf(listOf(AMIIBO)) }
    }

    override fun create(): AmiiboListViewModel = AmiiboListViewModel(
        getAmiibosUseCase,
        getAmiibosFilteredUseCase,
        getAmiiboTypeUseCase,
        searchAmiiboUseCase,
        amiiboListLogger,
        isFeatureEnableUseCase,
        reducer,
        viewModelRule.coroutineContextProvider
    )

    @Test
    fun `given a wish to refresh amiibos then it should get the amiibos and return them in the state`() {
        viewModelRule.viewModel.onWish(AmiiboListWish.RefreshAmiibos)

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = true,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = listOf(AMIIBO).map { ViewAmiibo(it) },
                filters = null,
                amiiboTailToShow = null,
                error = null
            )
        )

        verify { getAmiibosUseCase.execute() }
    }

    @Test
    fun `given there is an AmiiboListFailure getting the amiibos when wish is processed then it should return the error in the state`() {
        every { getAmiibosUseCase.execute() } answers {
            flow {
                throw AmiiboListFailure.UnknowError
            }
        }
        viewModelRule.viewModel.onWish(AmiiboListWish.RefreshAmiibos)

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = true,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = AmiiboListFailure.UnknowError
            )
        )

        verify { getAmiibosUseCase.execute() }
    }

    @Test
    fun `given a wish to filter the amiibos when wish is processed then it should return the filtered amiibos in the state`() {
        viewModelRule.viewModel.onWish(AmiiboListWish.FilterAmiibos(ViewAmiiboType("1", "2")))

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = true,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = listOf(AMIIBO).map { ViewAmiibo(it) },
                filters = null,
                amiiboTailToShow = null,
                error = null
            )
        )

        verify {
            getAmiibosFilteredUseCase.execute(AMIIBO_TYPE)
            amiiboListLogger.trackFilterApplied(any())
        }
    }

    @Test
    fun `given an amiibo list failure filtering the amiibos when wish is processed then it should return the error in the state`() {
        every { getAmiibosFilteredUseCase.execute(AMIIBO_TYPE) } answers { flow { throw AmiiboListFailure.UnknowError } }

        viewModelRule.viewModel.onWish(AmiiboListWish.FilterAmiibos(ViewAmiiboType("1", "2")))

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = true,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = AmiiboListFailure.UnknowError
            )
        )

        verify { getAmiibosFilteredUseCase.execute(AMIIBO_TYPE) }
    }

    @Ignore("We are not able to capture the erro inside the coroutine in the wish processor")
    @Test
    fun `given an error filtering the amiibos when wish is processed then it should return the error in the state`() {
        every { getAmiibosFilteredUseCase.execute(AMIIBO_TYPE) } answers { flow { throw NullPointerException() } }

        viewModelRule.viewModel.onWish(AmiiboListWish.FilterAmiibos(ViewAmiiboType("1", "2")))

        verify { getAmiibosFilteredUseCase.execute(AMIIBO_TYPE) }
    }

    @Test
    fun `given a wish to show the amiibos filters when wish is processed then types should be returned in the state`() {
        viewModelRule.viewModel.onWish(AmiiboListWish.ShowFilters)

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = null,
                filters = listOf(ViewAmiiboType("1", "2")),
                amiiboTailToShow = null,
                error = null
            )
        )

        verify {
            getAmiiboTypeUseCase.execute()
            amiiboListLogger.trackShownFiltersClicked()
        }
    }

    @Test
    fun `given a wish to show the amiibo details  and ff on when wish is process then amiibo id should be included in the state`() {
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowAmiiboDetail) } answers { true }
        viewModelRule.viewModel.onWish(AmiiboListWish.ShowAmiiboDetail(ViewAmiibo(AMIIBO)))

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = "11",
                error = null
            )
        )

        verify {
            amiiboListLogger.trackAmiiboClicked(any())
        }
    }

    @Test
    fun `given a wish to show the amiibo details  and ff off when wish is process then nothing should happen view should be idling`() {
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowAmiiboDetail) } answers { true }
        viewModelRule.viewModel.onWish(AmiiboListWish.ShowAmiiboDetail(ViewAmiibo(AMIIBO)))

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            )
        )

        verify {
            amiiboListLogger.trackAmiiboClicked(any())
        }
    }

    @Test
    fun `given a query to search amiibos when wish is processed then it should return the search results in the state`() {
        every { searchAmiiboUseCase.execute(MOCK_QUERY) } answers { flowOf(listOf(AMIIBO)) }

        viewModelRule.viewModel.onWish(AmiiboListWish.Search(MOCK_QUERY))

        viewModelRule.testCollector wereValuesEmitted listOf(
            AmiiboListViewState(
                isIdling = true,
                isLoading = false,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = true,
                amiibos = null,
                filters = null,
                amiiboTailToShow = null,
                error = null
            ),
            AmiiboListViewState(
                isIdling = false,
                isLoading = false,
                amiibos = listOf(AMIIBO).map { ViewAmiibo(it) },
                filters = null,
                amiiboTailToShow = null,
                error = null
            )
        )

        verify {
            searchAmiiboUseCase.execute(MOCK_QUERY)
        }
    }
}

private const val MOCK_QUERY = "1"
private val AMIIBO_TYPE = AmiiboType("1", "2")
private val AMIIBO = Amiibo(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    AmiiboReleaseDate("7", "8", "10", "9"),
    "11", "12"
)

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
import com.oscarg798.amiibowiki.amiibodetail.logger.AmiiboDetailLogger
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UiEffect
import com.oscarg798.amiibowiki.amiibodetail.mvi.ViewState
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class AmiiboDetailViewModelTest :
    ViewModelTestRule.ViewModelCreator<ViewState, AmiiboDetailViewModel> {

    @get: Rule
    val viewModelTestTule =
        ViewModelTestRule(this)

    private val logger = relaxedMockk<AmiiboDetailLogger>()
    private val getAmiiboDetailUseCase = mockk<GetAmiiboDetailUseCase>()
    private val isFeatureFlagEnableUseCase = mockk<IsFeatureEnableUseCase>()
    private val savedStateHandle = relaxedMockk<SavedStateHandle>()

    @Before
    fun setup() {
        coEvery { savedStateHandle.get<ViewState>(any()) } answers { null }
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } answers { AMIIBO }
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames) } answers { false }
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowGameDetail) } answers { false }
    }

    override fun create(): AmiiboDetailViewModel = AmiiboDetailViewModel(
        handle = savedStateHandle,
        getAmiiboDetailUseCase = getAmiiboDetailUseCase,
        amiiboDetailLogger = logger,
        isFeatureEnableUseCase = isFeatureFlagEnableUseCase,
        coroutineContextProvider = viewModelTestTule.coroutineContextProvider
    )

    @Test
    fun `given showrelated games FF is off and ShowDetail wish when view model process it then it should update the state with the amiibo result`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))

        viewModelTestTule.stateCollector wereValuesEmitted listOf(
            STATE.copy(loading = true),
            STATE.copy(amiibo = VIEW_AMIIBO_DETAIL),
            STATE.copy(relatedGamesSectionEnabled = false, amiibo = VIEW_AMIIBO_DETAIL)
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test
    fun `given show related games FF is on and ShowDetail wish when view model process it then it should update the state with the amiibo result`() {
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames) } answers { true }
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))

        viewModelTestTule.stateCollector wereValuesEmitted listOf(
            STATE.copy(loading = true),
            STATE.copy(amiibo = VIEW_AMIIBO_DETAIL),
            STATE.copy(relatedGamesSectionEnabled = true, amiibo = VIEW_AMIIBO_DETAIL)
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test
    fun `given show amiibo detail wish when view model process it  and there is an AmiiboNotFoundByTail failure it then it should update the state with the error`() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws AmiiboDetailFailure.AmiiboNotFoundByTail(
            TAIL
        )

        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))

        viewModelTestTule.stateCollector wereValuesEmitted listOf(
            STATE.copy(loading = true),
            STATE.copy(error = AmiiboDetailFailure.AmiiboNotFoundByTail(TAIL)),
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test
    fun `given expand image wish when its processed then it should generate a side effect to show the image`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(AMIIBO_IMAGE_URL))

        viewModelTestTule.effectCollector.wereValuesEmitted(
            listOf(
                UiEffect.ShowAmiiboImage(
                    AMIIBO_IMAGE_URL
                )
            )
        )
    }

    @Test
    fun `given show related games wish when its processed then it should generate a side effect to show them`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)

        viewModelTestTule.effectCollector.wereValuesEmitted(
            listOf(
                UiEffect.ShowRelatedGames(TAIL)
            )
        )
    }

    @Test
    fun `given ShowDetail wish when view model process and there is an Exception failure it then it should return it a unknow`() {
        val error = NullPointerException()
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws error

        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))

        viewModelTestTule.stateCollector wereValuesEmitted listOf(
            STATE,
            STATE.copy(loading = true),
            STATE.copy(error = AmiiboDetailFailure.UnknowError(error)),
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }
    }

    @Test
    fun `when show amiibo details wish is emitted then it should track the view as shown with the properties`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(TAIL))

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

private val STATE = ViewState()
private const val AMIIBO_IMAGE_URL = "5"
private const val GAME_ID = 4
private const val GAME_SERIES = "22"
private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"
private val AMIIBO = Amiibo(
    amiiboSeries = "1",
    character = "2",
    gameSeries = GAME_SERIES,
    head = "4",
    image = AMIIBO_IMAGE_URL,
    type = "6",
    releaseDate = AmiiboReleaseDate("7", "8", "9", "10"),
    tail = "11", name = "12"
)
private val VIEW_AMIIBO_DETAIL = ViewAmiiboDetails(AMIIBO)
private const val TAIL = "1"

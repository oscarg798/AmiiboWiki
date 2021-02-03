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

package com.oscarg798.amiibowiki.gamedetail

import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory
import com.oscarg798.amiibowiki.gamedetail.logger.GameDetailLogger
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageType
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailReducer
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.usecases.ExpandGameImagesUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGameTrailerUseCase
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameDetailViewModelTest :
    ViewModelTestRule.ViewModelCreator<GameDetailViewState, GameDetailViewModel> {

    @get: Rule
    val viewModelTestRule = ViewModelTestRule<GameDetailViewState, GameDetailViewModel>(this)

    private val gameDetailLogger = relaxedMockk<GameDetailLogger>()
    private val getGamesUseCase = relaxedMockk<GetGamesUseCase>()
    private val expandGameImagesUseCase = relaxedMockk<ExpandGameImagesUseCase>()
    private val getGameTrailerUseCase = relaxedMockk<GetGameTrailerUseCase>()
    private val reducer = spyk(GameDetailReducer())

    @Before
    fun setup() {
        coEvery { getGamesUseCase.execute(GAME_ID) } answers { GAME }
        coEvery { expandGameImagesUseCase.execute(EXPAND_IMAGE_PARAMS) } answers { EXPANDED_IMAGES }
    }

    override fun create(): GameDetailViewModel = GameDetailViewModel(
        GAME_ID,
        getGamesUseCase,
        expandGameImagesUseCase,
        getGameTrailerUseCase,
        gameDetailLogger,
        reducer,
        viewModelTestRule.coroutineContextProvider
    )

    @Test
    fun `given a wish to show the game details when its processed then it should return the state with the details`() {
        viewModelTestRule.viewModel.onWish(GameDetailWish.ShowGameDetail)

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            GameDetailViewState.Idling,
            GameDetailViewState.Loading,
            GameDetailViewState.ShowingGameDetails(GAME)
        )

        coVerify {
            getGamesUseCase.execute(GAME_ID)
            gameDetailLogger.trackScreenShown(mapOf("GAME_ID" to GAME_ID.toString()))
        }

        coVerify(exactly = 2) { reducer.reduce(any(), any()) }
    }

    @Test
    fun `given a wish to show the game trailer when its processed then it should return the state with the trailer id`() {
        coEvery { getGameTrailerUseCase.execute(GAME_ID) } answers { "9" }
        viewModelTestRule.viewModel.onWish(
            GameDetailWish.PlayGameTrailer
        )

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            GameDetailViewState.Idling,
            GameDetailViewState.ShowingGameTrailer("9")
        )

        verify {
            gameDetailLogger.trackTrailerClicked(mapOf("GAME_ID" to GAME_ID.toString()))
        }
    }

    @Test
    fun `given a wish to expand cover image when wish is processed then it shoudl return the expanded image`() {
        viewModelTestRule.viewModel.onWish(GameDetailWish.ExpandImages(EXPAND_IMAGE_PARAMS))

        viewModelTestRule.testCollector wereValuesEmitted listOf(
            GameDetailViewState.Idling,
            GameDetailViewState.ShowingGameImages(EXPANDED_IMAGES)
        )
    }
}

private val EXPANDED_IMAGES = listOf("expand_url")
private val EXPAND_IMAGE_PARAMS =
    listOf(ExpandableImageParam("url", ExpandableImageType.Cover))
private const val GAME_ID = 45
private val GAME =
    Game(
        id = GAME_ID,
        name = "2",
        category = GameCategory.createFromCategoryId(0),
        cover = "3",
        summary = "6",
        rating = 7.toDouble(),
        webSites = setOf("8"),
        videosId = setOf("9"),
        artworks = setOf("10"),
        ageRating = listOf(),
        screenshots = setOf()
    )

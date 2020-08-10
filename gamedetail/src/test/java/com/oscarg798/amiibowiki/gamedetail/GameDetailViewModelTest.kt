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
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailReducer
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameDetailViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val gameDetailLogger = relaxedMockk<GameDetailLogger>()
    private val getGamesUseCase = relaxedMockk<GetGamesUseCase>()
    private val reducer = spyk(GameDetailReducer())

    private lateinit var testCollector: TestCollector<GameDetailViewState>
    private lateinit var viewModel: GameDetailViewModel

    @Before
    fun setup() {
        coEvery { getGamesUseCase.execute(GAME_SERIE, GAME_ID) } answers { GAME }
        viewModel = GameDetailViewModel(
            getGamesUseCase,
            gameDetailLogger,
            reducer,
            coroutinesRule.coroutineContextProvider
        )
        testCollector = TestCollector()
    }

    @Test
    fun `given a wish to show the game details when its processed then it should return the state with the details`() {
        viewModel.onWish(GameDetailWish.ShowGameDetail(GAME_ID, GAME_SERIE))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            GameDetailViewState(
                isIdling = true,
                isLoading = false,
                gameDetails = null,
                gameTrailer = null,
                error = null
            ),
            GameDetailViewState(
                isIdling = false,
                isLoading = true,
                gameDetails = null,
                gameTrailer = null,
                error = null
            ),
            GameDetailViewState(
                isIdling = false,
                isLoading = false,
                gameDetails = GAME,
                gameTrailer = null,
                error = null
            )

        )

        coVerify {
            getGamesUseCase.execute(GAME_SERIE, GAME_ID)
            gameDetailLogger.trackScreenShown(mapOf("GAME_ID" to GAME_ID.toString()))
        }

        coVerify(exactly = 2) { reducer.reduce(any(), any()) }
    }

    @Test
    fun `given a wish to show the game trailer when its processed then it should return the state with the trailer id`() {
        viewModel.onWish(GameDetailWish.PlayGameTrailer(GAME_ID, GAME.videosId!!.first()))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            GameDetailViewState(
                isIdling = true,
                isLoading = false,
                gameDetails = null,
                gameTrailer = null,
                error = null
            ),
            GameDetailViewState(
                isIdling = false,
                isLoading = true,
                gameDetails = null,
                gameTrailer = null,
                error = null
            ),
            GameDetailViewState(
                isIdling = false,
                isLoading = false,
                gameDetails = null,
                gameTrailer = "9",
                error = null
            )

        )

        verify {
            gameDetailLogger.trackTrailerClicked(mapOf("GAME_ID" to GAME_ID.toString()))
        }
    }
}

private const val GAME_ID = 45
private const val GAME_SERIE = "44"
private val GAME =
    Game(
        id = GAME_ID,
        name = "2",
        category = GameCategory.createFromCategoryId(0),
        cover = "3",
        gameSeries = "5",
        summary = "6",
        raiting = 7.toDouble(),
        webSites = setOf("8"),
        videosId = setOf("9"),
        artworks = setOf("10"),
        ageRating = listOf(),
        screenshots = setOf()
    )

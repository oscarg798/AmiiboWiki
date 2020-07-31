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

import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.gamedetail.logger.GameDetailLogger
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameDetailViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val gameDetailLogger = relaxedMockk<GameDetailLogger>()
    private val getGamesUseCase = relaxedMockk<GetGamesUseCase>()

    private lateinit var testCollector: TestCollector<GameDetailViewState>
    private lateinit var viewModel: GameDetailViewModel

    @Before
    fun setup() {
        coEvery { getGamesUseCase.execute(GAME_SERIE, GAME_ID) } answers { GAME }
        viewModel = GameDetailViewModel(
            getGamesUseCase,
            gameDetailLogger,
            coroutinesRule.coroutineContextProvider
        )
        testCollector = TestCollector()
    }

    @Test
    fun `given a wish to show the game details when its processed then it should return the state with the details`() {
        viewModel.onWish(GameDetailWish.ShowGameDetail(GAME_ID, GAME_SERIE))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            GameDetailViewState.init(),
            GameDetailViewState(
                ViewState.LoadingState.Loading,
                GameDetailViewState.Status.None,
                null
            ),
            GameDetailViewState(
                ViewState.LoadingState.None,
                GameDetailViewState.Status.ShowingGameDetails(GAME),
                null
            )
        )

        coVerify {
            getGamesUseCase.execute(GAME_SERIE, GAME_ID)
            gameDetailLogger.trackScreenShown(mapOf("GAME_ID" to GAME_ID.toString()))
        }
    }

    @Test
    fun `given a wish to show the game trailer when its processed then it should return the state with the trailer id`() {
        viewModel.onWish(GameDetailWish.ShowGameDetail(GAME_ID, GAME_SERIE))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.clear()

        viewModel.onWish(GameDetailWish.PlayGameTrailer)

        testCollector wereValuesEmitted listOf(
            GameDetailViewState(
                ViewState.LoadingState.Loading,
                GameDetailViewState.Status.None,
                null
            ),
            GameDetailViewState(
                ViewState.LoadingState.None,
                GameDetailViewState.Status.PlayingGameTrailer("9"),
                null
            )
        )

        verify {
            gameDetailLogger.trackTrailerClicked(mapOf("GAME_ID" to GAME_ID.toString()))
        }
    }

    @Test
    fun `given a wish to show the game trailer when its processed if game does not have video then it throw a GameDoesNotIncludeTrailer exception`() {
        coEvery {
            getGamesUseCase.execute(
                GAME_SERIE,
                GAME_ID
            )
        } answers { GAME.copy(videosId = null) }

        viewModel.onWish(GameDetailWish.ShowGameDetail(GAME_ID, GAME_SERIE))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector.clear()

        viewModel.onWish(GameDetailWish.PlayGameTrailer)

        testCollector wereValuesEmitted listOf(
            GameDetailViewState(
                ViewState.LoadingState.Loading,
                GameDetailViewState.Status.None,
                null
            )
        )

        coroutinesRule.testCoroutineScope.uncaughtExceptions.size shouldBeEqualTo 1
        coroutinesRule.testCoroutineScope.uncaughtExceptions[0] shouldBeInstanceOf GameDetailFailure.GameDoesNotIncludeTrailer::class
    }
}

private const val GAME_ID = 45
private const val GAME_SERIE = "44"
private val GAME =
    Game(
        GAME_ID, "2",
        GameCategory.createFromCategoryId(0),
        "3",
        "5",
        "6",
        7.toDouble(),
        setOf("8"),
        setOf("9"),
        setOf("10"),
        listOf(),
        setOf()
    )

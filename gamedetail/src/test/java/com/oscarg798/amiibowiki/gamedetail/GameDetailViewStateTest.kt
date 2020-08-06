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
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailResult
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.junit.Before
import org.junit.Test

class GameDetailViewStateTest {

    private lateinit var state: GameDetailViewState

    @Before
    fun setup() {
        state = GameDetailViewState.init()
    }

    @Test
    fun `when state is init then it should it should not be loading, nor playing a trailer or showing details or errored`() {
        state.loading shouldBeInstanceOf ViewState.LoadingState.None::class

        state.error.shouldBeNull()
    }

    @Test
    fun `given a loading result  when state is reduced then it should reflect this loading state`() {
        val newState = state.reduce(GameDetailResult.Loading) as GameDetailViewState

        newState.loading shouldBeInstanceOf ViewState.LoadingState.Loading::class
        state.status shouldBeInstanceOf GameDetailViewState.Status.None::class
        newState.error.shouldBeNull()
    }

    @Test
    fun `given a game fetched result when state is reduced then it status should be showing game detail`() {
        val newState = state.reduce(GameDetailResult.GameFetched(GAME)) as GameDetailViewState

        newState.loading shouldBeInstanceOf ViewState.LoadingState.None::class
        newState.status shouldBeInstanceOf GameDetailViewState.Status.ShowingGameDetails::class

        (newState.status as GameDetailViewState.Status.ShowingGameDetails).gameDetails shouldBeEqualTo GAME
        newState.error.shouldBeNull()
    }

    @Test
    fun `given a game trailer found result when state is reduced then it state should reflect the trailer that will be shown`() {
        val newState =
            state.reduce(GameDetailResult.GameTrailerFound(TRAILER_ID)) as GameDetailViewState

        newState.loading shouldBeInstanceOf ViewState.LoadingState.None::class
        newState.status shouldBeInstanceOf GameDetailViewState.Status.PlayingGameTrailer::class

        (newState.status as GameDetailViewState.Status.PlayingGameTrailer).gameTrailer shouldBeEqualTo TRAILER_ID
        newState.error.shouldBeNull()
    }

    @Test
    fun `given a game detail failure when state is reduced then state should contain the state`() {
        val newState =
            state.reduce(GameDetailResult.Error(GameDetailFailure.GameNotFound(GAME.id))) as GameDetailViewState

        newState.loading shouldBeInstanceOf ViewState.LoadingState.None::class
        newState.status shouldBeInstanceOf GameDetailViewState.Status.None::class
        newState.error shouldBeInstanceOf GameDetailFailure::class
    }
}

private const val TRAILER_ID = "11"
private val GAME =
    Game(
        1, "2", GameCategory.createFromCategoryId(0), "3", "5", "6", 7.toDouble(), setOf("8"),
        setOf("9"), setOf("10"), listOf(), setOf()
    )

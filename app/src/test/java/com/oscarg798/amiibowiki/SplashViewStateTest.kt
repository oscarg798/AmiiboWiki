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

package com.oscarg798.amiibowiki

import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashResult
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class SplashViewStateTest {

    private lateinit var state: SplashViewState

    @Before
    fun setup() {
        state = SplashViewState.init()
    }

    @Test
    fun `when result is loading then view state should be loading`() {
        val newState = state.reduce(SplashResult.Loading)

        assertEquals(
            SplashViewState(
                ViewState.LoadingState.Loading,
                SplashViewState.FetchStatus.None,
                null
            ), newState
        )
    }

    @Test
    fun `when result is Fetch success then view state should not be loading and should felect this`() {
        val newState = state.reduce(SplashResult.TypesFetched)

        assertEquals(
            SplashViewState(
                ViewState.LoadingState.None,
                SplashViewState.FetchStatus.Success,
                null
            ), newState
        )
    }

    @Test
    fun `when there is an error then view state should have this error`() {
        val error = Exception()
        val newState = state.reduce(SplashResult.Error(error)) as SplashViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(SplashViewState.FetchStatus.None, newState.status)
        assert(newState.error != null)

    }
}

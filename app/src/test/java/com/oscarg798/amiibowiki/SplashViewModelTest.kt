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

import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.splash.SplashLogger
import com.oscarg798.amiibowiki.splash.SplashViewModel
import com.oscarg798.amiibowiki.splash.mvi.ViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.splash.mvi.UiEffect
import com.oscarg798.amiibowiki.splash.usecases.InitializeApplicationUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test


internal class SplashViewModelTest : ViewModelTestRule.ViewModelCreator<ViewState, SplashViewModel> {

    @get: Rule
    val viewModelTestRule = ViewModelTestRule(this)

    private val logger = relaxedMockk<SplashLogger>()
    private val initializaApplicationUseCase = mockk<InitializeApplicationUseCase>()

    @Before
    fun setup() {
        coEvery { initializaApplicationUseCase.execute() }  answers { flowOf(Unit)}
    }

    override fun create(): SplashViewModel = SplashViewModel(
        logger,
        initializaApplicationUseCase,
        viewModelTestRule.coroutineContextProvider
    )

    @Test
    fun `given a wish to get the types when events are proccess then state value should be loading and then fetch success`() {
        viewModelTestRule.viewModel.onWish(SplashWish.GetTypes)

        viewModelTestRule.effectCollector wereValuesEmitted listOf(UiEffect.Navigate)

        coVerify {
            initializaApplicationUseCase.execute()
            logger.trackScreenShown()
        }
    }

    @Test
    fun `given a wish to get the types when events are proccess but there is an error then error should be in the state`() {
        val error = AmiiboTypeFailure.FetchTypesFailure()
        coEvery { initializaApplicationUseCase.execute() } answers  { flow { throw error }}

        viewModelTestRule.viewModel.onWish(SplashWish.GetTypes)

        val initState = ViewState()
        viewModelTestRule.stateCollector wereValuesEmitted listOf(
            initState,
            initState.copy(loading = false, error = error)
        )

        coVerify {
            initializaApplicationUseCase.execute()
        }
    }
}

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

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.SplashViewModel
import com.oscarg798.amiibowiki.splash.failures.FetchTypesFailure
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.testutils.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class SplashViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val updateAmiiboTypeUseCase = mockk<UpdateAmiiboTypeUseCase>()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        coEvery { updateAmiiboTypeUseCase.execute() } answers { Result.success(Unit) }
        viewModel =
            SplashViewModel(updateAmiiboTypeUseCase, coroutinesRule.coroutineContextProvider)
        viewModel.viewModelScope.newCoroutineContext(coroutinesRule.coroutineContextProvider.mainDispatcher)
    }

    @Test
    fun `given a wish to get the types when events are proccess then state value should be loading and then fetch success`() {
        viewModel.onWish(SplashWish.GetTypes)
        val list = arrayListOf<SplashViewState>()
        viewModel.state.onEach {
            list.add(it)
        }.launchIn(CoroutineScope(coroutinesRule.coroutineContextProvider.mainDispatcher))

        list shouldBeEqualTo listOf(
            SplashViewState(
                status = SplashViewState.FetchStatus.None,
                error = null
            ),
            SplashViewState(
                status = SplashViewState.FetchStatus.Success,
                error = null
            )
        )

        coVerify {
            updateAmiiboTypeUseCase.execute()
        }
    }

    @Test
    fun `given a wish to get the types when events are proccess but there is an error then state value should be loading and then fetch success`() {
        val error = Exception("something")
        coEvery { updateAmiiboTypeUseCase.execute() } answers { Result.failure(error) }
        viewModel.onWish(SplashWish.GetTypes)
        val list = arrayListOf<SplashViewState>()
        viewModel.state.onEach {
            list.add(it)
        }.launchIn(CoroutineScope(coroutinesRule.coroutineContextProvider.mainDispatcher))


        Assert.assertEquals(SplashViewState(
            status = SplashViewState.FetchStatus.None,
            error = null
        ), list[0])

        val result = list[1]
        result.status shouldBeEqualTo SplashViewState.FetchStatus.None
        result.error shouldNotBeEqualTo  null
        assert(result.error is FetchTypesFailure)

        coVerify {
            updateAmiiboTypeUseCase.execute()
        }
    }
}

private val TYPES = listOf(AmiiboType("1", "2"))
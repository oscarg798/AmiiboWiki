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
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.SplashViewModel
import com.oscarg798.amiibowiki.splash.mvi.SplashResult
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import com.oscarg798.amiibowiki.splash.mvi.SplashWish
import com.oscarg798.amiibowiki.testutils.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class SplashViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val getAmiiboTypeUseCase = mockk<GetAmiiboTypeUseCase>()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        coEvery { getAmiiboTypeUseCase.execute() } answers { Result.success(TYPES) }
        viewModel = SplashViewModel(getAmiiboTypeUseCase, coroutinesRule.coroutineContextProvider)
        viewModel.viewModelScope.newCoroutineContext(coroutinesRule.coroutineContextProvider.mainDispatcher)
    }
}

private val TYPES = listOf(AmiiboType("1", "2"))
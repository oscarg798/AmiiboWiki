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

package com.oscarg798.amiibowiki.testutils.testrules

import com.oscarg798.amiibowiki.core.base.AbstractViewModelCompat
import com.oscarg798.amiibowiki.core.mvi.ViewStateCompat as MVIViewState
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ViewModelTestRule<ViewState : MVIViewState, VM : AbstractViewModelCompat<*, *, ViewState>>(
    private val viewModelCreator: ViewModelCreator<ViewState, VM>
) : TestRule {

    private lateinit var job: Job
    val testDispatcher = TestCoroutineDispatcher()
    val testCoroutineScope = TestCoroutineScope(testDispatcher)
    val coroutineContextProvider =
        CoroutineContextProvider(
            testDispatcher,
            testDispatcher
        )

    lateinit var viewModel: VM
    lateinit var testCollector: TestCollector<ViewState>

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                Dispatchers.setMain(testDispatcher)
                viewModel = viewModelCreator.create()
                testCollector = TestCollector()
                job = testCollector.test(testCoroutineScope, viewModel.state)
                base.evaluate()
                Dispatchers.resetMain()
                job.cancel()
                testCollector.clear()
            }
        }
    }

    interface ViewModelCreator<ViewState : MVIViewState, VM : AbstractViewModelCompat<*, *, ViewState>> {

        fun create(): VM
    }
}

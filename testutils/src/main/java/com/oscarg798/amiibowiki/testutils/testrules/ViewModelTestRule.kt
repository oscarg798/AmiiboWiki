/*
 * Copyright 2021 Oscar David Gallon Rosero
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

import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.mvi.SideEffect
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.mvi.ViewState as MVIViewState
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ViewModelTestRule<ViewState : MVIViewState, UIEffect : SideEffect, VM : AbstractViewModel<ViewState, UIEffect, *>>(
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
    lateinit var stateCollector: TestCollector<ViewState>
    lateinit var effectCollector: TestCollector<UIEffect>

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                job = SupervisorJob()
                Dispatchers.setMain(testDispatcher)
                viewModel = viewModelCreator.create()
                stateCollector = TestCollector()
                effectCollector = TestCollector()
                stateCollector.test(testCoroutineScope, viewModel.state, job)
                effectCollector.test(testCoroutineScope, viewModel.uiEffect, job)
                base.evaluate()
                Dispatchers.resetMain()
                job.cancel()
                effectCollector.clear()
                stateCollector.clear()
            }
        }
    }

    interface ViewModelCreator<ViewState : com.oscarg798.amiibowiki.core.mvi.ViewState, VM : AbstractViewModel<ViewState, *, *>> {

        fun create(): VM
    }
}

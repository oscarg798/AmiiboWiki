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

package com.oscarg798.potterwiki.core.mvi

import com.oscarg798.potterwiki.core.mvi.Result as MVIResult
import com.oscarg798.potterwiki.core.mvi.Actions as MVIActions

interface ViewRenderer<Result : MVIResult, State : ViewState<Result>, Actions : MVIActions> {

    fun render(state: State, actions: Actions)
}

interface ChildRenderer<Result : MVIResult, State : ViewState<Result>,
        Actions : MVIActions> :
    ViewRenderer<Result, State, Actions> {

    fun isApplicable(state: State): Boolean
}

abstract class AbstractViewRenderer<Result : MVIResult,
        State : ViewState<Result>, Actions : MVIActions>(
    private val childRenderers: List<ChildRenderer<Result, State, Actions>>
) : ViewRenderer<Result, State, Actions> {

    override fun render(state: State, actions: Actions) {
        childRenderers.first {
            it.isApplicable(state)
        }.render(state, actions)
    }

}
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

package com.oscarg798.amiibowiki.core.base

import androidx.lifecycle.ViewModel
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.mvi.Result as MVIResult
import com.oscarg798.amiibowiki.core.mvi.ViewState as MVIViewState
import com.oscarg798.amiibowiki.core.mvi.Wish as MVIWish
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.scan

abstract class AbstractViewModel<Wish : MVIWish, Result : MVIResult,
    ViewState : MVIViewState>(initialState: ViewState) : ViewModel() {

    protected abstract val reducer: Reducer<Result, ViewState>
    protected abstract val coroutineContextProvider: CoroutineContextProvider

    private val wishProcessor = ConflatedBroadcastChannel<Wish>()

    val state: Flow<ViewState> = wishProcessor.asFlow()
        .flatMapMerge {
            getResult(it)
        }
        .scan(initialState) { state, result ->
            reducer.reduce(state, result)
        }

    protected val defaultExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, exception -> throw exception }

    protected abstract suspend fun getResult(wish: Wish): Flow<Result>

    open fun onScreenShown() {
        // DO_NOTHING
    }

    fun onWish(wish: Wish) {
        wishProcessor.offer(wish)
    }

    override fun onCleared() {
        wishProcessor.close()
        super.onCleared()
    }
}

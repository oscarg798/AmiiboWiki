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
import com.oscarg798.amiibowiki.core.mvi.SideEffect as MVIUIEffect
import com.oscarg798.amiibowiki.core.mvi.ViewState as MVIViewState
import com.oscarg798.amiibowiki.core.mvi.Wish as MVIWish
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class AbstractViewModel<ViewState : MVIViewState, UIEffect : MVIUIEffect, Wish : MVIWish>(
    private val initialState: ViewState
) :
    ViewModel() {

    protected abstract val coroutineContextProvider: CoroutineContextProvider
    protected val _state =
        MutableSharedFlow<ViewState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected val _uiEffect =
        MutableSharedFlow<UIEffect>(extraBufferCapacity = 1)
    private val stateMutex = Mutex()

    val state: Flow<ViewState>
        get() = _state

    val uiEffect: Flow<UIEffect>
        get() = _uiEffect.filterNotNull()

    protected suspend fun updateState(
        reducer: (ViewState) -> ViewState
    ) {
        stateMutex.withLock {
            _state.emit(reducer(_state.replayCache.firstOrNull() ?: initialState))
        }
    }

    fun onWish(wish: Wish) {
        processWish(wish)
    }

    abstract fun processWish(wish: Wish)
}

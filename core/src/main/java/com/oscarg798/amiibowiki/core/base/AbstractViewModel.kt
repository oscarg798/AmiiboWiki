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
import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.mvi.Result as MVIResult
import com.oscarg798.amiibowiki.core.mvi.ViewState as MVIViewState
import com.oscarg798.amiibowiki.core.mvi.Wish as MVIWish
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

abstract class AbstractViewModel<Wish : MVIWish, Result : MVIResult, ViewState : MVIViewState>(
    initialState: ViewState
) : ViewModel() {

    protected abstract val reducer: Reducer<Result, ViewState>

    protected abstract val coroutineContextProvider: CoroutineContextProvider

    /**
     * We use channels as state flow will omit duplicated states or wishes
     */
    private val _state = MutableSharedFlow<ViewState>(
        replay = 1,
        extraBufferCapacity = 3,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val wishProcessor = MutableSharedFlow<Wish>(
        replay = 0,
        extraBufferCapacity = 3,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )


    val state: Flow<ViewState>
        get() = _state

    init {
        _state.tryEmit(initialState)

        wishProcessor
            .flatMapLatest {
                getResult(it)
            }.scan(initialState) { state, result ->
                reducer.reduce(state, result)
            }.onEach {
                _state.emit(it)
            }.launchIn(viewModelScope)
    }

    protected abstract suspend fun getResult(wish: Wish): Flow<Result>

    open fun onScreenShown() {
        // DO_NOTHING
    }

    fun onWish(wish: Wish) {
        wishProcessor.tryEmit(wish)
    }
}

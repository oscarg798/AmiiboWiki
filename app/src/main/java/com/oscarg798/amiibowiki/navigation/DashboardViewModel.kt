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

package com.oscarg798.amiibowiki.navigation

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.navigation.mvi.CheckUpdatesWish
import com.oscarg798.amiibowiki.navigation.mvi.DashboardViewState
import com.oscarg798.amiibowiki.navigation.mvi.RequestUpdateSideEffect
import com.oscarg798.amiibowiki.updatechecker.UpdateCheckerUseCase
import com.oscarg798.amiibowiki.updatechecker.UpdateType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
internal class DashboardViewModel @Inject constructor(
    private val usecase: UpdateCheckerUseCase,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<DashboardViewState, RequestUpdateSideEffect, CheckUpdatesWish>(DashboardViewState) {

    override fun processWish(wish: CheckUpdatesWish) {
        viewModelScope.launch {
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    usecase.execute()
                }
            }.onSuccess { status ->
                if (status is UpdateStatus.UpdateAvailable) {
                    _uiEffect.value = RequestUpdateSideEffect(
                        when (status) {
                            is UpdateStatus.UpdateAvailable.Immediate -> UpdateType.Immediate
                            else -> UpdateType.Flexible
                        }
                    )
                }
            }
        }
    }
}

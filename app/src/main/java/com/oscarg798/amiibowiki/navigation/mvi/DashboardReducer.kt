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

package com.oscarg798.amiibowiki.navigation.mvi

import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.navigation.UpdateStatus
import com.oscarg798.amiibowiki.updatechecker.UpdateType
import javax.inject.Inject

class DashboardReducer @Inject constructor() : Reducer<DashboardResult, DashboardViewState> {
    override suspend fun reduce(
        state: DashboardViewState,
        from: DashboardResult
    ): DashboardViewState = when (from) {
        is DashboardResult.UpdateStatusFound -> when (from.status) {
            UpdateStatus.AlreadyUpdated -> DashboardViewState.Idling
            is UpdateStatus.UpdateAvailable -> DashboardViewState.RequestingUpdate(
                when (from.status) {
                    is UpdateStatus.UpdateAvailable.Immediate -> UpdateType.Immediate
                    else -> UpdateType.Flexible
                }
            )
        }
    }
}
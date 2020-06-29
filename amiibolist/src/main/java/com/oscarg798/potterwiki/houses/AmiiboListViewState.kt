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

package com.oscarg798.potterwiki.houses

import com.oscarg798.potterwiki.core.models.AmiiboType
import com.oscarg798.potterwiki.core.mvi.ViewState
import com.oscarg798.potterwiki.houses.mvi.AmiiboListFailure
import com.oscarg798.potterwiki.houses.mvi.AmiiboListResult

data class AmiiboListViewState(
    val loading: ViewState.LoadingState,
    val status: Status,
    val filtering: Filtering,
    val showingFilters: ShowingFilters,
    val error: AmiiboListFailure?
) : ViewState<AmiiboListResult> {

    sealed class Filtering {
        object None : Filtering()
        data class FetchSuccess(val amiibos: List<ViewAmiibo>) : Filtering()
    }

    sealed class ShowingFilters {
        object None : ShowingFilters()
        data class FetchSuccess(val filters: List<ViewAmiiboType>) : ShowingFilters()
    }

    sealed class Status {
        object None : Status()
        data class AmiibosFetched(val amiibos: List<ViewAmiibo>) : Status()

    }

    /**
     * Map should happen in other place
     */
    override fun reduce(result: AmiiboListResult): ViewState<AmiiboListResult> {
        return when (result) {
            is AmiiboListResult.Loading -> copy(
                loading = ViewState.LoadingState.Loading,
                status = Status.None,
                filtering = Filtering.None,
                showingFilters = ShowingFilters.None,
                error = null
            )
            is AmiiboListResult.FetchSuccess -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.AmiibosFetched(result.amiibos.map { it.map() }),
                filtering = Filtering.None,
                showingFilters = ShowingFilters.None,
                error = null
            )
            is AmiiboListResult.Error -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.None,
                error = result.error,
                filtering = Filtering.None,
                showingFilters = ShowingFilters.None
            )
            is AmiiboListResult.AmiibosFiltered -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.None,
                filtering = Filtering.FetchSuccess(result.amiibos.map { it.map() }),
                error = null
            )
            is AmiiboListResult.FiltersFetched -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.None,
                filtering = Filtering.None,
                showingFilters = ShowingFilters.FetchSuccess(result.filters.map { it.map() }),
                error = null
            )
        }
    }


    companion object {
        fun init() =
            AmiiboListViewState(
                ViewState.LoadingState.None,
                Status.None,
                Filtering.None,
                ShowingFilters.None,
                null
            )
    }


}
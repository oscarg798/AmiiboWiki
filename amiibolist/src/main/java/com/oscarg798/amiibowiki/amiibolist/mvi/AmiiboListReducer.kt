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

package com.oscarg798.amiibowiki.amiibolist.mvi

import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo
import com.oscarg798.amiibowiki.amiibolist.ViewAmiiboType
import com.oscarg798.amiibowiki.core.mvi.Reducer
import javax.inject.Inject

class AmiiboListReducer @Inject constructor() : Reducer<AmiiboListResult, AmiiboListViewState> {

    override suspend fun reduce(
        state: AmiiboListViewState,
        from: AmiiboListResult
    ): AmiiboListViewState {
        return when (from) {
            is AmiiboListResult.Loading -> state.copy(
                isIdling = false,
                isLoading = true,
                amiiboTailToShow = null,
                error = null
            )
            is AmiiboListResult.FetchSuccess -> state.copy(
                isIdling = false,
                isLoading = false,
                amiiboTailToShow = null,
                filters = null,
                amiibos = from.amiibos.map { amiibo ->
                    ViewAmiibo(amiibo)
                },
                error = null
            )
            is AmiiboListResult.AmiibosFiltered -> state.copy(
                isIdling = false,
                isLoading = false,
                amiiboTailToShow = null,
                filters = null,
                amiibos = from.amiibos.map { amiibo ->
                    ViewAmiibo(amiibo)
                },
                error = null
            )
            is AmiiboListResult.Error -> state.copy(
                isIdling = false,
                isLoading = false,
                error = from.error,
                amiiboTailToShow = null
            )
            is AmiiboListResult.FiltersFetched -> state.copy(
                isIdling = false,
                isLoading = false,
                amiiboTailToShow = null,
                filters = from.filters.map { amiiboType ->
                    ViewAmiiboType(amiiboType)
                },
                error = null
            )
            is AmiiboListResult.ShowAmiiboDetail -> state.copy(
                isIdling = false,
                isLoading = false,
                amiiboTailToShow = from.amiiboTail,
                error = null
            )
            is AmiiboListResult.None -> state.copy(
                isIdling = true,
                isLoading = false,
                amiiboTailToShow = null,
                error = null
            )
        }
    }
}

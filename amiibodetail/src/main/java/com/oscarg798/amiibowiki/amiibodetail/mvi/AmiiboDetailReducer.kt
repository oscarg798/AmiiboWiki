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

package com.oscarg798.amiibowiki.amiibodetail.mvi

import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.core.mvi.ReducerCompat
import javax.inject.Inject

class AmiiboDetailReducer @Inject constructor() :
    Reducer<AmiiboDetailResult, AmiiboDetailViewState> {

    override suspend fun reduce(
        state: AmiiboDetailViewState,
        from: AmiiboDetailResult
    ): AmiiboDetailViewState = when (from) {
        is AmiiboDetailResult.Loading -> AmiiboDetailViewState.Loading
        is AmiiboDetailResult.DetailFetched -> AmiiboDetailViewState.ShowingAmiiboDetails(
            ShowingAmiiboDetailsParams(
                ViewAmiiboDetails(from.amiibo), from.isRelatedGamesSectionEnabled
            )
        )
        is AmiiboDetailResult.ImageExpanded -> AmiiboDetailViewState.ShowingAmiiboImage(from.url)
        is AmiiboDetailResult.Error -> AmiiboDetailViewState.Error(from.error)
    }
}

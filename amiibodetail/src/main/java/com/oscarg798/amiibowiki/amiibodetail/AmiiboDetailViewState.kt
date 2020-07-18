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

package com.oscarg798.amiibowiki.amiibodetail

import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.mvi.ViewState


data class AmiiboDetailViewState(
    val status: Status,
    val error: AmiiboDetailFailure? = null
) : ViewState<AmiiboDetailResult> {

    sealed class Status {
        object None : Status()
        data class ShowingDetail(val amiibo: Amiibo) : Status()
    }

    override fun reduce(result: AmiiboDetailResult): ViewState<AmiiboDetailResult> {
        return when (result) {
            is AmiiboDetailResult.DetailFetched -> copy(
                status = Status.ShowingDetail(result.amiibo),
                error = null
            )
            is AmiiboDetailResult.Error -> copy(
                status = Status.None,
                error = result.error
            )
        }
    }

    companion object {

        fun init() = AmiiboDetailViewState(Status.None)
    }
}
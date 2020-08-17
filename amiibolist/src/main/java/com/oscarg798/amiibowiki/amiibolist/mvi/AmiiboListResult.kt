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

import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.mvi.Result

sealed class AmiiboListResult : Result {
    object Loading : AmiiboListResult()
    object FilterSelectionCancelled : AmiiboListResult()
    object OpenSettings : AmiiboListResult()

    data class AmiibosFetched(val amiibos: Collection<Amiibo>) : AmiiboListResult()
    data class AmiibosFiltered(val amiibos: Collection<Amiibo>) : AmiiboListResult()
    data class FiltersFetched(val filters: Collection<AmiiboType>) : AmiiboListResult()
    data class ShowAmiiboDetail(val amiiboTail: String) : AmiiboListResult()
    data class Error(val error: AmiiboListFailure) : AmiiboListResult()
}

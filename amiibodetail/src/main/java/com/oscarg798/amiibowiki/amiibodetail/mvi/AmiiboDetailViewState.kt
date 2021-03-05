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
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.mvi.ViewState
import javax.annotation.concurrent.Immutable

@Immutable
data class AmiiboDetailViewState(
    val loading: Boolean = false,
    val showingDetails: ViewAmiiboDetails? = null,
    val relatedGamesSectionEnabled: Boolean = false,
    val showingImage: String? = null,
    val showRelatedGames: String? = null,
    val error: AmiiboDetailFailure? = null
) : ViewState

sealed class UIEffect : com.oscarg798.amiibowiki.core.mvi.UIEffect{
    object Nothing : UIEffect()
    data class ShowAmiiboImage(val url: String) : UIEffect() {
        override fun equals(other: Any?): Boolean = false
    }

    data class ShowRelatedGames(val amiiboId: String) : UIEffect() {
        override fun equals(other: Any?): Boolean = false
    }


}

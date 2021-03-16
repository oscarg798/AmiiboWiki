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

package com.oscarg798.amiibowiki.searchgamesresults.composeui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.oscarg798.amiibowiki.amiibodetail.ThemeContainer
import com.oscarg798.amiibowiki.core.spacingMedium
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultViewState
import com.oscarg798.amiibowiki.searchgamesresults.resultsListId

@Composable
internal fun Screen(
    state: SearchResultViewState,
    onSearchResultClickListener: onSearchResultClickListener
) {
    ThemeContainer {
        when {
            state.isLoading -> Loading()
            state.gamesResult != null ->
                ConstraintLayout(
                    constraintSet = getConstraintSet(),
                    Modifier.background(MaterialTheme.colors.background)
                ) {
                    LazyColumn(
                        Modifier
                            .layoutId(resultsListId)
                            .padding(
                                start = spacingMedium,
                                end = spacingMedium
                            )
                    ) {
                        items(
                            items = state.gamesResult.toList()
                        ) { SearchResult(item = it, onSearchResultClickListener) }
                    }
                }
        }
    }
}

private fun getConstraintSet() = ConstraintSet {
    val resultsListId = createRefFor(resultsListId)

    constrain(resultsListId) {
        top.linkTo(parent.top)
        linkTo(start = parent.start, end = parent.end)
    }
}

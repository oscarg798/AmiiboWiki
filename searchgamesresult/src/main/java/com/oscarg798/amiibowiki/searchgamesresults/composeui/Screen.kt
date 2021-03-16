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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.amiibodetail.ThemeContainer
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultViewState

@Composable
internal fun Screen(
    state: SearchResultViewState,
    searchBox: Boolean,
    onSearchResultClickListener: onSearchResultClickListener,
    currentQuery: String,
    onSearch: (String) -> Unit
) {
    val constrainSet = getConstraintSet(searchBox)
    ThemeContainer {
        ConstraintLayout(
            constraintSet = constrainSet,
            Modifier.background(MaterialTheme.colors.background)
        ) {
            if (searchBox) {
                SearchBox(currentQuery, onSearch)
            }
            when {
                state.isLoading -> Loading()
                state.idling -> EmptyState()
                state.gamesResult != null -> GameResults(
                    state.gamesResult,
                    onSearchResultClickListener
                )
            }
        }
    }
}

private fun getConstraintSet(searchBox: Boolean) = ConstraintSet {
    val resultsListId = createRefFor(resultsListId)
    val searchBoxId = createRefFor(searchBoxId)
    val emptyStateId = createRefFor(emptyStateId)
    val loadingId = createRefFor(loadingId)

    constrain(searchBoxId) {
        top.linkTo(parent.top)
        linkTo(start = parent.start, end = parent.end)
    }

    constrain(emptyStateId) {
        getContentConstraints(searchBox, searchBoxId)
    }

    constrain(resultsListId) {
        getContentConstraints(searchBox, searchBoxId)
    }

    constrain(loadingId) {
        getContentConstraints(searchBox, searchBoxId)
    }
}

private fun ConstrainScope.getContentConstraints(
    searchBox: Boolean,
    searchBoxId: ConstrainedLayoutReference
) {
    height = Dimension.fillToConstraints
    getVerticalConstrainsBasedOnSearchBox(searchBox, searchBoxId)
    linkTo(start = parent.start, end = parent.end)
}

private fun ConstrainScope.getVerticalConstrainsBasedOnSearchBox(
    searchBox: Boolean,
    searchBoxId: ConstrainedLayoutReference
) {
    if (searchBox) {
        linkTo(top = searchBoxId.bottom, bottom = parent.bottom)
    } else {
        linkTo(top = parent.top, bottom = parent.bottom)
    }
}

internal const val loadingId = "loadingId"
internal const val searchBoxId = "searchBox"
internal const val resultsListId = "resultsListId"
internal const val emptyStateId = "emptyStateId"

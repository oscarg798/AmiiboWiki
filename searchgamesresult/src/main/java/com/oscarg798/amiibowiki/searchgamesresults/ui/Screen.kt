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

package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.searchgamesresults.SearchGamesResultViewModel
import com.oscarg798.amiibowiki.searchgamesresults.mvi.ViewState

@Composable
internal fun Screen(
    viewModel: SearchGamesResultViewModel,
    searchBox: Boolean,
    onSearchResultClickListener: onSearchResultClickListener,
    onSearch: (TextFieldValue) -> Unit
) {
    val state by viewModel.state.collectAsState(initial = ViewState())
    var searchState by remember { mutableStateOf(TextFieldValue()) }
    val constrainSet = getConstraintSet(searchBox)

    ThemeContainer {
        ConstraintLayout(
            constraintSet = constrainSet,
            Modifier.background(MaterialTheme.colors.background)
        ) {
            if (searchBox) {
                SearchBox(
                    query = searchState,
                    onSearch = {
                        searchState = it
                        onSearch(it)
                    }
                )
            }
            when {
                state.isLoading -> GameResultsLoading()
                state.idling -> IdlingState()
                state.gamesResult != null && state.gamesResult!!.isEmpty() -> EmptyState()
                state.gamesResult != null && state.gamesResult!!.isNotEmpty() -> GameResults(
                    state.gamesResult!!,
                    onSearchResultClickListener
                )
            }
        }
    }
}

private fun getConstraintSet(searchBox: Boolean) = ConstraintSet {
    val resultsListId = createRefFor(ResultListId)
    val searchBoxId = createRefFor(SearchBoxId)
    val emptyStateId = createRefFor(EmptyStateId)
    val loadingId = createRefFor(LoadingId)

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

internal const val LoadingId = "loadingId"
internal const val SearchBoxId = "searchBox"
internal const val ResultListId = "resultsListId"
internal const val EmptyStateId = "emptyStateId"

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

package com.oscarg798.amiibowiki.amiibolist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.constraintlayout.compose.ConstraintSet
import com.oscarg798.amiibowiki.amiibodetail.ThemeContainer
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewModel
import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboClickListener
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import kotlinx.coroutines.CoroutineScope

@ExperimentalFoundationApi
@Composable
internal fun Screen(viewModel: AmiiboListViewModel, coroutineScope: CoroutineScope) {
    val state by viewModel.state.collectAsState(AmiiboListViewState())

    val snackbarHostState = remember { SnackbarHostState() }

    ThemeContainer {
        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
            AmiiboListError(
                state = state,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope
            )
            AmiiboLoadingList(state = state)
            AmiiboList(
                state,
                object : AmiiboClickListener {
                    override fun onClick(viewAmiibo: ViewAmiibo) {
                        viewModel.onWish(AmiiboListWish.ShowAmiiboDetail(viewAmiibo))
                    }
                }
            )
        }
    }
}

private fun getConstraints() = ConstraintSet {

    val contentId = createRefFor(CONTENT_ID)
    val errorId = createRefFor(ERROR_ID)

    constrain(contentId) {
        linkTo(top = parent.top, bottom = errorId.bottom)
        linkTo(start = parent.start, end = parent.end)
    }

    constrain(errorId) {
        bottom.linkTo(parent.bottom)
        linkTo(start = parent.start, end = parent.end)
    }
}

private const val CONTENT_ID = "content"
internal const val ERROR_ID = "error"

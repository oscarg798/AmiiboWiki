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
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewModel
import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import kotlinx.coroutines.CoroutineScope

@ExperimentalFoundationApi
@Composable
internal fun Screen(
    viewModel: AmiiboListViewModel,
    coroutineScope: CoroutineScope,
    amiiboClickListener: (ViewAmiibo) -> Unit,
) {
    val state by viewModel.state.collectAsState(initial = AmiiboListViewState())
    val snackbarHostState = remember { SnackbarHostState() }

    ThemeContainer {
        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
            if (state.error != null) {
                ErrorSnackbar(
                    message = state.error?.message,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )
            }

            when {
                state.loading -> AmiiboListLoading(state = state)
                !state.loading && state.amiibos != null -> AmiiboList(
                    state.amiibos!!,
                    amiiboClickListener
                )
            }
        }
    }
}

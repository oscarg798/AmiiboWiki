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

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewModel
import com.oscarg798.amiibowiki.amiibolist.R
import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo
import com.oscarg798.amiibowiki.amiibolist.mvi.ViewState
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import kotlinx.coroutines.CoroutineScope

@ExperimentalFoundationApi
@Composable
internal fun AmiiboListScreen(
    viewModel: AmiiboListViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    screenConfigurator: ScreenConfigurator
) {

    val state by viewModel.state.collectAsState(initial = ViewState())

    if (state.error != null) {
        ErrorSnackbar(
            message = state.error?.message,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        )
    }

    DetailsTile(screenConfigurator)

    when {
        state.loading -> AmiiboListLoading()
        !state.loading && state.amiibos != null ->
            AmiiboList(
                state.amiibos!!
            ) { amiibo ->
                showAmiiboDetail(navController, amiibo)
            }
    }
}

@Composable
private fun DetailsTile(
    screenConfigurator: ScreenConfigurator
) {
    val title = stringResource(id = R.string.app_name)
    SideEffect {
        screenConfigurator.titleUpdater(title)
    }
}

private fun showAmiiboDetail(
    navController: NavController,
    amiibo: ViewAmiibo
) {
    Router.AmiiboDetail.navigate(
        navController = navController,
        arguments = Bundle().apply {
            putString(
                Router.AmiiboDetail.AmiiboIdArgument,
                amiibo.tail
            )
        }
    )
}

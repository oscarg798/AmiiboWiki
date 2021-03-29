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

package com.oscarg798.amiibowiki.amiibodetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.oscarg798.amiibowiki.amiibodetail.AmiiboDetailViewModel
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UiEffect
import com.oscarg798.amiibowiki.amiibodetail.mvi.ViewState
import com.oscarg798.amiibowiki.core.failures.ArgumentNotFoundException
import com.oscarg798.amiibowiki.core.ui.Screen
import com.oscarg798.amiibowiki.core.utils.requireCurrentBackStackEntryArguments
import com.oscarg798.amiibowiki.core.utils.requirePreviousBackStackEntryArguments

@Composable
fun AmiiboDetailScreen(
    navController: NavController
) {
    val viewModel: AmiiboDetailViewModel = hiltNavGraphViewModel()
    val state by viewModel.state.collectAsState(initial = ViewState())

    val amiiboId =
        navController.requirePreviousBackStackEntryArguments()
            .getString(Screen.Detail.AmiiboIdArgument)
            ?: throw ArgumentNotFoundException(Screen.Detail.AmiiboIdArgument)

    viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(amiiboId))

    when {
        state.loading -> AmiiboDetailLoading()
        state.amiibo != null -> Detail(
            viewAmiiboDetails = state.amiibo!!,
            relatedGamesSectionEnabled = state.relatedGamesSectionEnabled,
            onImageClick = {
                // TODO: Show image composable
            },
            onRelatedGamesButtonClick = {
                viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)
            }
        )
    }

    ObserveUiEffects(
        amiiboId = amiiboId,
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun ObserveUiEffects(
    amiiboId: String,
    navController: NavController,
    viewModel: AmiiboDetailViewModel
) {
    val uiEffect by viewModel.uiEffect.collectAsState(initial = null)
    when (uiEffect) {
        is UiEffect.ShowAmiiboImage -> {
            // TODO: Show image composable
        }
        is UiEffect.ShowRelatedGames -> {
            navController.requireCurrentBackStackEntryArguments()
                .putString(Screen.SearchGames.AmiiboIdArgument, amiiboId)
            navController.requireCurrentBackStackEntryArguments()
                .putBoolean(Screen.SearchGames.ShowSearchBoxArgument, false)
            navController.navigate(Screen.SearchGames.RelatedGamesRoute)
        }
    }
}

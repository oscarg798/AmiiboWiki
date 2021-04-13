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

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.oscarg798.amiibowiki.amiibodetail.AmiiboDetailViewModel
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UiEffect
import com.oscarg798.amiibowiki.amiibodetail.mvi.ViewState
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator

@Composable
internal fun AmiiboDetailScreen(
    viewModel: AmiiboDetailViewModel,
    amiiboId: String,
    navController: NavController,
    screenConfigurator: ScreenConfigurator
) {
    val state by viewModel.state.collectAsState(initial = ViewState())

    viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail(amiiboId))

    when {
        state.loading -> AmiiboDetailLoading()
        state.amiibo != null -> Detail(
            viewAmiiboDetails = state.amiibo!!,
            relatedGamesSectionEnabled = state.relatedGamesSectionEnabled,
            screenConfigurator = screenConfigurator,
            onImageClick = { image ->
                viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(image))
            },
            onRelatedGamesButtonClick = {
                viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)
            }
        )
    }

    ObserveUiEffects(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun ObserveUiEffects(
    navController: NavController,
    viewModel: AmiiboDetailViewModel
) {
    val uiEffect by viewModel.uiEffect.collectAsState(initial = null)
    when (uiEffect) {
        is UiEffect.ShowAmiiboImage -> {
            expandImage(navController, (uiEffect as UiEffect.ShowAmiiboImage).url)
        }
        is UiEffect.ShowRelatedGames -> {
            Router.RelatedGames.navigate(
                navController = navController,
                arguments = Bundle().apply {
                    putString(
                        Router.RelatedGames.AmiiboIdArgument,
                        (uiEffect as UiEffect.ShowRelatedGames).amiiboId
                    )
                }
            )
        }
    }
}

private fun expandImage(navController: NavController, image: String) {
    Router.ImageGallery.navigate(
        navController = navController,
        arguments = Bundle().apply {
            putString(
                Router.ImageGallery.ImageArgument,
                image
            )
        }
    )
}

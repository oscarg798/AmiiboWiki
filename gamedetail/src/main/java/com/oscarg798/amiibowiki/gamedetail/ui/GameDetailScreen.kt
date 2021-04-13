package com.oscarg798.amiibowiki.gamedetail.ui

import android.app.Activity
import android.os.Bundle
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.gamedetail.GameDetailViewModel
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.mvi.UiEffect
import com.oscarg798.amiibowiki.gamedetail.mvi.ViewState

@Composable
internal fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    gameId: Int,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    screenConfigurator: ScreenConfigurator
) {

    val state by viewModel.state.collectAsState(initial = ViewState())
    ObserveViewModelState(navController = navController, viewModel = viewModel)

    if (state.error != null) {
        ErrorSnackbar(
            message = state.error?.message,
            snackbarHostState = snackbarHostState,
            coroutineScope = rememberCoroutineScope()
        )
    }

    when {
        state.loading -> GameDetailLoading()
        state.game != null ->
            GameDetail(
                game = state.game!!,
                screenConfigurator = screenConfigurator,
                onTrailerClicked = {
                    viewModel.onWish(GameDetailWish.PlayGameTrailer)
                },
                onBackButtonPressed = {
                    navController.popBackStack()
                }
            ) {
                viewModel.onWish(GameDetailWish.ExpandImages(it))
            }
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.onWish(
            GameDetailWish.ShowGameDetail(gameId)
        )
    }
}

@Composable
private fun ObserveViewModelState(
    navController: NavController,
    viewModel: GameDetailViewModel
) {

    val uiEffect by viewModel.uiEffect.collectAsState(initial = null)

    when (uiEffect) {
        is UiEffect.ShowingGameImages -> expandImage(
            navController = navController,
            image = (uiEffect as UiEffect.ShowingGameImages).image
        )
        is UiEffect.ShowingGameTrailer -> {
            val effect = uiEffect as UiEffect.ShowingGameTrailer
            ShowGameTrailer(effect.trailer, effect.apiKey)
        }
    }
}

@Composable
private fun ShowGameTrailer(trailer: String, apiKey: String) {
    val context = LocalContext.current

    val intent = YouTubeStandalonePlayer.createVideoIntent(
        context as Activity,
        apiKey,
        trailer, START_TIME, true, true
    )
    context.startActivity(intent)
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

private const val START_TIME = 0

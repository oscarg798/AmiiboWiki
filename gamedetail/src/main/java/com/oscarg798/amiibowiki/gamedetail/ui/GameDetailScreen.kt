package com.oscarg798.amiibowiki.gamedetail.ui

import android.app.Activity
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.Screen
import com.oscarg798.amiibowiki.core.utils.requirePreviousBackStackEntryArguments
import com.oscarg798.amiibowiki.gamedetail.GameDetailViewModel
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.mvi.UiEffect
import com.oscarg798.amiibowiki.gamedetail.mvi.ViewState

@Composable
fun GameDetailScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {

    val viewModel: GameDetailViewModel = hiltNavGraphViewModel()
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
            GameDetailWish.ShowGameDetail(
                navController.requirePreviousBackStackEntryArguments()
                    .getInt(Screen.GameDetail.GameIdArgument)
            )
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
        is UiEffect.ShowingGameImages -> {
            // Expand Images
        }
        is UiEffect.ShowingGameTrailer -> Navigate((uiEffect as UiEffect.ShowingGameTrailer).trailer)
    }
}

@Composable
fun Navigate(trailer: String) {
    val context = LocalContext.current

    val intent = YouTubeStandalonePlayer.createVideoIntent(
        context as Activity,
        "AIzaSyBXzt2KOnnQT084k5xRCq64ctFn",
        trailer, START_TIME, true, true
    )
    context.startActivity(intent)
}

private const val START_TIME = 0

package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.gamedetail.GameDetailViewModel
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.mvi.ViewState
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun Screen(
    viewModel: GameDetailViewModel,
    coroutineScope: CoroutineScope,
    onTrailerClicked: () -> Unit,
    onBackPressed: () -> Unit
) {

    val state by viewModel.state.collectAsState(initial = ViewState())
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
                state.loading -> GameDetailLoading()
                state.game != null ->
                    GameDetail(
                        game = state.game!!,
                        onTrailerClicked = onTrailerClicked,
                        onBackButtonPressed = onBackPressed
                    ) {
                        viewModel.onWish(GameDetailWish.ExpandImages(it))
                    }
            }
        }
    }
}

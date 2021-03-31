package com.oscarg798.amiibowiki.nfcreader.ui.navigation

import android.nfc.Tag
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oscarg798.amiibowiki.core.R
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.LoadingAnimation
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.nfcreader.NFCReaderViewModel
import com.oscarg798.amiibowiki.nfcreader.mvi.ReadTagWish
import com.oscarg798.amiibowiki.nfcreader.mvi.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect

internal fun NavGraphBuilder.nfcReaderNavigation(
    tag: Tag,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    onErrorDismissed: () -> Unit
) =
    composable(route = Router.NFCReader.route) { backStackEntry ->
        val viewModel: NFCReaderViewModel = hiltNavGraphViewModel(backStackEntry)
        val state by viewModel.state.collectAsState(initial = ViewState())

        ConstraintLayout(
            constraintSet = ConstraintSet {
                val animationId = createRefFor(AnimationId)
                constrain(animationId) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = parent.end)
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            if (state.error != null) {
                ErrorSnackbar(
                    message = state.error?.message ?: stringResource(R.string.generic_error),
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                ) {
                    onErrorDismissed()
                }
            }

            LoadingAnimation(
                modifier = Modifier.layoutId(AnimationId),
                playing = state.error == null
            )
        }

        ObserveUiEffects(viewModel, tag, navController)
    }

@Composable
private fun ObserveUiEffects(
    viewModel: NFCReaderViewModel,
    tag: Tag,
    navController: NavController
) {
    LaunchedEffect(key1 = viewModel) {

        viewModel.onWish(ReadTagWish(tag))

        viewModel.uiEffect.collect {
            navController.popBackStack()
            Router.AmiiboDetail.navigate(
                navController = navController,
                arguments = Bundle().apply {
                    putString(
                        Router.AmiiboDetail.AmiiboIdArgument,
                        it.amiiboIdentifier.tail
                    )
                }
            )
        }
    }
}

private const val AnimationId = "AnimationId"

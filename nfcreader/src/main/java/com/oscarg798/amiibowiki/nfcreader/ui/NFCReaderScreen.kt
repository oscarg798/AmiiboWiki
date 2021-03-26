package com.oscarg798.amiibowiki.nfcreader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.oscarg798.amiibowiki.core.R
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.LoadingAnimation
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.nfcreader.NFCReaderViewModel
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun NFCReaderScreen(
    viewModel: NFCReaderViewModel, coroutineScope: CoroutineScope,
    onErrorDismissed: () -> Unit
) {

    ThemeContainer {
        val state by viewModel.state.collectAsState(initial = NFCReaderViewState())
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
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
        }
    }
}

private const val AnimationId = "AnimationId"

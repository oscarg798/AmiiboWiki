package com.oscarg798.amiibowiki.splash.ui

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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.ErrorSnackbar
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.splash.SplashViewModel
import com.oscarg798.amiibowiki.splash.mvi.SplashViewState
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun SplashScreen(viewModel: SplashViewModel, coroutineScope: CoroutineScope) {
    ThemeContainer {
        val state by viewModel.state.collectAsState(initial = SplashViewState())
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
            ConstraintLayout(
                constraintSet = getConstraints(),
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                if (state.error != null) {
                    ErrorSnackbar(
                        message = state.error?.message,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope
                    )
                }
                SplashAnimation()
                AppTitle()
            }
        }
    }
}

@Composable
private fun getConstraints() = ConstraintSet {
    val animationId = createRefFor(AnimationId)
    val titleId = createRefFor(TitleId)

    constrain(animationId) {
        linkTo(top = parent.top, bottom = parent.bottom)
        linkTo(start = parent.start, end = parent.end)
    }

    constrain(titleId) {
        bottom.linkTo(parent.bottom, margin = Dimensions.Spacing.Medium)
        linkTo(start = parent.start, end = parent.end)
    }
}

internal const val AnimationId = "AnimationId"
internal const val TitleId = "TitleId"

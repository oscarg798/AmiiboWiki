package com.oscarg798.amiibowiki.core.ui

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oscarg798.amiibowiki.core.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ErrorSnackbar(
    message: String? = stringResource(R.string.generic_error),
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = message!!,
            duration = SnackbarDuration.Short
        )
    }
}

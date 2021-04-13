package com.oscarg798.amiibowiki.core.ui

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oscarg798.amiibowiki.core.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ErrorSnackbar(
    message: String?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit = {}
) {

    val snackbarMessage = message ?: stringResource(R.string.generic_error)
    coroutineScope.launch {
        val state = snackbarHostState.showSnackbar(
            message = snackbarMessage,
            duration = SnackbarDuration.Short
        )

        if (state == SnackbarResult.Dismissed) {
            onDismiss()
        }
    }
}

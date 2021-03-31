package com.oscarg798.amiibowiki.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.dashboard.DashboardViewModel
import com.oscarg798.amiibowiki.dashboard.mvi.DashboardWish
import com.oscarg798.amiibowiki.dashboard.mvi.UiEffect
import com.oscarg798.amiibowiki.updatechecker.UpdateType
import com.oscarg798.amiibowiki.updatechecker.openPlayStore

@Composable
internal fun UpdateDialog(
    uiEffect: UiEffect,
    viewModel: DashboardViewModel
) {
    val context = LocalContext.current
    AlertDialog(modifier = Modifier.background(MaterialTheme.colors.surface),
        onDismissRequest = {
            if ((uiEffect as UiEffect.RequestUpdateSideEffect).type is UpdateType.Flexible) {
                viewModel.onWish(DashboardWish.HideUpdateDialog)
            }
        }, text = {
            Text(
                stringResource(id = R.string.update_available),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(MaterialTheme.colors.onSurface)
                )
            )
        }, confirmButton = {
            Button(
                onClick = {
                    context.openPlayStore()
                }) {
                Text(
                    stringResource(id = R.string.update_dialog_positive_button),
                    style = TextStyle(MaterialTheme.colors.secondary)
                )
            }
        })
}

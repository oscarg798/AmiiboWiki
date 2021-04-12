package com.oscarg798.amiibowiki.settings.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.ArrayAdapter
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.settings.R
import com.oscarg798.amiibowiki.settings.SettingsViewModel
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.amiibowiki.settings.mvi.UiEffect
import com.oscarg798.amiibowiki.settings.mvi.ViewState
import com.oscarg798.flagly.developeroptions.FeatureFlagHandlerActivity
import java.lang.ref.WeakReference
import kotlinx.coroutines.flow.collect

@Composable
internal fun SettingsScreen(viewModel: SettingsViewModel, screenConfigurator: ScreenConfigurator) {
    val state by viewModel.state.collectAsState(initial = ViewState())
    val context = WeakReference(LocalContext.current)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(Dimensions.Spacing.Medium)
    ) {
        if (state.preferences != null) {
            state.preferences!!.toList().map { preference ->
                PreferenceItem(
                    key = preference.key,
                    title = preference.title,
                    icon = preference.iconResourceId,
                    onPreferenceClick = {
                        viewModel.onWish(SettingsWish.PreferenceClicked(preference.key))
                    }
                )
            }
        }
    }

    SettingsTitle(screenConfigurator)

    LaunchedEffect(key1 = viewModel) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                UiEffect.RecreateActivity -> restartActivity(context)
                UiEffect.ShowingDarkModeDialog -> showDarkModeDialog(context, viewModel)
                UiEffect.ShowingDevelopmentActivity -> showDevelopmentActivity(context)
            }
        }
    }

    SideEffect {
        viewModel.onWish(SettingsWish.CreatePreferences)
    }
}

private fun showDevelopmentActivity(weakReferenceContext: WeakReference<Context>) {
    val context = weakReferenceContext.get() ?: return
    context.startActivity(
        Intent(
            context,
            FeatureFlagHandlerActivity::class.java
        )
    )
}

@Composable
private fun SettingsTitle(
    screenConfigurator: ScreenConfigurator
) {
    val title = stringResource(id = R.string.settings_fragment_title)
    SideEffect {
        screenConfigurator.titleUpdater(title)
    }
}

@Composable
internal fun PreferenceItem(
    key: String,
    title: String,
    onPreferenceClick: (String) -> Unit,
    @DrawableRes icon: Int?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(PreferenceCellHeight)
            .fillMaxWidth()
            .clickable { onPreferenceClick(key) }
    ) {
        if (icon != null) {
            Column(
                modifier = Modifier
                    .padding(start = Dimensions.Spacing.Small)
                    .size(PreferenceIconSize)
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = stringResource(R.string.preference_item_icon_content_description)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = getPreferenceTitleStartOffset(icon),
                    end = Dimensions.Spacing.Medium
                )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h3.merge(
                    TextStyle(
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Normal
                    )
                )
            )
        }
    }
}

/**
 * Still depending on some android stuffGameDetailViewModelTest
 */
private fun restartActivity(weakReferenceContext: WeakReference<Context>) {
    val activity = weakReferenceContext.get() as? AppCompatActivity ?: return

    activity.packageManager
        .getLaunchIntentForPackage(activity.packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.finish()
        }
}

/**
 * This needs to be migrated to compose
 */
private fun showDarkModeDialog(
    weakReferenceContext: WeakReference<Context>,
    viewModel: SettingsViewModel
) {
    val context = weakReferenceContext.get() ?: return
    val adapter = ArrayAdapter(
        context,
        android.R.layout.select_dialog_singlechoice,
        listOf(
            context.getString(R.string.system_default_dark_mode_option),
            context.getString(R.string.ligth_dark_mode_option),
            context.getString(R.string.dark_mode_option)
        )
    )
    val builder = AlertDialog.Builder(context)

    builder.setAdapter(adapter) { _, which ->
        val selectedOption = adapter.getItem(which)
        require(selectedOption != null)
        viewModel.onWish(SettingsWish.DarkModeOptionSelected(selectedOption))
    }
    builder.show()
}

private fun getPreferenceTitleStartOffset(icon: Int?) = if (icon == null) {
    Dimensions.Spacing.Small + PreferenceIconSize
} else {
    Dimensions.Spacing.Small
}

private val PreferenceCellHeight = 50.dp
private val PreferenceIconSize = 30.dp

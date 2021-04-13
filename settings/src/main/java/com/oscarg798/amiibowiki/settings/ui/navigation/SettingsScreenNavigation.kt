package com.oscarg798.amiibowiki.settings.ui.navigation

import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.settings.SettingsViewModel
import com.oscarg798.amiibowiki.settings.ui.SettingsScreen

fun NavGraphBuilder.settingsScreenNavigation(screenConfigurator: ScreenConfigurator) =
    composable(
        Router.Settings.route, deepLinks = getDeepLinks(),
        content = { bacstackEntry ->
            val viewModel: SettingsViewModel = hiltNavGraphViewModel(bacstackEntry)

            SettingsScreen(
                viewModel = viewModel,
                screenConfigurator = screenConfigurator
            )
        }
    )

private fun getDeepLinks() = listOf(
    navDeepLink {
        uriPattern = Router.Settings.uriPattern
    }
)

package com.oscarg798.amiibowiki.dashboard.ui

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.oscarg798.amiibowiki.amiibodetail.ui.navigation.amiiboDetailNavigation
import com.oscarg798.amiibowiki.amiibolist.ui.navigation.amiiboListNavigation
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.core.ui.imagegallery.imageGalleryNavigation
import com.oscarg798.amiibowiki.gamedetail.ui.navigation.gameDetailNavigation
import com.oscarg798.amiibowiki.searchgamesresults.ui.navigation.searchGameRoute
import com.oscarg798.amiibowiki.settings.ui.navigation.settingsScreenNavigation

@Composable
internal fun MainNavigationHost(
    startDestination: String,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    screenConfigurator: ScreenConfigurator
) {
    NavHost(navController, startDestination = startDestination) {
        amiiboListNavigation(
            snackbarHostState = snackbarHostState,
            navController = navController,
            screenConfigurator = screenConfigurator
        )
        amiiboDetailNavigation(
            navController = navController,
            screenConfigurator = screenConfigurator
        )

        imageGalleryNavigation(navController)

        searchGameRoute(
            deepLink = Router.SearchGames,
            navController = navController,
            searchBoxEnabled = true,
            screenConfigurator = screenConfigurator
        )

        searchGameRoute(
            deepLink = Router.RelatedGames,
            navController = navController,
            searchBoxEnabled = false,
            screenConfigurator = screenConfigurator
        )


        gameDetailNavigation(
            snackbarHostState = snackbarHostState,
            navController = navController,
            screenConfigurator = screenConfigurator
        )

        settingsScreenNavigation(screenConfigurator)
    }

}

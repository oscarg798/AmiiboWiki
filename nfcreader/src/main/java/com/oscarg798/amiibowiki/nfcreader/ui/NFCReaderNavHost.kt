package com.oscarg798.amiibowiki.nfcreader.ui

import android.nfc.Tag
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.oscarg798.amiibowiki.amiibodetail.ui.navigation.amiiboDetailNavigation
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.core.ui.imagegallery.imageGalleryNavigation
import com.oscarg798.amiibowiki.gamedetail.ui.navigation.gameDetailNavigation
import com.oscarg798.amiibowiki.nfcreader.ui.navigation.nfcReaderNavigation
import com.oscarg798.amiibowiki.searchgamesresults.ui.navigation.searchGameRoute
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun NFCReaderNavHost(
    navController: NavHostController,
    tag: Tag,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onErrorDismissed: () -> Unit,
    screenConfigurator: ScreenConfigurator
) {
    NavHost(navController = navController, startDestination = Router.NFCReader.route) {
        nfcReaderNavigation(
            tag = tag,
            navController = navController,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            onErrorDismissed = onErrorDismissed
        )
        amiiboDetailNavigation(
            navController = navController,
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

        imageGalleryNavigation(navController)
    }
}

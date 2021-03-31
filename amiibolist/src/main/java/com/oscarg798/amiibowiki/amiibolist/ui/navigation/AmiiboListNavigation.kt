package com.oscarg798.amiibowiki.amiibolist.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.amiibolist.AmiiboListViewModel
import com.oscarg798.amiibowiki.amiibolist.ui.AmiiboListScreen
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator

@ExperimentalFoundationApi
fun NavGraphBuilder.amiiboListNavigation(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    screenConfigurator: ScreenConfigurator
) = composable(
    route = Router.AmiiboList.route,
    deepLinks = getDeeplinks()
) {

    val viewModel: AmiiboListViewModel = hiltNavGraphViewModel(it)

    AmiiboListScreen(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        navController = navController,
        coroutineScope = rememberCoroutineScope(),
        screenConfigurator = screenConfigurator
    )
}

private fun getDeeplinks() = listOf(
    navDeepLink {
        uriPattern = Router.AmiiboList.uriPattern
    }
)

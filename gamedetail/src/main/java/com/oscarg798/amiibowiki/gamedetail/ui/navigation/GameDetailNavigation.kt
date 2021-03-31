package com.oscarg798.amiibowiki.gamedetail.ui.navigation

import androidx.compose.material.SnackbarHostState
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.core.extensions.requireArguments
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.gamedetail.GameDetailViewModel
import com.oscarg798.amiibowiki.gamedetail.ui.GameDetailScreen

fun NavGraphBuilder.gameDetailNavigation(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    screenConfigurator: ScreenConfigurator
) = composable(
    route = Router.GameDetail.route,
    deepLinks = getDeeplinks(),
    arguments = getArguments(),
    content = { backStackEntry ->

        val viewModel: GameDetailViewModel = hiltNavGraphViewModel(backStackEntry)

        GameDetailScreen(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            navController = navController,
            gameId = backStackEntry.requireArguments().getInt(Router.GameDetail.GameIdArgument),
            screenConfigurator = screenConfigurator
        )
    }
)

private fun getArguments() = listOf(
    navArgument(Router.GameDetail.GameIdArgument) {
        this.nullable = false
        this.type = NavType.IntType
    }
)

private fun getDeeplinks() = listOf(
    navDeepLink {
        uriPattern = Router.GameDetail.uriPattern
    }
)

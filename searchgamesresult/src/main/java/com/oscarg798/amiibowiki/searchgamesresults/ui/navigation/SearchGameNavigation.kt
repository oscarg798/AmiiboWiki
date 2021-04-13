package com.oscarg798.amiibowiki.searchgamesresults.ui.navigation

import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.core.extensions.requireArguments
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.searchgamesresults.SearchGamesResultViewModel
import com.oscarg798.amiibowiki.searchgamesresults.ui.SearchGamesScreen

fun NavGraphBuilder.searchGameRoute(
    deepLink: Router,
    navController: NavController,
    searchBoxEnabled: Boolean,
    screenConfigurator: ScreenConfigurator
) = composable(
    route = deepLink.route,
    deepLinks = getDeeplinks(deepLink),
    content = { backStackEntry ->

        val viewModel: SearchGamesResultViewModel = hiltNavGraphViewModel(backStackEntry)

        SearchGamesScreen(
            viewModel = viewModel,
            navController = navController,
            searchBoxEnabled = searchBoxEnabled,
            screenConfigurator = screenConfigurator,
            amiiboId = backStackEntry.requireArguments()
                .getString(Router.RelatedGames.AmiiboIdArgument)
        )
    }
)

private fun getDeeplinks(deepLink: Router) = listOf(
    navDeepLink {
        uriPattern = deepLink.uriPattern
    }
)

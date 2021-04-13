package com.oscarg798.amiibowiki.amiibodetail.ui.navigation

import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.amiibodetail.AmiiboDetailViewModel
import com.oscarg798.amiibowiki.amiibodetail.ui.AmiiboDetailScreen
import com.oscarg798.amiibowiki.core.extensions.requireArguments
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator

fun NavGraphBuilder.amiiboDetailNavigation(
    navController: NavController,
    screenConfigurator: ScreenConfigurator
) = composable(
    route = Router.AmiiboDetail.route,
    deepLinks = getDeeplinks(),
    content = { backStackEntry ->

        val viewModel: AmiiboDetailViewModel = hiltNavGraphViewModel(backStackEntry)

        val amiiboId = backStackEntry.requireArguments()
            .getString(Router.AmiiboDetail.AmiiboIdArgument)!!

        AmiiboDetailScreen(
            viewModel = viewModel,
            amiiboId = amiiboId,
            navController = navController,
            screenConfigurator = screenConfigurator
        )
    }
)

private fun getDeeplinks() = listOf(
    navDeepLink {
        uriPattern = Router.AmiiboDetail.uriPattern
    }
)

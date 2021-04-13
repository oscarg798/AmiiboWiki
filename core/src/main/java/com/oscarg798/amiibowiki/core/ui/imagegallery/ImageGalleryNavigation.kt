package com.oscarg798.amiibowiki.core.ui.imagegallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.oscarg798.amiibowiki.core.extensions.requireArguments
import com.oscarg798.amiibowiki.core.ui.Router

fun NavGraphBuilder.imageGalleryNavigation(navController: NavController) = composable(
    route = Router.ImageGallery.route,
    arguments = getArguments(),
    deepLinks = getDeeplinks()
) { backStackEntry ->

    ImageGallery(
        image = backStackEntry.requireArguments()
            .getString(Router.ImageGallery.ImageArgument)!!,
        navController = navController
    )
}

private fun getArguments() = listOf(
    navArgument(Router.ImageGallery.ImageArgument) {
        type = NavType.StringType
    }
)

private fun getDeeplinks() = listOf(
    navDeepLink {
        uriPattern = Router.ImageGallery.uriPattern
    }
)

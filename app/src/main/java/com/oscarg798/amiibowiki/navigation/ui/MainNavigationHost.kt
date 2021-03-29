package com.oscarg798.amiibowiki.navigation.ui

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigation
import com.oscarg798.amiibowiki.amiibodetail.ui.AmiiboDetailScreen
import com.oscarg798.amiibowiki.amiibolist.ui.AmiiboListScreen
import com.oscarg798.amiibowiki.core.ui.Screen
import com.oscarg798.amiibowiki.gamedetail.ui.GameDetailScreen
import com.oscarg798.amiibowiki.searchgamesresults.ui.SearchGamesScreen

@Composable
internal fun MainNavigationHost(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    NavHost(navController, startDestination = NavigationScreens.AmiiboList.route) {
        composable(route = Screen.List.route) {
            AmiiboListScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                coroutineScope = rememberCoroutineScope()
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.Detail.AmiiboIdArgument) {
                this.nullable = false
                this.type = NavType.StringType
            }),
            content = {
                AmiiboDetailScreen(navController)
            })

        navigation(
            startDestination = Screen.SearchGames.RelatedGamesStartDestinationRoute,
            route = Screen.SearchGames.RelatedGamesRoute
        ) {
            searchGameRoute(
                route = Screen.SearchGames.RelatedGamesStartDestinationRoute,
                navController = navController,
                searchBoxEnabled = false
            )

            gameDetailRoute(snackbarHostState, navController)
        }

        navigation(
            startDestination = Screen.SearchGames.GameSearchInternalRoute,
            route = Screen.SearchGames.route
        ) {

            searchGameRoute(
                route = Screen.SearchGames.GameSearchInternalRoute,
                navController = navController,
                searchBoxEnabled = true
            )

            gameDetailRoute(snackbarHostState, navController)
        }

    }
}

private fun NavGraphBuilder.searchGameRoute(
    route: String,
    navController: NavHostController,
    searchBoxEnabled: Boolean
) {
    composable(
        route = route,
        arguments = listOf(navArgument(Screen.SearchGames.AmiiboIdArgument) {
            this.nullable = true
            this.type = NavType.StringType
        }, navArgument(Screen.SearchGames.ShowSearchBoxArgument) {
            this.nullable = false
            this.type = NavType.BoolType
        })
    ) {
        SearchGamesScreen(
            navController = navController,
            searchBox = searchBoxEnabled
        )
    }
}


private fun NavGraphBuilder.gameDetailRoute(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    composable(
        Screen.GameDetail.route,
        listOf(navArgument(Screen.GameDetail.GameIdArgument) {
            this.nullable = false
            this.type = NavType.IntType
        })
    ) {
        GameDetailScreen(
            snackbarHostState = snackbarHostState,
            navController = navController
        )
    }
}


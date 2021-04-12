package com.oscarg798.amiibowiki.dashboard.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.oscarg798.amiibowiki.R

@Composable
internal fun AppBottomNavigationBar(
    startDestination: String,
    navController: NavHostController,
    items: List<NavigationScreens>
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
        val currentRoute = CurrentRoute(navController = navController)
        items.forEach { screen ->
            AppNavigationItem(
                screen = screen,
                startDestination = startDestination,
                currentRoute = currentRoute,
                navController = navController
            )
        }
    }
}

@Composable
private fun RowScope.AppNavigationItem(
    screen: NavigationScreens,
    startDestination: String,
    currentRoute: String?,
    navController: NavHostController
) {
    BottomNavigationItem(
        icon = {
            Icon(
                painterResource(id = screen.icon),
                stringResource(R.string.home_icon_content_description)
            )
        },
        label = { Text(stringResource(id = screen.title)) },
        selectedContentColor = MaterialTheme.colors.secondary,
        unselectedContentColor = MaterialTheme.colors.onBackground,
        selected = currentRoute == screen.route || currentRoute == screen.nestedGraphStartedRoute,
        onClick = {
            navController.navigate(screen.route) {
                launchSingleTop = true
                this.popUpTo(startDestination) {
                    inclusive = true
                }

            }
        }
    )

}

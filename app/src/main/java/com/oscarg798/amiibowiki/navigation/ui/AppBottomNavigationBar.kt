package com.oscarg798.amiibowiki.navigation.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.oscarg798.amiibowiki.R

@Composable
internal fun AppBottomNavigationBar(
    navController: NavHostController,
    items: List<NavigationScreens>
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            AppNavigationItem(screen, currentRoute, navController)
        }
    }
}

@Composable
private fun RowScope.AppNavigationItem(
    screen: NavigationScreens,
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
        selected = currentRoute == screen.route,
        onClick = {
            if (currentRoute != screen.route) {
                navController.navigate(screen.route)
            }
        }
    )

}

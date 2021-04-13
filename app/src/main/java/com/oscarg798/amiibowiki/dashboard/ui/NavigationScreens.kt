package com.oscarg798.amiibowiki.dashboard.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.core.ui.Router

internal sealed class NavigationScreens(
    val route: String,
    val nestedGraphStartedRoute: String = route,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    object AmiiboList :
        NavigationScreens(
            route = Router.AmiiboList.route,
            title = R.string.title_home,
            icon = R.drawable.ic_home_black_24dp
        )

    object GameSearch :
        NavigationScreens(
            route = Router.SearchGames.route,
            title = R.string.search,
            icon = R.drawable.ic_search
        )

    object Settings : NavigationScreens(
        route = Router.Settings.route,
        title = R.string.settings_navigation_menu_title,
        icon = R.drawable.ic_settings
    )
}

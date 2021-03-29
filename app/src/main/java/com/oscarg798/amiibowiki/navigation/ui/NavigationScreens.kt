package com.oscarg798.amiibowiki.navigation.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.core.ui.Screen

internal sealed class NavigationScreens(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    object AmiiboList :
        NavigationScreens(Screen.List.route, R.string.title_home, R.drawable.ic_home_black_24dp)

    object GameSearch :
        NavigationScreens(Screen.SearchGames.route, R.string.search, R.drawable.ic_search)
}

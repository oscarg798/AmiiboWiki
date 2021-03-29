package com.oscarg798.amiibowiki.core.ui

sealed class Screen(val route: String) {

    object List : Screen("amiiboList")
    object Detail : Screen("amiiboDetail") {
        const val AmiiboIdArgument = "AmiiboIdArgument"
    }

    object SearchGames : Screen("searchGames") {
        const val GameSearchInternalRoute = "GameSearchInternalRoute"
        const val RelatedGamesRoute = "relatedGames"
        const val RelatedGamesStartDestinationRoute = "RelatedGamesStartDestinationRoute"
        const val ShowSearchBoxArgument = "ShowSearchBoxArgument"
        const val AmiiboIdArgument = "AmiiboIdArgument"
    }

    object GameDetail : Screen("gameDetail") {
        const val GameIdArgument = "GameIdArgument"
    }
}

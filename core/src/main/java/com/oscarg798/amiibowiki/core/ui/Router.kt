package com.oscarg798.amiibowiki.core.ui

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.navigate

sealed class Router(val route: String, val uriPattern: String) {

    object NFCReader : Router(NFCRouterRoute, NFCRouterUriPattern) {
        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri = uriPattern.toUri()
    }

    object AmiiboDetail : Router(route = AmiiboDetailRoute, uriPattern = AmiiboDetailUriPattern) {
        const val AmiiboIdArgument = AmiiboIdArgumentName

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri {
            require(arguments != null)
            return uriPattern.replace(
                "{$AmiiboIdArgument}",
                arguments.getString(AmiiboIdArgument)!!
            ).toUri()
        }
    }

    object AmiiboList : Router(AmiiboListRoute, AmiiboListUriPattern) {

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri = uriPattern.toUri()
    }

    object ImageGallery : Router(
        route = ImageGalleryRoute,
        uriPattern = ImageGalleryUriPattern
    ) {
        const val ImageArgument = ImageArgumentName

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri {
            require(arguments != null)
            return uriPattern.replace("{$ImageArgument}", arguments.getString(ImageArgument)!!)
                .toUri()
        }
    }

    object SearchGames : Router(GameSearchRoute, GameSearchUriPattern) {

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri = uriPattern.toUri()
    }

    object RelatedGames : Router(RelatedGamesRoute, RelatedGamesUriPattern) {
        const val AmiiboIdArgument = AmiiboIdArgumentName

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri {
            require(arguments != null)
            return uriPattern.replace(
                "{$AmiiboIdArgument}",
                arguments.getString(AmiiboIdArgument)!!
            ).toUri()
        }
    }

    object GameDetail : Router(GameDetailRoute, GameDetailUriPattern) {
        const val GameIdArgument = GameIdArgumentName

        override fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri {
            require(arguments != null)
            return uriPattern.replace(
                "{$GameIdArgument}",
                arguments.getInt(
                    GameIdArgument
                ).toString()
            ).toUri()
        }
    }

    protected abstract fun getDeeplinkNavigationRoute(arguments: Bundle?): Uri

    fun navigate(navController: NavController, arguments: Bundle?) {
        navController.navigate(getDeeplinkNavigationRoute(arguments))
    }
}

const val DeeplinkUri = "https://amiibowiki.com"

/**
 * Parameters name
 */
private const val GameIdArgumentName = "gameId"
private const val ImageArgumentName = "image"
private const val AmiiboIdArgumentName = "AmiiboId"

/**
 * Routes and uri Patterns
 */
private const val AmiiboListRoute = "amiiboList"
private const val AmiiboListUriPattern = "$DeeplinkUri/$AmiiboListRoute"

private const val AmiiboDetailRoute = "amiiboDetail?id={$AmiiboIdArgumentName}"
private const val AmiiboDetailUriPattern = "$DeeplinkUri/amiiboDetail/{$AmiiboIdArgumentName}"

private const val GameDetailRoute = "gameDetail/{$GameIdArgumentName}"
private const val GameDetailUriPattern = "$DeeplinkUri/$GameDetailRoute"

private const val ImageGalleryRoute = "imageGalley?image={$ImageArgumentName}"
private const val ImageGalleryUriPattern = "$DeeplinkUri/imageGallery/{$ImageArgumentName}"

private const val NFCRouterRoute = "nfcReader"
private const val NFCRouterUriPattern = "$DeeplinkUri/$NFCRouterRoute"

private const val GameSearchRoute = "gameSearch"
private const val GameSearchUriPattern = "$DeeplinkUri/$GameSearchRoute"

private const val RelatedGamesRoute = "relatedGames/{$AmiiboIdArgumentName}"
private const val RelatedGamesUriPattern = "$DeeplinkUri/$RelatedGamesRoute"

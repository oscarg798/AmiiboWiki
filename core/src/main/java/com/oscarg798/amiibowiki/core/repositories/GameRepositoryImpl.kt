/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.core.repositories

import com.oscarg798.amiibowiki.core.models.CoverUrl
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.models.VideoId
import com.oscarg798.amiibowiki.core.models.WebSiteId
import com.oscarg798.amiibowiki.core.models.WebSiteUrl
import com.oscarg798.amiibowiki.core.network.gameapiquery.APIGameQuery
import com.oscarg798.amiibowiki.core.network.gameapiquery.WhereClause
import com.oscarg798.amiibowiki.core.network.models.APIGame
import com.oscarg798.amiibowiki.core.network.models.APISearchResult
import com.oscarg798.amiibowiki.core.network.services.GameService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class GameRepositoryImpl @Inject constructor(private val gameService: GameService) :
    GameRepository {

    /**
     * This was intend to get a list of games after searching for the name but this will get just the games based on an id sent by the presenter
     * rather tan search based on a name.
     */
    override suspend fun getGames(gameName: String): Collection<Game> = coroutineScope {
        val gameIds = getGamesFromAPI(gameName)
            .mapNotNull { it.game }

        if (gameIds.isEmpty()) {
            return@coroutineScope listOf<Game>()
        }

        val apiGames = gameIds.map {
            async(start = CoroutineStart.LAZY) {
                gameService.getGames(
                    APIGameQuery(
                        whereClause = WhereClause.Id(
                            it
                        )
                    ).toString()
                )
            }
        }.awaitAll().mapNotNull {
            it.firstOrNull()
        }

        val webSites = getWebSites(apiGames)
        val videos = getVideos(apiGames)
        val covers = getCovers(apiGames)

        apiGames.map { apiGame ->
            apiGame.toGame(gameName, covers, webSites, videos)
        }
    }


    override suspend fun searchGame(query: String): Collection<GameSearchResult> =
        getGamesFromAPI(query)
            .mapNotNull {
                if (it.game != null) {
                    it.toGameSearchResult()
                } else {
                    null
                }
            }

    private suspend fun getVideos(apiGames: Collection<APIGame>): Map<Id, VideoId> =
        coroutineScope {
            val videosToRequest = mutableSetOf<Int>()
            apiGames.mapNotNull {
                it.videosId
            }.forEach {
                videosToRequest.addAll(it)
            }

            videosToRequest.map { videoId ->
                async(start = CoroutineStart.LAZY) {
                    gameService.getGameVideos(APIGameQuery(whereClause = WhereClause.Id(videoId)).toString())
                }
            }.awaitAll().mapNotNull {
                it.firstOrNull()
            }.map { apiWebSite ->
                apiWebSite.id to apiWebSite.videoId
            }.toMap()
        }

    private suspend fun getWebSites(apiGames: Collection<APIGame>): Map<WebSiteId, WebSiteUrl> =
        coroutineScope {
            val webSitesToRequest = mutableSetOf<Int>()
            apiGames.mapNotNull {
                it.webSiteIds
            }.forEach {
                webSitesToRequest.addAll(it)
            }

            webSitesToRequest.map { webSiteId ->
                async(start = CoroutineStart.LAZY) {
                    gameService.getWebSites(APIGameQuery(whereClause = WhereClause.Id(webSiteId)).toString())
                }
            }.awaitAll().mapNotNull {
                it.firstOrNull()
            }.map { apiWebSite ->
                apiWebSite.id to apiWebSite.url
            }.toMap()
        }

    private suspend fun getCovers(apiGames: Collection<APIGame>): Map<Id, CoverUrl> =
        coroutineScope {
            apiGames.filter {
                it.coverId != null
            }.map {
                async(start = CoroutineStart.LAZY) {
                    gameService.getCover(APIGameQuery(whereClause = WhereClause.Id(it.coverId!!)).toString())
                }
            }.awaitAll().mapNotNull {
                it.firstOrNull()
            }.map { apiGameCover ->
                apiGameCover.id to apiGameCover.url
            }.toMap()
        }

    private suspend fun getGamesFromAPI(gameName: String): Collection<APISearchResult> =
        gameService.searchGame(
            APIGameQuery(
                searchClause = gameName,
                limit = MAX_NUMBER_OF_RESULTS
            ).toString()
        )
}

private const val MAX_NUMBER_OF_RESULTS = 50

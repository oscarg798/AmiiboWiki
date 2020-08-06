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

import com.oscarg798.amiibowiki.core.extensions.getOrTransformNetworkException
import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.network.gameapiquery.APIGameQuery
import com.oscarg798.amiibowiki.core.network.gameapiquery.WhereClause
import com.oscarg798.amiibowiki.core.network.models.APISearchResult
import com.oscarg798.amiibowiki.core.network.services.GameService
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(private val gameService: GameService) :
    GameRepository {

    override suspend fun getGame(gameSeries: String, gameId: Id): Game {
        return runCatching {
            val apiGame =
                gameService.getGames(APIGameQuery(whereClause = WhereClause.Id(gameId)).toString())
                    .firstOrNull()
                    ?: throw GameDetailFailure.GameNotFound(gameId)

            val cover = if (apiGame.coverId != null) {
                getCovers(apiGame.coverId)
            } else {
                null
            }

            val video = if (apiGame.videosId != null) {
                getVideos(apiGame.videosId)
            } else {
                null
            }

            val webSite = if (apiGame.webSiteIds != null) {
                getWebSites(apiGame.webSiteIds)
            } else {
                null
            }

            val artworks = if (apiGame.artworks != null) {
                getArtWorks(apiGame.artworks)
            } else {
                null
            }

            val ageRaiting = if (apiGame.ageRatings != null) {
                getAgeRatings(apiGame.ageRatings)
            } else {
                null
            }

            val screenshots = if (apiGame.screenshots != null) {
                getScreenshots(apiGame.screenshots)
            } else {
                null
            }

            apiGame.toGame(gameSeries, cover, webSite, video, artworks, ageRaiting, screenshots)
        }.getOrTransformNetworkException { networkException ->
            GameDetailFailure.DateSourceError(gameId, networkException)
        }
    }

    override suspend fun searchGame(query: String): Collection<GameSearchResult> = runCatching {
        getGamesFromAPI(query)
    }.getOrTransformNetworkException { networkException ->
        SearchGameFailure.DateSourceError(query, networkException)
    }.filter {
        it.game != null
    }.map { it.toGameSearchResult() }

    private suspend fun getScreenshots(screenshots: Set<Int>) =
        gameService.getScreenshots(APIGameQuery(whereClause = WhereClause.In(screenshots)).toString())

    private suspend fun getArtWorks(artWorksId: Set<Int>) =
        gameService.getArtworks(APIGameQuery(whereClause = WhereClause.In(artWorksId)).toString())

    private suspend fun getVideos(videosId: Set<Int>) =
        gameService.getGameVideos(APIGameQuery(whereClause = WhereClause.In(videosId)).toString())

    private suspend fun getWebSites(webSitesId: Set<Int>) =
        gameService.getWebSites(APIGameQuery(whereClause = WhereClause.In(webSitesId)).toString())

    private suspend fun getCovers(coverId: Int) =
        gameService.getCover(APIGameQuery(whereClause = WhereClause.Id(coverId)).toString()).first()

    private suspend fun getAgeRatings(ageRaitingIds: Set<Int>) =
        gameService.getAgeRatings(APIGameQuery(whereClause = WhereClause.In(ageRaitingIds)).toString())

    private suspend fun getGamesFromAPI(gameName: String): Collection<APISearchResult> =
        gameService.searchGame(
            APIGameQuery(
                searchClause = gameName,
                limit = MAX_NUMBER_OF_RESULTS
            ).toString()
        )
}

private const val MAX_NUMBER_OF_RESULTS = 100

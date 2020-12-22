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

package com.oscarg798.amiibowiki.core.network.services

import com.oscarg798.amiibowiki.core.network.models.APIAgeRating
import com.oscarg798.amiibowiki.core.network.models.APIArtworks
import com.oscarg798.amiibowiki.core.network.models.APIGame
import com.oscarg798.amiibowiki.core.network.models.APIGameCover
import com.oscarg798.amiibowiki.core.network.models.APIGameVideo
import com.oscarg798.amiibowiki.core.network.models.APIScreenshots
import com.oscarg798.amiibowiki.core.network.models.APISearchResult
import com.oscarg798.amiibowiki.core.network.models.APIWebsite
import com.oscarg798.lomeno.event.NetworkTrackingEvent
import retrofit2.http.Body
import retrofit2.http.POST

interface GameService {

    @POST(GAMES_PATH)
    suspend fun getGames(@Body gameQuery: String): List<APIGame>

    @NetworkTrackingEvent(SEARCH_GAME_EVENT_NAME)
    @POST(SEARCH_PATH)
    suspend fun searchGame(@Body searchQuery: String): List<APISearchResult>

    @POST(GAME_VIDEOS_PATH)
    suspend fun getGameVideos(@Body searchQuery: String): List<APIGameVideo>

    @POST(WEB_SITES_PATH)
    suspend fun getWebSites(@Body searchQuery: String): List<APIWebsite>

    @POST(COVER_PATH)
    suspend fun getCover(@Body searchQuery: String): List<APIGameCover>

    @POST(ARTWORKS_PATH)
    suspend fun getArtworks(@Body searchQuery: String): List<APIArtworks>

    @POST(AGE_RAITING_PATH)
    suspend fun getAgeRatings(@Body searchQuery: String): List<APIAgeRating>

    @POST(SCREEN_SHOT_PATH)
    suspend fun getScreenshots(@Body searchQuery: String): List<APIScreenshots>
}

private const val SEARCH_GAME_EVENT_NAME = "POST_SEARCH_GAME"
private const val AGE_RAITING_PATH = "/v4/age_ratings"
private const val ARTWORKS_PATH = "/v4/artworks"
private const val COVER_PATH = "/v4/covers"
private const val SCREEN_SHOT_PATH = "/v4/screenshots"
private const val WEB_SITES_PATH = "/v4/websites"
private const val GAME_VIDEOS_PATH = "/v4/game_videos"
private const val SEARCH_PATH = "/v4/search"
private const val GAMES_PATH = "/v4/games"

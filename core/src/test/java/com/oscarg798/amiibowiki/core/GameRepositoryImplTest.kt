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

package com.oscarg798.amiibowiki.core

import com.oscarg798.amiibowiki.core.models.AgeRating
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory
import com.oscarg798.amiibowiki.core.models.Rating
import com.oscarg798.amiibowiki.core.network.models.APIAgeRating
import com.oscarg798.amiibowiki.core.network.models.APIArtworks
import com.oscarg798.amiibowiki.core.network.models.APIGame
import com.oscarg798.amiibowiki.core.network.models.APIGameCover
import com.oscarg798.amiibowiki.core.network.models.APIGameVideo
import com.oscarg798.amiibowiki.core.network.models.APIScreenshots
import com.oscarg798.amiibowiki.core.network.models.APIWebsite
import com.oscarg798.amiibowiki.core.network.services.GameService
import com.oscarg798.amiibowiki.core.persistence.dao.AgeRatingDAO
import com.oscarg798.amiibowiki.core.persistence.dao.GameDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAgeRating
import com.oscarg798.amiibowiki.core.persistence.models.DBGame
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.core.repositories.GameRepositoryImpl
import com.oscarg798.amiibowiki.core.persistence.sharepreferences.SharedPreferencesWrapper
import com.oscarg798.amiibowiki.testutils.extensions.coVerifyWasNotCalled
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class GameRepositoryImplTest {

    private val gameDAO = relaxedMockk<GameDAO>()
    private val ageRatingDAO = relaxedMockk<AgeRatingDAO>()
    private val gameService = relaxedMockk<GameService>()
    private val sharedPreferencesWrapper = relaxedMockk<SharedPreferencesWrapper>()

    private lateinit var gameRepository: GameRepository

    @Before
    fun setup() {
        coEvery { gameDAO.getGameById(GAME_ID) } answers { DBGAME }
        coEvery { ageRatingDAO.getByGameId(GAME_ID) } answers { flowOf(listOf(DB_AGE_RATING)) }
        coEvery { gameService.getGames(GAME_SEARCH_QUERY) } answers { listOf(API_GAME) }
        coEvery { gameService.getCover(COVER_SEARCH_QUERY) } answers { listOf(API_GAME_COVER) }
        coEvery { gameService.getWebSites(WEBSITE_SEARCH_QUERY) } answers { listOf(API_GAME_WEBSITE) }
        coEvery { gameService.getGameVideos(GAME_VIDEOS_SEARCH_QUERY) } answers {
            listOf(
                API_GAME_VIDEO
            )
        }
        coEvery { gameService.getAgeRatings(GAME_AGE_RATING_SEARCH_QUERY) } answers {
            listOf(
                API_GAME_AGE_RATING
            )
        }
        coEvery { gameService.getArtworks(GAME_API_ARTWORKS_SEARCH_QUERY) } answers {
            listOf(
                API_GAME_ARTWORKS
            )
        }
        coEvery { gameService.getScreenshots(GAME_API_SCREENSHOOTS_SEARCH_QUERY) } answers {
            listOf(
                API_GAME_SCREEN_SHOTS
            )
        }

        gameRepository = GameRepositoryImpl(
            gameDAO,
            ageRatingDAO,
            gameService,
            sharedPreferencesWrapper
        )
    }

    @Test
    fun `given a game id than does not exists in the db when get game is executed then it should get the game from the api and save it`() {
        coEvery { gameDAO.countById(GAME_ID) } answers { 0 }

        val game = runBlocking {
            gameRepository.getGame(GAME_ID)
        }

        game.id shouldBeEqualTo GAME.id
        game.name shouldBeEqualTo GAME.name
        game.category shouldBeEqualTo GAME.category
        game.cover shouldBeEqualTo GAME.cover
        game.summary shouldBeEqualTo GAME.summary
        game.rating shouldBeEqualTo GAME.rating
        game.webSites shouldBeEqualTo GAME.webSites?.toList()
        game.videosId shouldBeEqualTo GAME.videosId?.toList()
        game.artworks shouldBeEqualTo GAME.artworks?.toList()
        game.ageRating shouldBeEqualTo GAME.ageRating?.toList()
        game.screenshots shouldBeEqualTo GAME.screenshots?.toList()

        coVerify {
            gameDAO.countById(GAME_ID)
            gameService.getGames(GAME_SEARCH_QUERY)
            gameService.getCover(COVER_SEARCH_QUERY)
            gameService.getWebSites(WEBSITE_SEARCH_QUERY)
            gameService.getGameVideos(GAME_VIDEOS_SEARCH_QUERY)
            gameService.getAgeRatings(GAME_AGE_RATING_SEARCH_QUERY)
            gameService.getArtworks(GAME_API_ARTWORKS_SEARCH_QUERY)
            gameService.getScreenshots(GAME_API_SCREENSHOOTS_SEARCH_QUERY)
            gameDAO.insertGames(
                match {
                    it.count { dbGame ->
                        dbGame.id == DBGAME.id &&
                            dbGame.name == DBGAME.name &&
                            dbGame.category == DBGAME.category &&
                            dbGame.cover == DBGAME.cover &&
                            dbGame.summary == DBGAME.summary &&
                            dbGame.rating == DBGAME.rating &&
                            dbGame.webSites == DBGAME.webSites?.toList() &&
                            dbGame.videosId == DBGAME.videosId?.toList() &&
                            dbGame.artworks == DBGAME.artworks?.toList() &&
                            dbGame.screenshots == DBGAME.screenshots?.toList()
                    } > 0
                }
            )
            ageRatingDAO.insert(
                match {
                    it.count { dbAgeRaiting ->
                        dbAgeRaiting.gameId == GAME_ID &&
                            dbAgeRaiting.category == 1 &&
                            dbAgeRaiting.rating == 11
                    } > 0
                }
            )
        }

        coVerifyWasNotCalled { gameDAO.getGameById(GAME_ID) }
    }

    @Test
    fun `given a game id present in the db when get game is executed then it should get the games from db and not call the api`() {
        coEvery { gameDAO.countById(GAME_ID) } answers { 1 }

        runBlocking {
            gameRepository.getGame(GAME_ID)
        } shouldBeEqualTo GAME

        coVerify {
            gameDAO.countById(any())
            gameDAO.getGameById(GAME_ID)
            ageRatingDAO.getByGameId(GAME_ID)
        }

        coVerifyWasNotCalled {
            gameService.getGames(any())
            gameService.getCover(any())
            gameService.getWebSites(any())
            gameService.getGameVideos(any())
            gameService.getAgeRatings(any())
            gameService.getArtworks(any())
            gameService.getScreenshots(any())
        }
    }
}

private const val GAME_ID = 1
private const val GAME_SERIES = "GAME_SERIES"

private const val COVER_SEARCH_QUERY = "fields *;where id=3;"
private const val WEBSITE_SEARCH_QUERY = "fields *;where id=(4);"
private const val GAME_VIDEOS_SEARCH_QUERY = "fields *;where id=(5);"
private const val GAME_AGE_RATING_SEARCH_QUERY = "fields *;where id=(7);"
private const val GAME_API_ARTWORKS_SEARCH_QUERY = "fields *;where id=(8);"
private const val GAME_API_SCREENSHOOTS_SEARCH_QUERY = "fields *;where id=(9);"
private const val GAME_SEARCH_QUERY = "fields *;where id=$GAME_ID;"

private val GAME = Game(
    id = GAME_ID,
    name = "name",
    category = GameCategory.Expansion,
    cover = "cover_url",
    summary = "summary",
    rating = 6.6,
    webSites = setOf("web_site_url"),
    videosId = setOf("video_id"),
    artworks = setOf("artworks_url"),
    ageRating = listOf(AgeRating(AgeRatingCategory.ESRB, Rating.ESRBMature)),
    screenshots = setOf("screenshots_url")
)

private val API_GAME = APIGame(
    id = GAME_ID,
    category = 2,
    name = "name",
    coverId = 3,
    summary = "summary",
    webSiteIds = setOf(4),
    videosId = setOf(5),
    rating = 6.6,
    ageRatings = setOf(7),
    artworks = setOf(8),
    screenshots = setOf(9)
)

private val DBGAME = DBGame(
    id = GAME_ID,
    name = "name",
    category = 2,
    cover = "cover_url",
    summary = "summary",
    rating = 6.6,
    webSites = setOf("web_site_url"),
    videosId = setOf("video_id"),
    artworks = setOf("artworks_url"),
    screenshots = setOf("screenshots_url")
)

private val DB_AGE_RATING = DBAgeRating(20, 1, 11, GAME_ID)

private val API_GAME_COVER = APIGameCover(10, "cover_url", GAME_ID)
private val API_GAME_WEBSITE = APIWebsite(11, "web_site_url", true)
private val API_GAME_VIDEO = APIGameVideo(12, "video_id")
private val API_GAME_AGE_RATING = APIAgeRating(1, 11)
private val API_GAME_ARTWORKS = APIArtworks("width", "height", "imageUrl", "artworks_url")
private val API_GAME_SCREEN_SHOTS = APIScreenshots("screenshots_url")

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

package com.oscarg798.amiibowiki.gamedetail

import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.persistence.dao.AgeRatingDAO
import com.oscarg798.amiibowiki.core.persistence.dao.GameDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAgeRating
import com.oscarg798.amiibowiki.core.persistence.models.DBGame
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.BaseUITest
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.Test

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class
)
@HiltAndroidTest
class GameDetailTest : BaseUITest(DISPATCHER) {

    @get:Rule
    val intentTestRule: IntentsTestRule<GameDetailActivity> =
        IntentsTestRule(GameDetailActivity::class.java, true, false)

    @Inject
    lateinit var gameDAO: GameDAO

    @Inject
    lateinit var ageRatingDAO: AgeRatingDAO

    private val gameDetailRobot = GameDetailRobot()

    override fun prepareTest() {
        coEvery { gameDAO.countById(GAME_ID) } answers { 1 }
        coEvery { gameDAO.getGameById(GAME_ID) } answers { DBGAME }
        coEvery { ageRatingDAO.getByGameId(GAME_ID) } answers { flowOf(listOf(DB_AGE_RATING)) }

        intentTestRule.launchActivity(
            Intent().apply {
                putExtra(ARGUMENT_GAME_ID, 1)
            }
        )
    }

    @Test
    fun when_screen_is_shown_then_it_should_show_game_details() {
        gameDetailRobot.isViewDisplayed()
        gameDetailRobot.isGameDetailDisplayed()
    }

    @Test
    fun when_click_on_trailer_then_it_should_show_it() {
        gameDetailRobot.isViewDisplayed()
        gameDetailRobot.showGameTrailer()
        Intents.intended(hasExtra("developer_key", "MOCK_API_KEY"))
    }
}

private const val GAME_SERIES = "Super Mario"
private const val GAME_ID = 1
private val DBGAME = DBGame(
    id = GAME_ID,
    name = "Mario",
    category = 2,
    cover = "cover_url",
    summary = "summary",
    rating = 6.6,
    webSites = setOf("www.google.com"),
    videosId = setOf("//images.igdb.com/igdb/image/upload/t_thumb/fgubhnuapjmdbxwqxhsq.jpg"),
    artworks = setOf("//images.igdb.com/igdb/image/upload/t_thumb/fgubhnuapjmdbxwqxhsq.jpg"),
    screenshots = setOf("//images.igdb.com/igdb/image/upload/t_thumb/fgubhnuapjmdbxwqxhsq.jpg")
)

private val DB_AGE_RATING = DBAgeRating(20, 1, 11, GAME_ID)

private val DISPATCHER = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when {
            request.path!!.contains("/game") -> MockResponse().setResponseCode(200).setBody(
                """
                    []
                """.trimIndent()
            )
            else -> MockResponse().setResponseCode(500)
        }
    }
}

package com.oscarg798.amiibowiki.gamedetail/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

import androidx.compose.ui.test.junit4.createComposeRule
import com.oscarg798.amiibowiki.core.EnvirormentCheckerModule
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.persistence.dao.AgeRatingDAO
import com.oscarg798.amiibowiki.core.persistence.dao.GameDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAgeRating
import com.oscarg798.amiibowiki.core.persistence.models.DBGame
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.ui.GameDetailScreen
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.testrules.MockWebServerTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class,
    EnvirormentCheckerModule::class
)
@HiltAndroidTest
@Ignore("This test needs to be migrated to compose")
internal class GameDetailTest {

    @get:Rule
    val mockWebServerTestRule = MockWebServerTestRule(DISPATCHER)

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var gameDAO: GameDAO

    @Inject
    lateinit var ageRatingDAO: AgeRatingDAO

    private lateinit var viewModel: GameDetailViewModel

    private val gameDetailRobot = GameDetailRobot(composeTestRule)

    private val trailerClickListener = mockk<() -> Unit>()

    @Before
    fun prepareTest() {
        hiltRule.inject()
        coEvery { gameDAO.countById(GAME_ID) } answers { 1 }
        coEvery { gameDAO.getGameById(GAME_ID) } answers { DBGAME }
        coEvery { ageRatingDAO.getByGameId(GAME_ID) } answers { flowOf(listOf(DB_AGE_RATING)) }
        every { trailerClickListener.invoke() } just Runs

//        viewModel = factory.create(GAME_ID)
//
//        composeTestRule.setContent {
//            GameDetailScreen(
//                viewModel = viewModel,
//                coroutineScope = TestCoroutineScope(),
//                onTrailerClicked = trailerClickListener,
//                onBackPressed = { })
//        }
        //viewModel.onWish(GameDetailWish.ShowGameDetail)
    }

    @Test
    fun when_screen_is_shown_then_it_should_show_game_details() {
        composeTestRule.waitForIdle()
        gameDetailRobot.isViewDisplayed()
        gameDetailRobot.isGameDetailDisplayed()
    }

    @Test
    fun when_click_on_trailer_then_it_should_show_it() {
        gameDetailRobot.isViewDisplayed()
        gameDetailRobot.showGameTrailer()
        verify {
            trailerClickListener.invoke()
        }
    }
}

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

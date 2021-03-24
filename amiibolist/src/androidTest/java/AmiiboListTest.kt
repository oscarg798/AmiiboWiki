
/*
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
package com.oscarg798.amiibowiki.amiibolist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.amiibolist.ui.Screen
import com.oscarg798.amiibowiki.core.EnvirormentCheckerModule
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.extensions.createMockResponse
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.idleresources.ComposeNetworkIdlingResource
import com.oscarg798.amiibowiki.testutils.testrules.MockWebServerTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalFoundationApi
@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class,
    EnvirormentCheckerModule::class,
)
@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
class AmiiboListTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockWebServerTestRule = MockWebServerTestRule(DISPATCHER)

    @Inject
    lateinit var factory: AmiiboListViewModel.Factory

    @Inject
    lateinit var amiiboDAO: AmiiboDAO

    private val composeNetworkIdlingResource = ComposeNetworkIdlingResource()
    private val amiiboListRobot = AmiiboListRobot(composeTestRule)
    private val stateHandler = relaxedMockk<SavedStateHandle>()
    private lateinit var viewModel: AmiiboListViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        coEvery { stateHandler.get<AmiiboListViewState>(any()) } answers { null }
        every { amiiboDAO.getAmiibos() } answers { flowOf(listOf(DB_AMIIBO)) }

        viewModel = factory.create(stateHandler)

        composeTestRule.registerIdlingResource(composeNetworkIdlingResource)
        composeTestRule.setContent {
            Screen(viewModel = viewModel, coroutineScope = TestCoroutineScope()) {
            }
        }
    }

    @After
    fun dropdown() {
        composeTestRule.unregisterIdlingResource(composeNetworkIdlingResource)
    }

    @Test
    fun when_is_open_then_it_should_show_amiibos() {
        viewModel.processWish(AmiiboListWish.GetAmiibos)

        composeTestRule.waitForIdle()
        amiiboListRobot.isViewDisplayed()
        amiiboListRobot.areAmiibosDisplayed()
    }
}

private val DB_AMIIBO = DBAmiibo(
    "11", "12", "13", "14", "15", "16", "17", "Mario", DBAMiiboReleaseDate("19", "20", "21", "22")
)
private val DISPATCHER = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            "/api/amiibo/" -> createMockResponse(
                200,
                "{\n" +
                    "    \"amiibo\": [\n" +
                    "        {\n" +
                    "            \"amiiboSeries\": \"Super Smash Bros.\",\n" +
                    "            \"character\": \"Mario\",\n" +
                    "            \"gameSeries\": \"Super Mario\",\n" +
                    "            \"head\": \"00000000\",\n" +
                    "            \"image\": \"https://raw.githubusercontent.com/N3evin/AmiiboAPI/master/images/icon_00000000-00000002.png\",\n" +
                    "            \"name\": \"Mario\",\n" +
                    "            \"release\": {\n" +
                    "                \"au\": \"2014-11-29\",\n" +
                    "                \"eu\": \"2014-11-28\",\n" +
                    "                \"jp\": \"2014-12-06\",\n" +
                    "                \"na\": \"2014-11-21\"\n" +
                    "            },\n" +
                    "            \"tail\": \"00000002\",\n" +
                    "            \"type\": \"Figure\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"
            )
            else -> MockResponse().setResponseCode(500)
        }
    }
}

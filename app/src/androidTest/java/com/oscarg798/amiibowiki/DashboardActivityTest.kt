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

package com.oscarg798.amiibowiki

import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.amiibolist.AmiiboListFragment
import com.oscarg798.amiibowiki.amiibolist.R
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiiboType
import com.oscarg798.amiibowiki.di.AppModule
import com.oscarg798.amiibowiki.navigation.DashboardActivity
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.BaseUITest
import com.oscarg798.amiibowiki.testutils.extensions.createMockResponse
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class,
    AppModule::class
)
@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
class DashboardActivityTest : BaseUITest(DISPATCHER)  {

    private val dashboardTestRobot = DashboardTestRobot()

    @get:Rule
    val intentTestRule: IntentsTestRule<DashboardActivity> =
        IntentsTestRule(DashboardActivity::class.java, true, true)

    @Inject
    lateinit var amiiboDAO: AmiiboDAO

    @Inject
    lateinit var amiiboTypeDAO: AmiiboTypeDAO

    override fun prepareTest() {
        every { amiiboDAO.getAmiibos() } answers { flowOf(listOf(DB_AMIIBO)) }
        every { amiiboTypeDAO.getTypes() } answers { flowOf(DB_AMIIBO_TYPES) }
    }

    @Test
    fun when_view_is_displayed_tabs_should_be_shown(){
        dashboardTestRobot.isViewDisplayed()
    }

    @Test
    fun when_search_is_clicked_then_search_fragment_should_be_shown(){
        dashboardTestRobot.isViewDisplayed()
        dashboardTestRobot.openSearch()
    }

    @Test
    fun when_settings_is_clicked_then_search_fragment_should_be_shown(){
        dashboardTestRobot.isViewDisplayed()
        dashboardTestRobot.openSettings()
    }

    @Test
    fun when_a_filter_is_clicked_then_it_should_show_amiibos_matching_with_the_filter() {
        dashboardTestRobot.isViewDisplayed()
        dashboardTestRobot.clickFilterMenu()
        dashboardTestRobot.clickOnFigureAmiiboTypeFilter()
        dashboardTestRobot.areAmiibosFilteredDisplayed()
    }

    @Test
    fun when_filter_is_applied_and_clear_filter_is_clicked_then_it_should_show_all_the_amiibos() {
        dashboardTestRobot.isViewDisplayed()
        dashboardTestRobot.clickFilterMenu()
        dashboardTestRobot.clickOnFigureAmiiboTypeFilter()
        dashboardTestRobot.areAmiibosFilteredDisplayed()
        dashboardTestRobot.clearFilters()
        dashboardTestRobot.areAmiibosDisplayed()
    }
}

private val DB_AMIIBO = DBAmiibo(
    "11", "12", "13", "14", "15", "16", "17", "Mario", DBAMiiboReleaseDate("19", "20", "21", "22")
)
private val DB_AMIIBO_TYPES = listOf(DBAmiiboType("1", "Figure"))

private val DISPATCHER = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            "/api/amiibo/" -> createMockResponse(200, "{\n" +
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
                "}")
            "/api/amiibo/?type=Figure" -> createMockResponse(200,     "{\n" +
                "    \"amiibo\": [\n" +
                "        {\n" +
                "            \"amiiboSeries\": \"Super Smash Bros.\",\n" +
                "            \"character\": \"Luigi\",\n" +
                "            \"gameSeries\": \"Super Mario\",\n" +
                "            \"head\": \"00000001\",\n" +
                "            \"image\": \"https://raw.githubusercontent.com/N3evin/AmiiboAPI/master/images/icon_00000001-00000003.png\",\n" +
                "            \"name\": \"Luigi\",\n" +
                "            \"release\": {\n" +
                "                \"au\": \"2014-11-29\",\n" +
                "                \"eu\": \"2014-11-28\",\n" +
                "                \"jp\": \"2014-12-06\",\n" +
                "                \"na\": \"2014-11-21\"\n" +
                "            },\n" +
                "            \"tail\": \"00000003\",\n" +
                "            \"type\": \"Figure\"\n" +
                "        }\n" +
                "    ]\n" +
                "}")
            else -> MockResponse().setResponseCode(500)
        }
    }
}


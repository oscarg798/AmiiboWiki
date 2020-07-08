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

package com.oscarg798.amiibowiki.houses

import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.amiibolist.AmiiboListActivity
import com.oscarg798.amiibowiki.testutils.idleresources.NetworkIdlingResourceRule
import com.oscarg798.amiibowiki.testutils.testrules.MockWebServerTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith


@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class AmiiboListTest {

    @get:Rule
    val intentTestRule: IntentsTestRule<AmiiboListActivity> =
        IntentsTestRule(AmiiboListActivity::class.java, true, true)

    @Rule
    @JvmField
    val testRule: TestRule = RuleChain.outerRule(NetworkIdlingResourceRule())
        .around(MockWebServerTestRule(dispather))

    private val amiiboListRobot = AmiiboListRobot()

    @Test
    fun when_is_open_then_it_should_show_amiibos() {
        amiiboListRobot.isViewDisplayed()
        amiiboListRobot.isMarioDisplayed()
    }
}

val dispather = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            "/api/amiibo/" -> MockResponse().setResponseCode(200).setBody(
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
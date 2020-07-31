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

package com.oscarg798.amiibowiki.amiibodetail

import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.testutils.BaseUITest
import com.oscarg798.amiibowiki.testutils.di.TestFeatureFlagHandlerModule
import com.oscarg798.amiibowiki.testutils.di.TestPersistenceModule
import io.mockk.coEvery
import io.mockk.every
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class AmiiboDetailTest : BaseUITest(DISPATCHER) {

    @get:Rule
    val intentTestRule: IntentsTestRule<AmiiboDetailActivity> =
        IntentsTestRule(AmiiboDetailActivity::class.java, true, false)

    private val amiiboListRobot = AmiiboDetailRobot()

    @Before
    fun setup() {
        coEvery { TestPersistenceModule.amiiboDAO.getById(AMIIBO_TAIL) } answers { AMIIBO }
        every {
            TestFeatureFlagHandlerModule.mainFeatureFlagHandler.isFeatureEnabled(
                AmiiboWikiFeatureFlag.ShowRelatedGames
            )
        } answers { true }
        intentTestRule.launchActivity(
            Intent().apply {
                putExtra(TAIL_ARGUMENT, AMIIBO_TAIL)
            }
        )
    }

    @Test
    fun when_view_is_open_then_it_should_show_the_detail() {
        amiiboListRobot.isViewDisplayed()
        amiiboListRobot.isAmiiboDataDisplayed()
    }
}

private const val AMIIBO_TAIL = "1"
private val AMIIBO = DBAmiibo(
    "Super Mario Bros",
    "Mario",
    "Super Mario Bros",
    "14",
    "15",
    "Figure",
    "17",
    "Mario",
    DBAMiiboReleaseDate("19", "20", "21", "22")
)

private val DISPATCHER = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when {
            request.path!!.contains("/search") -> MockResponse().setResponseCode(200).setBody(
                """
                    [
                      {
                        "id": 10458975,
                        "alternative_name": "Mario Golf GB マリオゴルフGB",
                        "game": 135389,
                        "name": "Mario Golf",
                        "published_at": 934243200
                      }
                    ]
                """.trimIndent()
            )
            else -> MockResponse().setResponseCode(500)
        }
    }
}

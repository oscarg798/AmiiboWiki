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

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.di.qualifiers.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.BaseUITest
import com.oscarg798.amiibowiki.testutils.COVER_RESPONSE
import com.oscarg798.amiibowiki.testutils.GAME_COVER_SEARCH_RESPONSE
import com.oscarg798.amiibowiki.testutils.GAME_SEARCH_RESPONSE
import com.oscarg798.amiibowiki.testutils.extensions.createMockResponse
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import javax.inject.Inject
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class
)
@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
class AmiiboDetailTest : BaseUITest(DISPATCHER) {

    @Inject
    lateinit var amiiboDAO: AmiiboDAO

    @Inject
    @MainFeatureFlagHandler
    lateinit var mainFeatureFlagHandler: FeatureFlagHandler

    private val amiiboListRobot = AmiiboDetailRobot()

    override fun prepareTest() {
        coEvery { amiiboDAO.getById(AMIIBO_TAIL) } answers { DB_AMIIBO }
        every {
            mainFeatureFlagHandler.isFeatureEnabled(AmiiboWikiFeatureFlag.ShowRelatedGames)
        } answers { true }

        launchFragmentInContainer<AmiiboDetailFragment>(
            fragmentArgs = Bundle().apply {
                putString(ARGUMENT_TAIL, AMIIBO_TAIL)
            },
            themeResId = R.style.AppTheme
        )
    }

    @Test
    fun when_view_is_open_then_it_should_show_the_detail() {
        amiiboListRobot.isViewDisplayed()
        amiiboListRobot.isAmiiboDataDisplayed()
    }
}

private const val AMIIBO_TAIL = "17"
private val DB_AMIIBO = DBAmiibo(
    "Super Mario Bros",
    "Mario",
    "Super Mario Bros",
    "14",
    "15",
    "Figure",
    AMIIBO_TAIL,
    "Mario",
    DBAMiiboReleaseDate("19", "20", "21", "22")
)

private val DISPATCHER = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when {
            request.path!!.contains("/search") -> createMockResponse(200, GAME_SEARCH_RESPONSE)
            request.path!!.contains("/games") -> createMockResponse(200, GAME_COVER_SEARCH_RESPONSE)
            request.path!!.contains("/covers") -> createMockResponse(200, COVER_RESPONSE)
            else -> MockResponse().setResponseCode(500)
        }
    }
}

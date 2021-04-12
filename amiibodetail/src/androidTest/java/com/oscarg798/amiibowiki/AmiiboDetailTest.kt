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

package com.oscarg798.amiibowiki

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.oscarg798.amiibowiki.amiibodetail.AmiiboDetailViewModel
import com.oscarg798.amiibowiki.amiibodetail.logger.AmiiboDetailLogger
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ViewState
import com.oscarg798.amiibowiki.amiibodetail.ui.AmiiboDetailScreen
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.EnvirormentCheckerModule
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.di.qualifiers.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import javax.inject.Inject
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class,
    EnvirormentCheckerModule::class,
)
@HiltAndroidTest
@Ignore("Kind of broken when i removed assisted inject need to fix it later")
internal class AmiiboDetailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @MainFeatureFlagHandler
    lateinit var mainFeatureFlagHandler: FeatureFlagHandler

    @Inject
    lateinit var amiiboDAO: AmiiboDAO

    @Inject
    lateinit var getAmiiboDetailUseCase: GetAmiiboDetailUseCase

    @Inject
    lateinit var isFeatureEnabledUseCase: IsFeatureEnableUseCase

    @Inject
    lateinit var coroutinesContextProvider: CoroutineContextProvider

    private lateinit var viewModel: AmiiboDetailViewModel

    private val logger: AmiiboDetailLogger = relaxedMockk()

    private val navController: NavController = relaxedMockk()
    private val screenConfigurator: ScreenConfigurator = relaxedMockk()

    private val amiiboListRobot = AmiiboDetailRobot(composeTestRule)
    private val stateHandler = relaxedMockk<SavedStateHandle>()

    @Before
    fun setup() {
        hiltRule.inject()
        coEvery { amiiboDAO.getById(AMIIBO_TAIL) } answers { DB_AMIIBO }
        coEvery { stateHandler.get<ViewState>(any()) } answers { null }
        every { mainFeatureFlagHandler.isFeatureEnabled(AmiiboWikiFeatureFlag.ShowRelatedGames) } answers { true }

        viewModel = AmiiboDetailViewModel(
            handle = stateHandler,
            getAmiiboDetailUseCase = getAmiiboDetailUseCase,
            amiiboDetailLogger = logger,
            isFeatureEnableUseCase = isFeatureEnabledUseCase,
            coroutineContextProvider = coroutinesContextProvider
        )

        composeTestRule.setContent {
            AmiiboDetailScreen(
                viewModel = viewModel,
                amiiboId = AMIIBO_TAIL,
                navController = navController,
                screenConfigurator = screenConfigurator
            )
        }
    }

    @Test
    fun when_show_amiibo_detail_wish_then_it_should_show_the_detail() {
        viewModel.processWish(AmiiboDetailWish.ShowAmiiboDetail(AMIIBO_TAIL))

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

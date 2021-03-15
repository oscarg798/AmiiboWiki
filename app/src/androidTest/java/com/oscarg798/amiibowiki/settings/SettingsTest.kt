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
package com.oscarg798.amiibowiki.settings

import androidx.test.runner.AndroidJUnit4
import com.oscarg798.amiibowiki.HiltTestActivity
import com.oscarg798.amiibowiki.core.EnvirormentChecker
import com.oscarg798.amiibowiki.core.EnvirormentCheckerModule
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.di.qualifiers.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.di.AppModule
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.BaseUITest
import com.oscarg798.amiibowiki.testutils.extensions.launchFragmentInHiltContainer
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import org.junit.Ignore
import javax.inject.Inject
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class,
    EnvirormentCheckerModule::class,
    AppModule::class
)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseUITest() {

    @Inject
    @MainFeatureFlagHandler
    lateinit var mainFeatureFlagHandler: FeatureFlagHandler

    @Inject
    lateinit var envirormentChecker: EnvirormentChecker

    private val settingsRobot = SettingsRobot()

    override fun prepareTest() {
        every { envirormentChecker.invoke() } answers { true }
        coEvery { mainFeatureFlagHandler.isFeatureEnabled(AmiiboWikiFeatureFlag.ShowGameDetail) }
        launchFragmentInHiltContainer<SettingsFragment>(
            HiltTestActivity::class.java,
            themeResId = R.style.AppTheme
        )
    }

    @Ignore("ignoring as it should be in it's own module")
    @Test
    fun when_view_is_shown_then_it_should_include_user_interface_category_with_dark_mode_on_it() {
        settingsRobot.isViewDisplayed()
        settingsRobot.isDarkModePreferenceDisplayed()
        settingsRobot.isDevelopmentActivityDisplayed()
    }

    @Ignore("ignoring as it should be in it's own module")
    @Test
    fun when_dark_mode_preference_is_clicked_then_it_should_show_a_dialog_to_select_mode() {
        settingsRobot.isViewDisplayed()
        settingsRobot.clickDarkModeOption()
        settingsRobot.isDarkModeDialogDisplayed()
    }
}

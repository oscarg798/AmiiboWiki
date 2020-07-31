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

import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest {

    @get:Rule
    val intentTestRule: IntentsTestRule<SettingsActivity> =
        IntentsTestRule(SettingsActivity::class.java, true, true)

    private val settingsRobot = SettingsRobot()

    @Test
    fun when_view_is_shown_then_it_should_include_user_interface_category_with_dark_mode_on_it() {
        settingsRobot.isViewDisplayed()
        settingsRobot.isDarkModePreferenceDisplayed()
        settingsRobot.isDevelopmentActivityDisplayed()
    }

    @Test
    fun when_dark_mode_preference_is_clicked_then_it_should_show_a_dialog_to_select_mode() {
        settingsRobot.isViewDisplayed()
        settingsRobot.clickDarkModeOption()
        settingsRobot.isDarkModeDialogDisplayed()
    }
}

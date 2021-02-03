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

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.oscarg798.amiibowiki.testutils.isViewWithTextDisplayed
import com.oscarg798.amiibowiki.testutils.utils.TestRobot

class SettingsRobot : TestRobot {

    override fun isViewDisplayed() {
        isViewWithTextDisplayed("User Interface")
    }

    fun isDarkModePreferenceDisplayed() {
        isViewWithTextDisplayed("Dark Mode")
    }

    fun isDevelopmentActivityDisplayed() {
        isViewWithTextDisplayed("Development Activity")
    }

    fun isDarkModeDialogDisplayed() {
        isViewWithTextDisplayed("System default")
        isViewWithTextDisplayed("Ligth")
        isViewWithTextDisplayed("Dark")
    }

    fun clickDarkModeOption() {
        Espresso.onView(ViewMatchers.withText("Dark Mode")).perform(ViewActions.click())
    }
}

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


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.oscarg798.amiibowiki.testutils.clickElementWithId
import com.oscarg798.amiibowiki.testutils.idleresources.waitUntilViewIsDisplayed
import com.oscarg798.amiibowiki.testutils.isViewWithTextDisplayed
import com.oscarg798.amiibowiki.testutils.utils.TestRobot

class DashboardTestRobot() : TestRobot {

    override fun isViewDisplayed() {
        waitUntilViewIsDisplayed(ViewMatchers.withText("Amiibos"))
    }

    fun openSettings(){
        clickElementWithId(R.id.navigation_settings)
        isViewWithTextDisplayed("Settings")
        isViewWithTextDisplayed("Search Result Limit")
    }

    fun areAmiibosDisplayed() {
        Espresso.onView(ViewMatchers.withText("Mario")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun clickFilterMenu() {
        Espresso.onView(ViewMatchers.withId(R.id.action_filter)).perform(ViewActions.click())
    }

    fun clickOnFigureAmiiboTypeFilter() {
        waitUntilViewIsDisplayed(ViewMatchers.withText("Clear Filters"))
        Espresso.onView(ViewMatchers.withText("Figure")).perform(ViewActions.click())
    }

    fun areAmiibosFilteredDisplayed() {
        Espresso.onView(ViewMatchers.withText("Luigi")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun clearFilters() {
        clickFilterMenu()
        waitUntilViewIsDisplayed(ViewMatchers.withText("Clear Filters"))
        Espresso.onView(ViewMatchers.withText("Clear Filters")).perform(ViewActions.click())
    }
}

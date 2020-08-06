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

package com.oscarg798.amiibowiki.testutils

import android.annotation.SuppressLint
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun isViewContainingTextDisplayed(text: String, index: Int = FIRST_MATCH_INDEX) {
    Espresso.onView(withIndex(ViewMatchers.withText(text), index))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

fun isViewWithIdDisplayed(id: Int, index: Int = FIRST_MATCH_INDEX) {
    Espresso.onView(withIndex(ViewMatchers.withId(id), index))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
    return object : TypeSafeMatcher<View?>() {
        var currentIndex = 0
        var viewObjHash = 0

        @SuppressLint("DefaultLocale")
        override fun describeTo(description: Description) {
            description.appendText(String.format("with index: %d ", index))
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            if (matcher.matches(view) && currentIndex++ == index) {
                viewObjHash = view.hashCode()
            }
            return view.hashCode() === viewObjHash
        }
    }
}

private const val FIRST_MATCH_INDEX = 0

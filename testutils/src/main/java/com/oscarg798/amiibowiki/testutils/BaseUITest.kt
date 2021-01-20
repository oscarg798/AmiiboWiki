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

import com.oscarg798.amiibowiki.testutils.idleresources.NetworkIdlingResourceRule
import com.oscarg798.amiibowiki.testutils.testrules.MockWebServerTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

abstract class BaseUITest(dispatcher: Dispatcher = defaultDispatcher) {

    val hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val testRule: TestRule = RuleChain.outerRule(hiltRule)
        .around(MockDependenciesTestRule(this))
        .around(MockWebServerTestRule(dispatcher))
        .around(NetworkIdlingResourceRule())

    abstract fun prepareTest()
}

private val defaultDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse = MockResponse().setResponseCode(500).setBody("Not Found dispatch response")
}

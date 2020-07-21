package com.oscarg798.amiibowiki.testutils.idleresources

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.oscarg798.amiibowiki.testutils.di.TestNetworkModule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NetworkIdlingResourceRule : TestRule {

    private val resource: IdlingResource = OkHttp3IdlingResource.create(
        "okhttp",
        TestNetworkModule.okHttpClient
    )

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                IdlingRegistry.getInstance().register(resource)
                base.evaluate()
                IdlingRegistry.getInstance().unregister(resource)
            }
        }
    }
}

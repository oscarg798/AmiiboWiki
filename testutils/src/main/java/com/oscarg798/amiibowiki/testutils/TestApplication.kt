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

import android.app.Application
import com.oscarg798.amiibowiki.core.di.CoreComponent
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.models.Flavor
import com.oscarg798.amiibowiki.testutils.di.DaggerTestCoreComponent
import com.oscarg798.amiibowiki.testutils.di.TestFeatureFlagHandlerModule
import com.oscarg798.amiibowiki.testutils.testrules.MOCK_WEB_SERVER_URL
import com.oscarg798.flagly.developeroptions.FeatureHandleResourceProvider
import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlag
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagProvider

class TestApplication : Application(), CoreComponentProvider, FeatureHandleResourceProvider {

    override fun provideCoreComponent(): CoreComponent {
        return DaggerTestCoreComponent.factory()
            .create(
                this,
                Config(
                    MOCK_WEB_SERVER_URL,
                    MOCK_WEB_SERVER_URL,
                    Flavor.Debug,
                    MOCK_API_KEY,
                    MOCK_API_KEY
                )
            )
    }

    override fun getFeatureFlagProvider(): FeatureFlagProvider = object : FeatureFlagProvider {

        override fun provideAppSupportedFeatureflags(): Collection<FeatureFlag> {
            return AmiiboWikiFeatureFlag.getValues()
        }
    }

    override fun getLocalFeatureflagHandler(): DynamicFeatureFlagHandler =
        TestFeatureFlagHandlerModule.localFeatureFlagHandler

    override fun getRemoteFeatureFlagHandler(): FeatureFlagHandler =
        TestFeatureFlagHandlerModule.firebaseFeatureFlagHandler
}

private const val MOCK_API_KEY = "MOCK_API_KEY"

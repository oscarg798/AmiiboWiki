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

import android.app.Application
import com.oscarg798.amiibowiki.core.di.qualifiers.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.di.qualifiers.RemoteFeatureFlagHandler
import com.oscarg798.flagly.developeroptions.FeatureHandleResourceProvider
import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AmiiboWikiApplication : Application(), FeatureHandleResourceProvider {

    @Inject
    @MainFeatureFlagHandler
    lateinit var featureFlagHandler: FeatureFlagHandler

    @Inject
    lateinit var dynamicFeatureFlag: DynamicFeatureFlagHandler

    @Inject
    @RemoteFeatureFlagHandler
    lateinit var remoteFeatureFlagProvider: FeatureFlagHandler

    override fun getFeatureFlagProvider(): FeatureFlagProvider =
        featureFlagHandler as FeatureFlagProvider

    override fun getLocalFeatureflagHandler(): DynamicFeatureFlagHandler = dynamicFeatureFlag

    override fun getRemoteFeatureFlagHandler(): FeatureFlagHandler = remoteFeatureFlagProvider
}

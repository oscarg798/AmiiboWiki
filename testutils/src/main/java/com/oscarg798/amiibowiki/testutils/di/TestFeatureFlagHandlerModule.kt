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

package com.oscarg798.amiibowiki.testutils.di

import com.oscarg798.amiibowiki.core.di.qualifier.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.di.qualifier.RemoteFeatureFlagHandler
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import com.oscarg798.flagly.remoteconfig.RemoteConfig
import dagger.Module
import dagger.Provides

@Module
object TestFeatureFlagHandlerModule {

    val firebaseFeatureFlagHandler = relaxedMockk<FeatureFlagHandler>()
    val localFeatureFlagHandler = relaxedMockk<DynamicFeatureFlagHandler>()
    val mainFeatureFlagHandler = relaxedMockk<FeatureFlagHandler>()
    val remoteConfig = relaxedMockk<RemoteConfig>()

    @Provides
    fun provideRemoteConfig() = remoteConfig

    @RemoteFeatureFlagHandler
    @Provides
    fun provideRemoteFeatureFlagHandler(): FeatureFlagHandler = firebaseFeatureFlagHandler

    @Provides
    fun provideDynamicFeatureFlag(): DynamicFeatureFlagHandler = localFeatureFlagHandler

    @MainFeatureFlagHandler
    @Provides
    fun provideAmiiboWikiFeatureFlagHandler(): FeatureFlagHandler = mainFeatureFlagHandler
}

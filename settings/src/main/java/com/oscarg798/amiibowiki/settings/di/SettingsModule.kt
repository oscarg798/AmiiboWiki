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

package com.oscarg798.amiibowiki.settings.di

import com.oscarg798.amiibowiki.core.mvi.Reducer

import com.oscarg798.amiibowiki.settings.featurepoint.DarkModelPreferenceFeatureFactory
import com.oscarg798.amiibowiki.settings.featurepoint.DevelopmentActivityFeatureFactory
import com.oscarg798.amiibowiki.settings.featurepoint.MaxNumberOfResultInSearchFeatureFactory
import com.oscarg798.amiibowiki.settings.featurepoint.PreferenceFeaturePoint
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsReducer
import com.oscarg798.amiibowiki.settings.mvi.SettingsResult
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import com.oscarg798.amiibowiki.settings.repositories.SettingsRepository
import com.oscarg798.amiibowiki.settings.repositories.SettingsRepositoryImpl
import com.oscarg798.flagly.featurepoint.SuspendFeaturePoint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
object SettingsModule {

    @ViewModelScoped
    @Provides
    fun bindFeaturePoint(
        developmentActivityFeatureFactory: DevelopmentActivityFeatureFactory,
        darkModelPreferenceFeatureFactory: DarkModelPreferenceFeatureFactory,
        maxNumberOfResultInSearchFeatureFactory: MaxNumberOfResultInSearchFeatureFactory
    ): SuspendFeaturePoint<PreferenceBuilder, Unit> =
        PreferenceFeaturePoint(
            setOf(
                darkModelPreferenceFeatureFactory,
                maxNumberOfResultInSearchFeatureFactory,
                developmentActivityFeatureFactory
            )
        )

    @ViewModelScoped
    @Provides
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository =
        settingsRepositoryImpl

    @ViewModelScoped
    @Provides
    fun provideSettingsReducer(settingsReducer: SettingsReducer): Reducer<@JvmSuppressWildcards SettingsResult, SettingsViewState> =
        settingsReducer
}

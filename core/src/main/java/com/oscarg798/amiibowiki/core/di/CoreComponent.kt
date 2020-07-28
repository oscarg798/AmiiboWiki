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

package com.oscarg798.amiibowiki.core.di

import android.app.Application
import android.content.Context
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.di.qualifier.MainFeatureFlagHandler
import com.oscarg798.amiibowiki.core.di.qualifier.RemoteFeatureFlagHandler
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboTypeUseCase
import com.oscarg798.amiibowiki.core.usecases.GetDefaultAmiiboTypeUseCase
import com.oscarg798.amiibowiki.core.usecases.GetGamesUseCase
import com.oscarg798.amiibowiki.core.usecases.SearchGameByAmiiboUseCase
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboApiQualifier
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiQualifier
import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import com.oscarg798.lomeno.logger.Logger
import dagger.BindsInstance
import dagger.Component
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
@CoreScope
@Component(
    modules = [
        CoreModule::class, ViewModelsModule::class, NetworkModule::class,
        PersistenceModule::class, LoggerModule::class, FeatureFlagHandlerModule::class
    ]
)
interface CoreComponent {

    @Component.Factory
    interface Builder {

        fun create(
            @BindsInstance context: Context,
            @BindsInstance config: Config
        ): CoreComponent
    }

    fun inject(application: Application)

    @RemoteFeatureFlagHandler
    fun provideRemoteFeatureFlagHandler(): FeatureFlagHandler

    fun provideDynamicFeatureFlag(): DynamicFeatureFlagHandler

    @MainFeatureFlagHandler
    fun provideAmiiboWikiFeatureFlagHandler(): FeatureFlagHandler

    @AmiiboApiQualifier
    fun provideAmiiboAPIRetrofit(): Retrofit
    @GameApiQualifier
    fun provideGameAPIRetrofit(): Retrofit

    fun provideCoroutineContextProvider(): CoroutineContextProvider
    fun provideLocale(): Locale

    fun provideAmiiboService(): AmiiboService
    fun provideAmiiboTypeService(): AmiiboTypeService

    fun provideAmiiboRepository(): AmiiboRepository
    fun provideAmiiboTypeRepository(): AmiiboTypeRepository

    fun provideGetAmiiboTypeUseCase(): GetAmiiboTypeUseCase
    fun provideGetDefaulAmiiboTypeUseCase(): GetDefaultAmiiboTypeUseCase
    fun provideUpdateAmiiboTypeUseCase(): UpdateAmiiboTypeUseCase
    fun provideGetGamesUseCase(): GetGamesUseCase
    fun provideSearchGameUseCase(): SearchGameByAmiiboUseCase

    fun provideContext(): Context
    fun provideLogger(): Logger

    fun provideAmiiboTypeDao(): AmiiboTypeDAO
    fun provideAmiiboDAO(): AmiiboDAO
}

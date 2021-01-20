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

package com.oscarg798.amiibowiki.core.di.modules

import com.oscarg798.amiibowiki.core.di.qualifiers.DatabaseName
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.sharepreferences.AmiiboWikiPreferenceWrapper
import com.oscarg798.amiibowiki.core.persistence.sharepreferences.SharedPreferencesWrapper
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepositoryImpl
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepositoryImpl
import com.oscarg798.amiibowiki.core.repositories.GameAuthRepository
import com.oscarg798.amiibowiki.core.repositories.GameAuthRepositoryImpl
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.core.repositories.GameRepositoryImpl
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.utils.ResourceProvider
import com.oscarg798.amiibowiki.core.utils.StringResourceProvider
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIConsumer
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @DatabaseName
    @Provides
    @Singleton
    fun provideDatabaseName(config: Config): String = config.databaseName

    @Singleton
    @Provides
    fun provideLocale(): Locale = Locale.getDefault()

    @Singleton
    @Provides
    fun provideCoroutineContextProvider(): CoroutineContextProvider =
        CoroutineContextProvider(
            Dispatchers.Main,
            Dispatchers.IO
        )

    @Reusable
    @Provides
    fun provideAmiiboTypeService(@AmiiboAPIConsumer retrofit: Retrofit): AmiiboTypeService =
        retrofit.create(AmiiboTypeService::class.java)

    @Singleton
    @Provides
    fun provideAmiiboRepository(amiiboRepositoryImpl: AmiiboRepositoryImpl): AmiiboRepository =
        amiiboRepositoryImpl

    @Singleton
    @Provides
    fun provideAmiiboTypeRepository(amiiboTypeRepositoryImpl: AmiiboTypeRepositoryImpl): AmiiboTypeRepository =
        amiiboTypeRepositoryImpl

    @Singleton
    @Provides
    fun provideGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository =
        gameRepositoryImpl

    @Singleton
    @Provides
    fun provideStringResourceProvider(stringResourceProvider: StringResourceProvider): ResourceProvider<String> =
        stringResourceProvider

    @Singleton
    @Provides
    fun providePreferenceWrapper(amiiboWikiPreferenceWrapper: AmiiboWikiPreferenceWrapper): SharedPreferencesWrapper =
        amiiboWikiPreferenceWrapper

    @Singleton
    @Provides
    fun provideGameAuthRepository(gameAuthRepositoryImpl: GameAuthRepositoryImpl): GameAuthRepository = gameAuthRepositoryImpl
}

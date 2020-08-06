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

import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.network.services.GameService
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepositoryImpl
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepositoryImpl
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.core.repositories.GameRepositoryImpl
import com.oscarg798.amiibowiki.core.sharepreferences.AmiiboWikiPreferenceWrapper
import com.oscarg798.amiibowiki.core.sharepreferences.SharedPreferencesWrapper
import com.oscarg798.amiibowiki.core.utils.ResourceProvider
import com.oscarg798.amiibowiki.core.utils.StringResourceProvider
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboApiQualifier
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIKey
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiQualifier
import dagger.Module
import dagger.Provides
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

@Module
object CoreModule {

    @CoreScope
    @Provides
    fun provideLocale(): Locale = Locale.getDefault()

    @CoreScope
    @Provides
    fun provideCoroutineContextProvider(): CoroutineContextProvider =
        CoroutineContextProvider(Dispatchers.Main, Dispatchers.IO)

    @CoreScope
    @Provides
    fun provideAmiibo(@AmiiboApiQualifier retrofit: Retrofit): AmiiboService =
        retrofit.create(AmiiboService::class.java)

    @AmiiboAPIBaseUrl
    @CoreScope
    @Provides
    fun provideAmiiboAPIBaseUrl(config: Config) = config.amiiboBaseUrl

    @GameAPIBaseUrl
    @CoreScope
    @Provides
    fun provideGameAPIBaseUrl(config: Config) = config.gameBaseUrl

    @CoreScope
    @Provides
    fun provideAmiiboTypeService(@AmiiboApiQualifier retrofit: Retrofit): AmiiboTypeService =
        retrofit.create(AmiiboTypeService::class.java)

    @CoreScope
    @Provides
    fun provideGameService(@GameApiQualifier retrofit: Retrofit): GameService =
        retrofit.create(GameService::class.java)

    @CoreScope
    @Provides
    fun provideAmiiboRepository(amiiboRepositoryImpl: AmiiboRepositoryImpl): AmiiboRepository =
        amiiboRepositoryImpl

    @CoreScope
    @Provides
    fun provideAmiiboTypeRepository(amiiboTypeRepositoryImpl: AmiiboTypeRepositoryImpl): AmiiboTypeRepository =
        amiiboTypeRepositoryImpl

    @CoreScope
    @Provides
    fun provideGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository =
        gameRepositoryImpl

    @CoreScope
    @Provides
    fun provideStringResourceProvider(stringResourceProvider: StringResourceProvider): ResourceProvider<String> =
        stringResourceProvider

    @CoreScope
    @Provides
    fun providePreferenceWrapper(amiiboWikiPreferenceWrapper: AmiiboWikiPreferenceWrapper): SharedPreferencesWrapper =
        amiiboWikiPreferenceWrapper

    @GameAPIKey
    @CoreScope
    @Provides
    fun provideGameAPIKey(config: Config) = config.gameAPIKey
}

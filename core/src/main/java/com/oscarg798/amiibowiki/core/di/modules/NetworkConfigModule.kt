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

import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.services.AuthService
import com.oscarg798.amiibowiki.core.network.services.GameService
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.AuthAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIConfig
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiConsumer
import com.oscarg798.amiibowiki.core.network.interceptors.APIConfig
import com.oscarg798.amiibowiki.core.network.interceptors.APIKeyInterceptor
import com.oscarg798.amiibowiki.core.persistence.sharepreferences.SharedPreferencesWrapper
import com.oscarg798.amiibowiki.core.repositories.GameAuthRepository
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIConsumer
import com.oscarg798.amiibowiki.network.di.qualifiers.AuthAPIConsumer
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkConfigModule {

    @GameAPIConfig
    @Singleton
    @Provides
    fun provideGameAPIConfig(config: Config) =
        APIConfig(
            clientId = config.gameClientId
        )

    @GameAPIInterceptor
    @Reusable
    @Provides
    fun provideAPIKeyInterceptor(
        @GameAPIConfig apiConfig: APIConfig,
        gameAuthRepository: GameAuthRepository,
    ): Interceptor = APIKeyInterceptor(
        apiConfig,
        gameAuthRepository
    )

    @Singleton
    @Provides
    fun provideGameService(@GameApiConsumer retrofit: Retrofit): GameService =
        retrofit.create(GameService::class.java)

    @Singleton
    @Provides
    fun provideAmiiboService(@AmiiboAPIConsumer retrofit: Retrofit): AmiiboService =
        retrofit.create(AmiiboService::class.java)

    @AmiiboAPIBaseUrl
    @Singleton
    @Provides
    fun provideAmiiboAPIBaseUrl(config: Config) = config.amiiboBaseUrl

    @GameAPIBaseUrl
    @Singleton
    @Provides
    fun provideGameAPIBaseUrl(config: Config) = config.gameBaseUrl

    @AuthAPIBaseUrl
    @Singleton
    @Provides
    fun provideAuthAPIBaseUrl(config: Config) = config.authAPIBaseUrl

    @Singleton
    @Provides
    fun provideAuthService(@AuthAPIConsumer retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)
}

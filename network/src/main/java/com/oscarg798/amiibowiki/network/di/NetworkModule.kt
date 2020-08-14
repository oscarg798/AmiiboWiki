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

package com.oscarg798.amiibowiki.network.di

import com.google.gson.GsonBuilder
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboApiQualifier
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIKey
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiQualifier
import com.oscarg798.amiibowiki.network.interceptors.APIKeyInterceptor
import com.oscarg798.amiibowiki.network.interceptors.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Reusable
    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        val gson = GsonBuilder()
            .create()
        return GsonConverterFactory.create(gson)
    }

    @Reusable
    @Provides
    fun provideLogginInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return loggingInterceptor
    }

    @Reusable
    @Provides
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkLoggerInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(ErrorInterceptor())
            .addInterceptor(loggingInterceptor)
            .addInterceptor(networkLoggerInterceptor)

        return builder.build()
    }

    @AmiiboApiQualifier
    @Reusable
    @Provides
    fun provideAmiiboAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        httpClient: OkHttpClient,
        @AmiiboAPIBaseUrl
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(httpClient)
            .build()
    }

    @Reusable
    @Provides
    fun provideAPIKeyInterceptor(@GameAPIKey apiKey: String) = APIKeyInterceptor(apiKey)

    @GameApiQualifier
    @Reusable
    @Provides
    fun provideGameAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        httpClient: OkHttpClient,
        apiKeyInterceptor: APIKeyInterceptor,
        @GameAPIBaseUrl
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(
                httpClient.newBuilder().addInterceptor(apiKeyInterceptor).build()
            )
            .build()
    }
}

private const val TIME_OUT_SECONDS = 30L

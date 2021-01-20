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
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIClient
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIConsumer
import com.oscarg798.amiibowiki.network.di.qualifiers.AuthAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.AuthAPIClient
import com.oscarg798.amiibowiki.network.di.qualifiers.AuthAPIConsumer
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIClient
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIInterceptor
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiConsumer
import com.oscarg798.amiibowiki.network.di.qualifiers.NetworkTrackerInterceptor
import com.oscarg798.amiibowiki.network.interceptors.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val httpLoggingInterceptor: Interceptor by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

        loggingInterceptor
    }

    private val errorInterceptor: ErrorInterceptor by lazy { ErrorInterceptor() }

    @Reusable
    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        val gson = GsonBuilder()
            .create()
        return GsonConverterFactory.create(gson)
    }

    private fun getHttpClientBuilder(
        @NetworkTrackerInterceptor
        networkLoggerInterceptor: Interceptor,
    ) = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(networkLoggerInterceptor)
        .addInterceptor(errorInterceptor)

    @AmiiboAPIClient
    @Reusable
    @Provides
    fun provideAmiiboHttpClient(
        @NetworkTrackerInterceptor
        networkLoggerInterceptor: Interceptor
    ): OkHttpClient = getHttpClientBuilder(networkLoggerInterceptor).build()

    @GameAPIClient
    @Reusable
    @Provides
    fun provideGameHttpClient(
        @NetworkTrackerInterceptor
        networkLoggerInterceptor: Interceptor,
        @GameAPIInterceptor
        apiKeyInterceptor: Interceptor
    ): OkHttpClient =
        getHttpClientBuilder(networkLoggerInterceptor)
            .addInterceptor(apiKeyInterceptor).build()

    @AuthAPIClient
    @Reusable
    @Provides
    fun provideAuthHttpClient(
        @NetworkTrackerInterceptor
        networkLoggerInterceptor: Interceptor
    ): OkHttpClient =
        getHttpClientBuilder(networkLoggerInterceptor).build()

    @AmiiboAPIConsumer
    @Reusable
    @Provides
    fun provideAmiiboAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @AmiiboAPIClient
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

    @GameApiConsumer
    @Reusable
    @Provides
    fun provideGameAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @GameAPIClient
        httpClient: OkHttpClient,
        @GameAPIBaseUrl
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(gsonConverterFactory)
        .client(httpClient)
        .build()

    @AuthAPIConsumer
    @Reusable
    @Provides
    fun provideAuthAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @AuthAPIClient
        httpClient: OkHttpClient,
        @AuthAPIBaseUrl
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(gsonConverterFactory)
        .client(httpClient)
        .build()
}

private const val TIME_OUT_SECONDS = 30L

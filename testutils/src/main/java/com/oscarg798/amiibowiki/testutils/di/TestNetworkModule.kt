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

import com.google.gson.GsonBuilder
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.AmiiboApiQualifier
import com.oscarg798.amiibowiki.network.di.qualifiers.GameAPIBaseUrl
import com.oscarg798.amiibowiki.network.di.qualifiers.GameApiQualifier
import com.oscarg798.amiibowiki.network.interceptors.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
object TestNetworkModule {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(ErrorInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }

    @Reusable
    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        val gson = GsonBuilder()
            .create()
        return GsonConverterFactory.create(gson)
    }

    @AmiiboApiQualifier
    @Reusable
    @Provides
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @AmiiboAPIBaseUrl
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @GameApiQualifier
    @Reusable
    @Provides
    fun provideGameAPIRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @GameAPIBaseUrl
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }
}

private const val TIME_OUT_SECONDS = 30L

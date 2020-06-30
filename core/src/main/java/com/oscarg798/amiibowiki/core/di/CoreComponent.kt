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

import android.content.Context
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.CoreAmiiboDatabase
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.network.di.NetworkModule
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit
import java.util.*

@CoreScope
@Component(modules = [CoreModule::class, ViewModelsModule::class, NetworkModule::class])
interface CoreComponent {

    @Component.Factory
    interface Builder {

        fun create(
            @BindsInstance context: Context,
            @BindsInstance config: Config
        ): CoreComponent
    }

    fun provideRetrofit(): Retrofit
    fun provideCoroutineContextProvider(): CoroutineContextProvider
    fun provideLocale(): Locale
    fun provideAmiiboService(): AmiiboService
    fun provideAmiiboRepository(): AmiiboRepository
    fun provideAmiiboTypeService(): AmiiboTypeService
    fun provideAmiiboTypeDao(): AmiiboTypeDAO

}
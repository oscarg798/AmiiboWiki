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

package com.oscarg798.amiibowiki.splash.di

import androidx.lifecycle.ViewModel
import com.oscarg798.amiibowiki.core.ViewModelKey
import com.oscarg798.amiibowiki.splash.SplashLogger
import com.oscarg798.amiibowiki.splash.SplashLoggerImpl
import com.oscarg798.amiibowiki.splash.SplashViewModel
import com.oscarg798.lomeno.logger.Logger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview



@Module
object SplahModule {

    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    @Provides
    fun provideHouseViewModel(amiiboListViewModel: SplashViewModel): ViewModel = amiiboListViewModel

    @SplashScope
    @Provides
    fun provideSplashLogger(logger: Logger): SplashLogger = SplashLoggerImpl(logger)

}

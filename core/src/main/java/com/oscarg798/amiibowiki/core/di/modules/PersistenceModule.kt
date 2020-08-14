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

import android.content.Context
import androidx.room.Room
import com.oscarg798.amiibowiki.core.persistence.dao.AgeRatingDAO
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.dao.GameDAO
import com.oscarg798.amiibowiki.core.persistence.database.CoreAmiiboDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PersistenceModule {

    /**
     * We are not saving  states, just downloaded data
     * so  for now we will not provider any migration
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CoreAmiiboDatabase {
        return Room.databaseBuilder(
            context,
            CoreAmiiboDatabase::class.java, "core_amiibo_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAmiiboTypeDao(database: CoreAmiiboDatabase): AmiiboTypeDAO = database.amiiboTypeDAO()

    @Singleton
    @Provides
    fun provideAmiiboDAO(database: CoreAmiiboDatabase): AmiiboDAO = database.amiiboDAO()
    
    @Singleton
    @Provides
    fun provideGameDAO(database: CoreAmiiboDatabase): GameDAO = database.gameDAO()

    @Singleton
    @Provides
    fun provideAgeRatingDAO(database: CoreAmiiboDatabase): AgeRatingDAO = database.ageRatingDAO()
}

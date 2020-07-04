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

package com.oscarg798.amiibowiki.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oscarg798.amiibowiki.core.persistence.models.AMIIBO_TABLE_NAME
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import kotlinx.coroutines.flow.Flow

@Dao
interface AmiiboDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(dbAmiibos: List<DBAmiibo>)

    @Query("select * from $AMIIBO_TABLE_NAME")
    fun getAmiibos(): Flow<List<DBAmiibo>>

    @Query("select count(*) from $AMIIBO_TABLE_NAME")
    suspend fun getCount(): Int

    @Query("select * from $AMIIBO_TABLE_NAME where tail=:tail")
    suspend fun getById(tail: String): DBAmiibo?
}
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

package com.oscarg798.amiibowiki.core.persistence.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate

data class DBAMiiboReleaseDate(
    @ColumnInfo(name = "au")
    val australia: String?,
    @ColumnInfo(name = "eu")
    val europe: String?,
    @ColumnInfo(name = "na")
    val northAmerica: String?,
    @ColumnInfo(name = "jp")
    val japan: String?
) {

    constructor(releaseDate: AmiiboReleaseDate) : this(
        releaseDate.australia,
        releaseDate.europe,
        releaseDate.northAmerica,
        releaseDate.japan
    )

    fun map() = AmiiboReleaseDate(australia, europe, northAmerica, japan)
}

@Entity(tableName = AMIIBO_TABLE_NAME)
data class DBAmiibo(
    @ColumnInfo(name = AMIIBO_SERIES_COLUMN_NAME)
    val amiiboSeries: String,
    @ColumnInfo(name = CHARACTER_COLUMN_NAME)
    val character: String,
    @ColumnInfo(name = AMIIBO_GAME_SERIES_COLUMN_NAME)
    val gameSeries: String,
    @ColumnInfo(name = "head")
    val head: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "type")
    val type: String,
    @PrimaryKey
    val tail: String,
    @ColumnInfo(name = AMIIBO_NAME_COLUMN_NAME)
    val name: String,
    @Embedded
    val releaseDate: DBAMiiboReleaseDate?

) {

    constructor(amiibo: Amiibo) : this(
        amiibo.amiiboSeries,
        amiibo.character,
        amiibo.gameSeries,
        amiibo.head,
        amiibo.image,
        amiibo.type,
        amiibo.tail,
        amiibo.name,
        if (amiibo.releaseDate != null) {
            DBAMiiboReleaseDate(amiibo.releaseDate)
        } else {
            null
        }
    )

    fun toAmiibo() = Amiibo(
        amiiboSeries,
        character,
        gameSeries,
        head,
        image,
        type,
        releaseDate?.map(),
        tail,
        name
    )
}

const val AMIIBO_GAME_SERIES_COLUMN_NAME = "gameSeries"
const val AMIIBO_NAME_COLUMN_NAME = "name"
const val CHARACTER_COLUMN_NAME = "character"
const val AMIIBO_SERIES_COLUMN_NAME = "amiiboSeries"
const val AMIIBO_TABLE_NAME = "amiibo"

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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oscarg798.amiibowiki.core.models.AgeRating
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Rating

@Entity(tableName = DB_AGE_RATING_TABLE_NAME)
data class DBAgeRating(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "category")
    val category: Int,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = GAME_ID_COLUMN_NAME)
    val gameId: Int
) {

    constructor(ageRating: AgeRating, gameId: Int) : this(
        id = null,
        category = when (ageRating.category) {
            is AgeRatingCategory.ESRB -> 1
            else -> 2
        },
        rating = ageRating.rating.id, gameId = gameId
    )

    fun toAgeRating() = AgeRating(AgeRatingCategory.getCategory(category), Rating.getRating(rating))
}

const val GAME_ID_COLUMN_NAME = "gameId"
const val DB_AGE_RATING_TABLE_NAME = "age_rating"

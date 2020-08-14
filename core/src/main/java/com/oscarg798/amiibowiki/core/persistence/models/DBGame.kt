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
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory

@Entity(tableName = DB_GAME_TABLE_NAME)
data class DBGame(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = DB_GAME_COLUMN_NAME_NAME)
    val name: String,
    @ColumnInfo(name = "category")
    val category: Int,
    @ColumnInfo(name = "cover")
    val cover: String?,
    @ColumnInfo(name = "gameSeries")
    val gameSeries: String,
    @ColumnInfo(name = "summary")
    val summary: String?,
    @ColumnInfo(name = "rating")
    val rating: Double?,
    @ColumnInfo(name = "webSites")
    val webSites: Collection<String>?,
    @ColumnInfo(name = "videosId")
    val videosId: Collection<String>?,
    @ColumnInfo(name = "artworks")
    val artworks: Collection<String>?,
    @ColumnInfo(name = "screenshots")
    val screenshots: Collection<String>?
) {

    constructor(game: Game) : this(
        game.id,
        game.name,
        game.category.categoryId,
        game.cover,
        game.gameSeries,
        game.summary,
        game.rating,
        game.webSites,
        game.videosId,
        game.artworks,
        game.screenshots
    )

    fun toGame(dbAgeRatings: List<DBAgeRating>) =
        Game(
            id,
            name,
            GameCategory.createFromCategoryId(category),
            cover,
            gameSeries,
            summary,
            rating,
            webSites,
            videosId,
            artworks,
            dbAgeRatings.map {
                it.toAgeRating()
            },
            screenshots
        )
}

const val DB_GAME_TABLE_NAME = "game"
const val DB_GAME_COLUMN_NAME_NAME = "name"

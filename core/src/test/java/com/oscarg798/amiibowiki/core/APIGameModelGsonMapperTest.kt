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

package com.oscarg798.amiibowiki.core

import com.google.gson.Gson
import com.oscarg798.amiibowiki.core.network.models.APIGame
import org.junit.Assert
import org.junit.Test

class APIGameModelGsonMapperTest {

    @Test
    fun `given a json with a game information when its parse using gson then it should return an api model`() {

        val gameJson =
            """
            {
                "id": 41829,
                "category": 2,
                "collection": 106,
                "cover": 98625,
                "created_at": 1498953600,
                "first_release_date": 1488499200,
                "franchises": [
                596
                ],
                "game_modes": [
                1
                ],
                "genres": [
                12,
                31
                ],
                "involved_companies": [
                51589
                ],
                "keywords": [
                541,
                3823
                ],
                "name": "The Legend of Zelda: Breath of the Wild - Expansion Pass",
                "parent_game": 7346,
                "platforms": [
                41,
                130
                ],
                "player_perspectives": [
                2
                ],
                "popularity": 1.0,
                "pulse_count": 1,
                "rating": 89.3828696532286,
                "rating_count": 5,
                "release_dates": [
                88320,
                88321
                ],
                "similar_games": [
                55199,
                81249,
                96217,
                101608,
                103303,
                105049,
                106987,
                116530,
                119171
                ],
                "slug": "the-legend-of-zelda-breath-of-the-wild-expansion-pass",
                "summary": "The expansion pass is the only way to purchase the DLC packs for the game.  \n  \nThe pass became available for purchase as of March 3rd 2017 but the 2 DLC packs included are to be released during the summer and winter of 2017.",
                "tags": [
                1,
                17,
                268435468,
                268435487,
                536871453,
                536874735
                ],
                "themes": [
                1,
                17
                ],
                "total_rating": 89.3828696532286,
                "total_rating_count": 5,
                "updated_at": 1587945600,
                "url": "https://www.igdb.com/games/the-legend-of-zelda-breath-of-the-wild-expansion-pass",
                "videos": [
                14759
                ],
                "websites": [
                50428
                ],
                "checksum": "93d388df-fcb5-4065-72f5-1b44ee96b2d6"
            }
            """.trimIndent()

        val gson = Gson()
        val game = gson.fromJson(gameJson, APIGame::class.java)
        Assert.assertEquals("The Legend of Zelda: Breath of the Wild - Expansion Pass", game.name)
        Assert.assertEquals(41829, game.id)
        Assert.assertEquals(2, game.category)
        Assert.assertEquals(98625, game.coverId)
        assert(89.3828696532286 == game.rating)
        Assert.assertNotNull(game.summary)
        Assert.assertEquals(setOf(50428), game.webSiteIds)
        Assert.assertEquals(setOf(14759), game.videosId)
    }
}

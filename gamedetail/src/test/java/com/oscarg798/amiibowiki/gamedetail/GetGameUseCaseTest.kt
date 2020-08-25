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

package com.oscarg798.amiibowiki.gamedetail

import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.gamedetail.usecases.GetGamesUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class GetGameUseCaseTest {

    private val repository = relaxedMockk<GameRepository>()

    private lateinit var usecase: GetGamesUseCase

    @Before
    fun setup() {
        usecase = GetGamesUseCase(repository)
    }

    @Test
    fun `when its executed and it does contain cover, screenshots and artworks then those should be formated`() {
        coEvery { repository.getGame(GAME_ID) } answers { GAME }

        val result = runBlocking {
            usecase.execute(GAME_ID)
        }

        result.id shouldBeEqualTo GAME_ID
        result.category shouldBeEqualTo GameCategory.MainGame
        result.summary shouldBeEqualTo "6"
        result.webSites shouldBeEqualTo setOf("8")
        result.videosId shouldBeEqualTo setOf("9")
        result.ageRating shouldBeEqualTo listOf()
        result.cover shouldBeEqualTo "https://images.igdb.com/igdb/image/upload/t_cover_big/a.jpg"
        result.artworks!!.first() shouldBeEqualTo "https://images.igdb.com/igdb/image/upload/t_screenshot_med/b.jpg"
        result.screenshots!!.first() shouldBeEqualTo "https://images.igdb.com/igdb/image/upload/t_screenshot_med/c.jpg"
    }
}

private const val GAME_ID = 43
private val GAME =
    Game(
        GAME_ID, "2",
        GameCategory.createFromCategoryId(0),
        "//images.igdb.com/igdb/image/upload/t_thumb/a.jpg",
        "6",
        7.toDouble(),
        webSites = setOf("8"),
        videosId = setOf("9"),
        artworks = setOf("//images.igdb.com/igdb/image/upload/t_thumb/b.jpg"),
        ageRating = listOf(),
        screenshots = setOf("//images.igdb.com/igdb/image/upload/t_thumb/c.jpg")
    )

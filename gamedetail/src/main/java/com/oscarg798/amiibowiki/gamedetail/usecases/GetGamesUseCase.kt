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

package com.oscarg798.amiibowiki.gamedetail.usecases

import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.gamedetail.models.COVER_SIZE
import com.oscarg798.amiibowiki.gamedetail.models.ORIGINAL_IMAGE_SIZE
import com.oscarg798.amiibowiki.gamedetail.models.SCREENSHOT_IMAGE_SIZE
import javax.inject.Inject

class GetGamesUseCase @Inject constructor(private val gameRepository: GameRepository) {

    suspend fun execute(gameSeries: String, gameId: Id): Game {
        var game = gameRepository.getGame(gameSeries, gameId)

        if (game.cover != null) {
            game = game.copy(cover = transformImageUrl(game.cover!!, COVER_SIZE))
        }

        if (game.screenshots != null) {
            game = game.copy(
                screenshots = game.screenshots!!.map { screenShot ->
                    transformImageUrl(screenShot)
                }
            )
        }

        if (game.artworks != null) {
            game = game.copy(
                artworks = game.artworks!!.map { artwork ->
                    transformImageUrl(artwork)
                }
            )
        }

        return game
    }

    private fun transformImageUrl(url: String, desiredSize: String = SCREENSHOT_IMAGE_SIZE) =
        url.replace(ORIGINAL_SCHEMA, DESIRED_SCHEMA).replace(ORIGINAL_IMAGE_SIZE, desiredSize)
}

private const val DESIRED_SCHEMA = "https://"
private const val ORIGINAL_SCHEMA = "//"

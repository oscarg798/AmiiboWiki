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

package com.oscarg798.amiibowiki.core.usecases

import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import javax.inject.Inject

class FillGameResultCoverUseCase @Inject constructor(private val gameRepository: GameRepository) {

    suspend fun execute(gameSearchResults: Collection<GameSearchResult>): Collection<GameSearchResult> {
        val gameIds = gameSearchResults.map { gameSearchResult ->
            gameSearchResult.gameId
        }

        val gameCoversMap = HashMap<Int, String>()
        gameRepository.getGameCover(gameIds).map {
            Pair(it.gameId, it.coverUrl)
        }.forEach {
            gameCoversMap[it.gameId] = it.coverUrl
        }

        return gameSearchResults.map {
            val cover = gameCoversMap.get(it.gameId)
            it.copy(
                coverUrl = if (cover == null) {
                    cover
                } else {
                    transformImageUrl(cover)
                }
            )
        }
    }

    private fun transformImageUrl(url: String) =
        url.replace(ORIGINAL_SCHEMA, DESIRED_SCHEMA).replace(ORIGINAL_IMAGE_SIZE, COVER_SIZE)

    private val Pair<Int, String>.gameId
        get() = this.first
    private val Pair<Int, String>.coverUrl
        get() = this.second
}

private const val COVER_SIZE = "t_cover_small"
private const val DESIRED_SCHEMA = "https://"
private const val ORIGINAL_IMAGE_SIZE = "t_thumb"
private const val ORIGINAL_SCHEMA = "//"

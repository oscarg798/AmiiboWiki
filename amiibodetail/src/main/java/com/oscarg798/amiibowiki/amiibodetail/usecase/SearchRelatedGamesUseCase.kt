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

package com.oscarg798.amiibowiki.amiibodetail.usecase

import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.extensions.getOrTransform
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SearchRelatedGamesUseCase @Inject constructor(private val gameRepository: GameRepository) {

    suspend fun execute(amiibo: Amiibo): Collection<GameSearchResult> = coroutineScope {
        runCatching {
            val searches = mutableListOf<Deferred<Collection<GameSearchResult>>>()
            searches.add(
                async(start = CoroutineStart.LAZY) {
                    gameRepository.searchGame(amiibo.name)
                }
            )

            searches.add(
                async(start = CoroutineStart.LAZY) {
                    gameRepository.searchGame(amiibo.gameSeries)
                }
            )

            searches.add(
                async(start = CoroutineStart.LAZY) {
                    gameRepository.searchGame(amiibo.character)
                }
            )

            val result = HashSet<GameSearchResult>()

            searches.awaitAll().forEach { gameResults ->
                result.addAll(gameResults)
            }

            result.sortedBy { it.name }
        }.getOrTransform {
            if (it is SearchGameFailure.DateSourceError) {
                throw AmiiboDetailFailure.GamesRelatedNotFound()
            }

            throw it
        }
    }
}

private const val AMIIBO_NAME_DELIMITER = "-"

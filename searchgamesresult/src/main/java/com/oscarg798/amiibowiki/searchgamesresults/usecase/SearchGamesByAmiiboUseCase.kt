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

package com.oscarg798.amiibowiki.searchgamesresults.usecase

import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.core.repositories.GameRepository
import com.oscarg798.amiibowiki.core.usecases.FillGameResultCoverUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@ViewModelScoped
class SearchGamesByAmiiboUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val amiiboRepository: AmiiboRepository,
    private val fillGameResultCoverUseCase: FillGameResultCoverUseCase,
    private val locale: Locale
) {

    suspend fun execute(
        amiiboId: String
    ): Flow<Collection<GameSearchResult>> = flow<Amiibo> {
        emit(amiiboRepository.getAmiiboById(amiiboId))
    }.flatMapMerge { amiibo ->
        combine(
            searchByParameter(amiibo.name),
            searchByParameter(amiibo.gameSeries),
            searchByParameter(amiibo.character)
        ) { nameResults: Collection<GameSearchResult>, gameSeriesResults: Collection<GameSearchResult>, characterResults: Collection<GameSearchResult> ->
            val result = HashSet<GameSearchResult>()
            result.addAll(nameResults)
            result.addAll(gameSeriesResults)
            result.addAll(characterResults)
            result
        }.map {
            it.filterNot { gameSearchResult ->
                gameSearchResult.name.toLowerCase(locale).contains(DUPLICATE)
            }
        }
    }.flatMapMerge { results ->
        flow<Collection<GameSearchResult>> {
            emit(results.sortByName())
            val resultsWithImages =
                fillGameResultCoverUseCase.execute(results).sortByName()
            emit(resultsWithImages)
        }
    }

    private fun searchByParameter(parameter: String) = flow<Collection<GameSearchResult>> {
        emit(gameRepository.searchGame(parameter))
    }

    private fun Collection<GameSearchResult>.sortByName() = sortedBy {
        it.name
    }
}

private const val DUPLICATE = "duplicate"
private const val AMIIBO_NAME_DELIMITER = "-"

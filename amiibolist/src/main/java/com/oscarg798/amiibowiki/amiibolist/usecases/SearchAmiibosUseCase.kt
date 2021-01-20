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

package com.oscarg798.amiibowiki.amiibolist.usecases

import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboSearchQuery
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SearchAmiibosUseCase @Inject constructor(private val amiiboRepository: AmiiboRepository) {

    fun execute(query: String): Flow<Collection<Amiibo>> = combine(
        amiiboRepository.searchAmiibos(AmiiboSearchQuery.Character(query)),
        amiiboRepository.searchAmiibos(AmiiboSearchQuery.AmiiboName(query)),
        amiiboRepository.searchAmiibos(AmiiboSearchQuery.GameSeries(query)),
        amiiboRepository.searchAmiibos(AmiiboSearchQuery.AmiiboSeries(query)),
        transform = { nameResult: Collection<Amiibo>, characterResult: Collection<Amiibo>, gameSeriesResult: Collection<Amiibo>, amiiboSeriesResult: Collection<Amiibo> ->
            val results = LinkedHashSet<Amiibo>()
            results.addAll(nameResult)
            results.addAll(characterResult)
            results.addAll(gameSeriesResult)
            results.addAll(amiiboSeriesResult)
            results
        }
    )
}

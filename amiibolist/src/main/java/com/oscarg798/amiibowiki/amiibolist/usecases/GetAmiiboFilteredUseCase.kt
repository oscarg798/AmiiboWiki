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

import com.oscarg798.amiibowiki.amiibolist.exceptions.AmiiboListFailure
import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.core.usecases.GetDefaultAmiiboTypeUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetAmiiboFilteredUseCase @Inject constructor(
    private val getDefaultAmiiboTypeUseCase: GetDefaultAmiiboTypeUseCase,
    private val amiiboRepository: AmiiboRepository
) {

    suspend fun execute(filter: AmiiboType): Collection<Amiibo> = when (filter) {
        getDefaultAmiiboTypeUseCase.execute() -> amiiboRepository.getAmiibosWithoutFilters().first()
        else -> filterAmiibos(filter.name)
    }

    private suspend fun filterAmiibos(filter: String): Collection<Amiibo> {
        return runCatching {
            amiiboRepository.getAmiibosFilteredByTypeName(filter)
        }.getOrElse { cause ->
            if (cause !is FilterAmiiboFailure) {
                throw cause
            }

            throw AmiiboListFailure.FilterError(
                cause.message ?: "Error filtering the amiibos is the data source"
            )
        }
    }
}

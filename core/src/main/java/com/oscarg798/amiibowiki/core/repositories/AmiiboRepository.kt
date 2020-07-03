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

package com.oscarg798.amiibowiki.core.repositories

import com.oscarg798.amiibowiki.core.base.getOrTransformNetworkException
import com.oscarg798.amiibowiki.core.base.runCatchingNetworkException
import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.failures.REMOTE_DATA_SOURCE_TYPE
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.models.APIAmiibo
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AmiiboRepository @Inject constructor(
    private val amiiboService: AmiiboService,
    private val amiiboDAO: AmiiboDAO
) {
    fun getAmiibos(): Flow<List<Amiibo>> = amiiboDAO.getAmiibos().map {
        it.map { dbAmiibo ->
            dbAmiibo.map()
        }
    }

    suspend fun updateAmiibos(): Flow<List<Amiibo>> =
        flow<List<Amiibo>> {
            val result = runCatching {
                amiiboService.get().amiibo.map { apiAmiibo ->
                    apiAmiibo.map()
                }
            }.getOrTransformNetworkException {
                GetAmiibosFailure.ProblemInDataSource(REMOTE_DATA_SOURCE_TYPE, it)
            }

            emit(result)
        }.onEach {
            if (it.isEmpty()) {
                return@onEach
            }

            amiiboDAO.insert(it.map { amiibo ->
                amiibo.map()
            })
        }

    suspend fun getAmiibosFilteredByTypeName(type: String): List<Amiibo> =
        runCatching {
            amiiboService.getAmiiboFilteredByType(type).amiibo.map { it.map() }
        }.getOrTransformNetworkException {
            FilterAmiiboFailure.ErrorFilteringAmiibos(it)
        }
}

fun AmiiboReleaseDate.map() = DBAMiiboReleaseDate(australia, europe, northAmerica, japan)
fun Amiibo.map() =
    DBAmiibo(amiiboSeries, character, gameSeries, head, image, type, tail, name, releaseDate.map())

fun APIAmiiboReleaseDate.map() = AmiiboReleaseDate(australia, europe, northAmerica, japan)
fun APIAmiibo.map() =
    Amiibo(amiiboSeries, character, gameSeries, head, image, type, releaseDate.map(), tail, name)
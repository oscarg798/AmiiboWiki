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

import android.util.Log
import com.oscarg798.amiibowiki.core.di.CoreScope
import com.oscarg798.amiibowiki.core.extensions.getOrTransformNetworkException
import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.failures.REMOTE_DATA_SOURCE_TYPE
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
@CoreScope
class AmiiboRepositoryImpl @Inject constructor(
    private val amiiboService: AmiiboService,
    private val amiiboDAO: AmiiboDAO
) : AmiiboRepository {

    override fun getAmiibos(): Flow<List<Amiibo>> = amiiboDAO.getAmiibos()
        .catch { cause->
            Log.i("PENE", cause.stackTrace.toString())
            Log.i("PENE","PENE")
        }
        .map {
        it.map { dbAmiibo ->
            dbAmiibo.map()
        }
    }

    override suspend fun getAmiiboById(tail: String): Amiibo {
        val amiibo = amiiboDAO.getById(tail)?.map()

        return amiibo
            ?: throw IllegalArgumentException("tail $tail does not belong to any saved amiibo")
    }

    override suspend fun updateAmiibos(): Flow<List<Amiibo>> =
        flow<List<Amiibo>> {
            val result = runCatching {
                amiiboService.get().amiibo.map { apiAmiibo ->
                    apiAmiibo.toAmiibo()
                }
            }.getOrTransformNetworkException {
                GetAmiibosFailure.ProblemInDataSource(REMOTE_DATA_SOURCE_TYPE, it)
            }

            emit(result)
        }.onEach {
            if (it.isEmpty()) {
                return@onEach
            }

            amiiboDAO.insert(
                it.map { amiibo ->
                    amiibo.toDBAmiibo()
                }
            )
        }

    override suspend fun getAmiibosFilteredByTypeName(type: String): List<Amiibo> =
        runCatching {
            amiiboService.getAmiiboFilteredByType(type).amiibo.map { it.toAmiibo() }
        }.getOrTransformNetworkException {
            FilterAmiiboFailure.ErrorFilteringAmiibos(it)
        }
}

fun AmiiboReleaseDate.toDBAmiiboReleaseDate() =
    DBAMiiboReleaseDate(australia, europe, northAmerica, japan)

fun Amiibo.toDBAmiibo() =
    DBAmiibo(
        amiiboSeries,
        character,
        gameSeries,
        head,
        image,
        type,
        tail,
        name,
        releaseDate.toDBAmiiboReleaseDate()
    )

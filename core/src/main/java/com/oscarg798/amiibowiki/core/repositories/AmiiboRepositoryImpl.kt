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

import com.oscarg798.amiibowiki.core.extensions.getOrTransformNetworkException
import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.failures.REMOTE_DATA_SOURCE_TYPE
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboSearchQuery
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class AmiiboRepositoryImpl @Inject constructor(
    private val amiiboService: AmiiboService,
    private val amiiboDAO: AmiiboDAO
) : AmiiboRepository {

    override suspend fun getAmiibos(): Flow<List<Amiibo>> = flow {
        emit(getLocalAmiibos().first())
        getCloudAmiibos()
        emitAll(getLocalAmiibos())
    }

    override fun searchAmiibos(query: AmiiboSearchQuery): Flow<Collection<Amiibo>> {
        val searchQuery = "%${query.query}%"
        return when (query) {
            is AmiiboSearchQuery.AmiiboName -> amiiboDAO.searchByAmiiboName(searchQuery)
            is AmiiboSearchQuery.Character -> amiiboDAO.searchByCharacter(searchQuery)
            is AmiiboSearchQuery.GameSeries -> amiiboDAO.searchByGameSeries(searchQuery)
            is AmiiboSearchQuery.AmiiboSeries -> amiiboDAO.searchByAmiiboSeries(searchQuery)
        }.map {
            it.map { dbAmiibo ->
                dbAmiibo.toAmiibo()
            }
        }
    }

    override suspend fun getAmiibosWithoutFilters(): Flow<List<Amiibo>> = getLocalAmiibos()

    override suspend fun getAmiiboById(tail: String): Amiibo {
        val amiibo = amiiboDAO.getById(tail)?.toAmiibo()

        return amiibo
            ?: throw IllegalArgumentException("tail $tail does not belong to any saved amiibo")
    }

    override suspend fun getAmiibosFilteredByTypeName(type: String): List<Amiibo> =
        runCatching {
            amiiboService.getAmiiboFilteredByType(type).amiibo.map { it.toAmiibo() }
        }.getOrTransformNetworkException {
            FilterAmiiboFailure.ErrorFilteringAmiibos(it)
        }

    private fun getLocalAmiibos(): Flow<List<Amiibo>> {
        return amiiboDAO.getAmiibos()
            .map {
                it.map { dbAmiibo ->
                    dbAmiibo.toAmiibo()
                }
            }
    }

    private suspend fun getCloudAmiibos() {
        val result = runCatching {
            amiiboService.get().amiibo.map { apiAmiibo ->
                apiAmiibo.toAmiibo()
            }
        }.getOrTransformNetworkException {
            throw GetAmiibosFailure.ProblemInDataSource(REMOTE_DATA_SOURCE_TYPE, it)
        }

        if (result.isEmpty()) {
            return
        }

        amiiboDAO.insert(
            result.map { amiibo ->
                amiibo.toDBAmiibo()
            }
        )
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
        releaseDate?.toDBAmiiboReleaseDate()
    )

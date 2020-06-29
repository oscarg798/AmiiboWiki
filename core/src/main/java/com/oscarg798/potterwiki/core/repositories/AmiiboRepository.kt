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

package com.oscarg798.potterwiki.core.repositories

import com.oscarg798.potterwiki.core.base.runCatchingNetworkException
import com.oscarg798.potterwiki.core.failures.FilterAmiiboFailure
import com.oscarg798.potterwiki.core.models.Amiibo
import com.oscarg798.potterwiki.core.models.AmiiboReleaseDate
import com.oscarg798.potterwiki.core.network.APIAmiibo
import com.oscarg798.potterwiki.core.network.APIAmiiboReleaseDate
import com.oscarg798.potterwiki.core.network.AmiiboService
import com.oscarg798.potterwiki.network.exceptions.NetworkException
import javax.inject.Inject

class AmiiboRepository @Inject constructor(private val amiiboService: AmiiboService) {

    suspend fun getAmiibos(): Result<List<Amiibo>> =
        runCatchingNetworkException { amiiboService.get().amiibo.map { it.map() } }

    suspend fun getAmiibosFilteredByTypeName(type: String): Result<List<Amiibo>> =
        runCatchingNetworkException(exceptionHandler = {
            Result.failure<List<Amiibo>>(
                when (it) {
                    is NetworkException.BadRequest -> FilterAmiiboFailure.FilterDoesNotExists(it)
                    else -> FilterAmiiboFailure.Unknown(it)
                }
            )
        }) { amiiboService.getAmiiboFilteredByType(type).amiibo.map { it.map() } }
}

fun APIAmiiboReleaseDate.map() = AmiiboReleaseDate(australia, europe, northAmerica, japan)
fun APIAmiibo.map() =
    Amiibo(amiiboSeries, character, gameSeries, head, image, type, releaseDate.map(), tail, name)
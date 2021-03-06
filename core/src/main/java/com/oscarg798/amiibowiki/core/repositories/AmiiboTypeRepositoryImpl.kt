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
import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiiboType
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class AmiiboTypeRepositoryImpl @Inject constructor(
    private val amiiboTypeService: AmiiboTypeService,
    private val amiiboTypeDAO: AmiiboTypeDAO
) : AmiiboTypeRepository {

    override suspend fun getTypes(): Collection<AmiiboType> {
        return amiiboTypeDAO.getTypes().map {
            it.toAmiiboType()
        }
    }

    override suspend fun updateTypes(): Collection<AmiiboType> {
        return runCatching {
            amiiboTypeService.getTypes().amiibo.map {
                val type = it.toAmiibo()
                amiiboTypeDAO.insertType(DBAmiiboType(type))
                type
            }
        }.getOrTransformNetworkException {
            throw AmiiboTypeFailure.FetchTypesFailure(cause = it)
        }
    }

    override suspend fun hasTypes(): Boolean {
        return amiiboTypeDAO.count() > NO_ELEMENTS_SIZE
    }
}

private const val NO_ELEMENTS_SIZE = 0

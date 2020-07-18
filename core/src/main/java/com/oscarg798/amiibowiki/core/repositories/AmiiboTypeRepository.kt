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


import com.oscarg798.amiibowiki.core.di.CoreScope
import com.oscarg798.amiibowiki.core.extensions.runCatchingNetworkException
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboType
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiiboType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@CoreScope
class AmiiboTypeRepository @Inject constructor(
    private val amiiboTypeService: AmiiboTypeService,
    private val amiiboTypeDAO: AmiiboTypeDAO
) {

    fun getTypes(): Flow<List<AmiiboType>> {
        return amiiboTypeDAO.getTypes().map {
            it.map { type -> type.map() }
        }
    }

    suspend fun updateTypes(): Result<List<AmiiboType>> {
        return runCatchingNetworkException {
            amiiboTypeService.getTypes().amiibo.map {
                val type = it.toDBAmiibo()
                amiiboTypeDAO.insertType(type.toDBAmiibo())
                type
            }
        }
    }

    suspend fun hasTypes(): Boolean {
        return amiiboTypeDAO.count() > 0
    }
}

private fun AmiiboType.toDBAmiibo() =
    DBAmiiboType(key, name)

private fun APIAmiiboType.toDBAmiibo() = AmiiboType(key, name)
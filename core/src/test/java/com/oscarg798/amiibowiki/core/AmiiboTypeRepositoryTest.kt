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

package com.oscarg798.amiibowiki.core

import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboType
import com.oscarg798.amiibowiki.core.network.models.GetAmiiboTypeResponse
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class AmiiboTypeRepositoryTest {

    private val amiiboTypeService = mockk<AmiiboTypeService>()
    private lateinit var repository: AmiiboTypeRepository

//    @Before
//    fun setup() {
//        coEvery { amiiboTypeService.getTypes() } answers {
//            GetAmiiboTypeResponse(
//                MOCK_TYPES
//            )
//        }
//        repository = AmiiboTypeRepository(
//                amiiboTypeService
//            )
//    }
//
//    @Test
//    fun `when is invoke then it should return types as result`() {
//        val result = runBlocking {
//            repository.getTypes()
//        }
//
//        true shouldBeEqualTo result.isSuccess
//        listOf(AmiiboType("1", "2")) shouldBeEqualTo result.getOrNull()
//    }


}

private val MOCK_TYPES = listOf(
    APIAmiiboType(
        "1",
        "2"
    )
)
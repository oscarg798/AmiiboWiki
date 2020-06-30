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

package com.oscarg798.amiibowiki.amiibolist

import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.houses.repository.AmiiboTypeRepository
import com.oscarg798.amiibowiki.houses.usecases.GetAmiiboTypeUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class GetAmiiboTypeUseCaseTest {

    private val getDefaultAmiiboTypeUseCase = mockk<GetDefaultAmiiboTypeUseCase>()
    private val amiiboTypeRepository = mockk<AmiiboTypeRepository>()
    private lateinit var usecase: GetAmiiboTypeUseCase

    @Before
    fun setup() {
        coEvery { amiiboTypeRepository.getTypes() } answers { Result.success(AMIIBO_TYPES) }
        every { getDefaultAmiiboTypeUseCase.execute() }.answers { AmiiboType("3", "3") }
        usecase = GetAmiiboTypeUseCase(getDefaultAmiiboTypeUseCase, amiiboTypeRepository)
    }

    @Test
    fun `when is executed then it should return types with default value`() {
        val result = runBlocking {
            usecase.getAmiiboType()
        }

        true shouldBeEqualTo result.isSuccess
        listOf(
            AmiiboType("1", "2"),
            AmiiboType("3", "3")
        ) shouldBeEqualTo result.getOrNull()
    }
}

private val AMIIBO_TYPES = listOf(
    AmiiboType("1", "2")
)
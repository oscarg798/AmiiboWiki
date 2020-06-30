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

import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.houses.usecases.GetAmiiboFilteredUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class GetAmiiboFilteredUseCaseTest {

    private val getDefaultAmiiboTypeUseCase = mockk<GetDefaultAmiiboTypeUseCase>()
    private val repository = mockk<AmiiboRepository>()

    private lateinit var usecase: GetAmiiboFilteredUseCase

    @Before
    fun setup() {
        every { getDefaultAmiiboTypeUseCase.execute() }.answers { AmiiboType("3", "3") }
        coEvery { repository.getAmiibosFilteredByTypeName(any()) } answers {
            Result.success(
                FILTERED_AMIIBOS
            )
        }

        usecase = GetAmiiboFilteredUseCase(
            getDefaultAmiiboTypeUseCase,
            repository
        )
    }

    @Test
    fun `given non default filter when is executed then it should return filtered amiibos`() {
        FILTERED_AMIIBOS shouldBeEqualTo runBlocking {
            usecase.execute(AmiiboType("1", "2"))
        }.getOrNull()
    }

    @Test
    fun `given default filter when is executed then it should return all the amiibos`() {
        coEvery { repository.getAmiibos() } answers { Result.success(NO_FILTERED_AMIIBOS) }

        NO_FILTERED_AMIIBOS shouldBeEqualTo runBlocking {
            usecase.execute(AmiiboType("3", "3"))
        }.getOrNull()

    }
}

private val FILTERED_AMIIBOS = listOf(
    Amiibo(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        AmiiboReleaseDate("7", "8", "9", "10"),
        "11", "12"
    )
)

private val NO_FILTERED_AMIIBOS = listOf(
    Amiibo(
        "12",
        "22",
        "32",
        "42",
        "52",
        "62",
        AmiiboReleaseDate("72", "82", "92", "102"),
        "112", "122"
    )
)

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

import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListFailure
import com.oscarg798.amiibowiki.amiibolist.usecases.GetAmiibosUseCase
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class GetAmiibosUseCaseTest {

    private val repository = mockk<AmiiboRepository>()
    private lateinit var usecase: GetAmiibosUseCase

    @Before
    fun setup() {
        coEvery { repository.getAmiibos() } answers { flowOf(AMIIBO_RESULT) }
        usecase = GetAmiibosUseCase(repository)
    }

    @Test
    fun `when its executed then it should return amiibos`() {
        val response = runBlocking { usecase.execute().toList() }

        response.size shouldBeEqualTo 1
        listOf(
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
        ) shouldBeEqualTo response[0]
    }

    @Test(expected = AmiiboListFailure::class)
    fun `when there is failure in the data source then it should throw an Amiibo List Failure`() {
        every { repository.getAmiibos() } answers {
            flow {
                throw GetAmiibosFailure.ProblemInDataSource(
                    "",
                    null
                )
            }
        }

        runBlocking { usecase.execute().toList() }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when there is an exception getting amiibos then it should throw the exception`() {
        every { repository.getAmiibos() } answers {
            throw IllegalArgumentException()
        }

        runBlocking { usecase.execute().toList() }
    }
}

private val AMIIBO_RESULT =
    listOf(
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

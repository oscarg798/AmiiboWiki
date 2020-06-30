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

import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.models.APIAmiibo
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.models.GetAmiiboResponse
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException


class AmiiboRepositoryTest {

    private val amiiboService = mockk<AmiiboService>()
    private lateinit var repository: AmiiboRepository

    @Before
    fun before() {
        coEvery { amiiboService.get() } answers {
            GetAmiiboResponse(
                listOf(API_AMIIBO)
            )
        }
        coEvery { amiiboService.getAmiiboFilteredByType(any()) } answers {
            GetAmiiboResponse(
                listOf(
                    API_AMIIBO
                )
            )
        }
        repository = AmiiboRepository(amiiboService)
    }

    @Test
    fun `when get amiibos is called then it should return amiibos`() {
        val response = runBlocking { repository.getAmiibos() }

        assertEquals(
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
            ), response.getOrNull()
        )
    }

    @Test
    fun `when there is an network exception getting amiibos then it should be returned as failure`() {
        coEvery { amiiboService.get() } throws NetworkException.APIKeyNotFound("not found")
        val response = runBlocking { repository.getAmiibos() }

        assert(response.isFailure)
        assert(response.exceptionOrNull() is NetworkException.APIKeyNotFound)
    }

    @Test(expected = IOException::class)
    fun `when there is an exception getting amiibos then it should throw`() {
        coEvery { amiiboService.get() } throws IOException()
        runBlocking { repository.getAmiibos() }
    }

    @Test
    fun `given a filter when repo is invoke to filter amiibos then it should return them`() {
        val response = runBlocking { repository.getAmiibosFilteredByTypeName("yarn") }

        assertEquals(
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
            ), response.getOrNull()
        )
    }

    @Test
    fun `given a filter when repo is invoke and there is a badexception filtering amiibos then FilterDoesNotExists should be return`() {
        coEvery { amiiboService.getAmiiboFilteredByType(any()) }.throws(
            NetworkException.BadRequest(
                "something"
            )
        )
        val response = runBlocking { repository.getAmiibosFilteredByTypeName("yarn") }

        true shouldBeEqualTo response.isFailure
        true shouldBeEqualTo (response.exceptionOrNull() is FilterAmiiboFailure.FilterDoesNotExists)
    }

    @Test
    fun `given a filter when repo is invoke and there is a NetworkException filtering amiibos then FilterAmiiboFailure should be return`() {
        coEvery { amiiboService.getAmiiboFilteredByType(any()) }.throws(
            NetworkException.Forbidden("something")
        )
        val response = runBlocking { repository.getAmiibosFilteredByTypeName("yarn") }

        true shouldBeEqualTo response.isFailure
        true shouldBeEqualTo (response.exceptionOrNull() is FilterAmiiboFailure.Unknown)
    }
}

private val API_AMIIBO =
    APIAmiibo(
        "1", "2", "3", "4", "5", "6",
        APIAmiiboReleaseDate(
            "7",
            "8",
            "9",
            "10"
        ), "11", "12"
    )
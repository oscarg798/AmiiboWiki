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
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.models.APIAmiibo
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.models.GetAmiiboResponse
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException


@ExperimentalCoroutinesApi
class AmiiboRepositoryTest {

    private val amiiboDAO = mockk<AmiiboDAO>()
    private val amiiboService = mockk<AmiiboService>()
    private lateinit var repository: AmiiboRepository

    @Before
    fun before() {
        every { amiiboDAO.getAmiibos() } answers { flowOf(listOf(DB_AMIIBO)) }
        every { amiiboDAO.insert(any()) } answers { Unit }
        coEvery { amiiboDAO.getById("1") } answers { DB_AMIIBO }
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
        repository = AmiiboRepository(amiiboService, amiiboDAO)
    }

    @Test
    fun `when get amiibos is called then it should return local amiibos`() {
        val response = runBlocking { repository.getAmiibos().toList() }

        1 shouldBeEqualTo response.size
        listOf(
            Amiibo(
                "11", "12", "13", "14", "15", "16",
                AmiiboReleaseDate("19", "20", "21", "22"), "17", "18"
            )
        ) shouldBeEqualTo response[0]
        verify {
            amiiboDAO.getAmiibos()
        }
    }

    @Test
    fun `when update amiibos is called then it should return amiibos from the web`() {
        val response = runBlocking { repository.updateAmiibos().toList() }

        1 shouldBeEqualTo response.size

        listOf(AMIIBO) shouldBeEqualTo response[0]

        coVerify {
            amiiboDAO.insert(
                listOf(
                    DBAmiibo(
                        "1", "2", "3", "4", "5", "6", "11", "12",
                        DBAMiiboReleaseDate("7", "8", "9", "10")
                    )
                )
            )
            amiiboService.get()
        }
    }


    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_TimeOut updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.TimeOut
        runBlocking { repository.updateAmiibos().toList() }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_UnknowHost updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.UnknowHost("")
        runBlocking { repository.updateAmiibos().toList() }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_Connection updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.Connection
        runBlocking { repository.updateAmiibos().toList() }
    }

    @Test(expected = NetworkException.BadRequest::class)
    fun `when there is an exception updating amiibos then it should throw that exception`() {
        coEvery { amiiboService.get() } throws NetworkException.BadRequest("")
        runBlocking { repository.updateAmiibos().toList() }
    }

    @Test
    fun `given a name when filter amiibos is called then it shoudl return filtered amiibos`() {
        val result = runBlocking { repository.getAmiibosFilteredByTypeName("1") }
        listOf(AMIIBO) shouldBeEqualTo result
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_TimeOut filtering amiibos by name then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.TimeOut
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_UnknowHost filtering amiibos then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.UnknowHost("")
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_Connection filtering amiibos then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.Connection
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }
    }

    @Test(expected = NetworkException.BadRequest::class)
    fun `when there is an exception filtering amiibos then it should throw that exception`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.BadRequest("")
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }
    }

    @Test
    fun `given an amiibo tail when get by id is called then it should return amiibo found`() {
        val result = runBlocking { repository.getAmiiboById("1") }
        Amiibo(
            "11", "12", "13", "14",
            "15", "16", AmiiboReleaseDate("19", "20", "21", "22"), "17", "18"
        ) shouldBeEqualTo result
    }

}

private val AMIIBO = Amiibo(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    AmiiboReleaseDate("7", "8", "9", "10"),
    "11", "12"
)
private val DB_AMIIBO = DBAmiibo(
    "11", "12", "13", "14", "15"
    , "16", "17", "18", DBAMiiboReleaseDate("19", "20", "21", "22")
)
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
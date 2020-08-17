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

import com.oscarg798.amiibowiki.amiibolist.usecases.SearchAmiibosUseCase
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboSearchQuery
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepository
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class SearchAmiiboUseCaseTest {

    private val amiiboRepository = relaxedMockk<AmiiboRepository>()
    private lateinit var usecase: SearchAmiibosUseCase

    @Before
    fun setup() {
        every { amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.AmiiboName }) } answers {
            flowOf(
                listOf(AMIIBO.copy(name = "name"))
            )
        }

        every { amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.Character }) } answers {
            flowOf(
                listOf(AMIIBO.copy(name = "character"))
            )
        }

        every { amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.GameSeries }) } answers {
            flowOf(
                listOf(AMIIBO.copy(name = "game_series"))
            )
        }

        every { amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.AmiiboSeries }) } answers {
            flowOf(
                listOf(AMIIBO.copy(name = "amiibo_series"))
            )
        }

        usecase = SearchAmiibosUseCase(amiiboRepository)
    }

    @Test
    fun `when its executed then it should search by name, character, game series and amiibo series, and return result in this order`() {
        val result = runBlocking {
            usecase.execute(MOCK_QUERY).first()
        }

        result.toList() shouldBeEqualTo listOf(
            AMIIBO.copy(name = "character"),
            AMIIBO.copy(name = "name"),
            AMIIBO.copy(name = "game_series"),
            AMIIBO.copy(name = "amiibo_series")
        )

        verify {
            amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.AmiiboName })
            amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.Character })
            amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.GameSeries })
            amiiboRepository.searchAmiibos(match { query -> query is AmiiboSearchQuery.AmiiboSeries })
        }
    }
}

private val AMIIBO = Amiibo(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    AmiiboReleaseDate("7", "8", "10", "9"),
    "11", "12"
)
private const val MOCK_QUERY = "QUERY"

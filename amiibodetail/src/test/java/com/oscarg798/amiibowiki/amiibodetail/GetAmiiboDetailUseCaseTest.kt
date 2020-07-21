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

package com.oscarg798.amiibowiki.amiibodetail

import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.amiibodetail.usecase.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAmiiboDetailUseCaseTest {

    private val repository = mockk<AmiiboRepositoryImpl>()
    private lateinit var usecase: GetAmiiboDetailUseCase

    @Before
    fun setup() {
        coEvery { repository.getAmiiboById("1") } answers { AMIIBO }
        usecase = GetAmiiboDetailUseCase(repository)
    }

    @Test
    fun `given a tail id when its executed then it should return the amiibo`() {
        val result = runBlocking { usecase.execute("1") }
        AMIIBO shouldBeEqualTo result
    }

    @Test(expected = AmiiboDetailFailure.AmiiboNotFoundByTail::class)
    fun `when usecase is executed and repository throws an illegalargumentexception then it should throw an AmiiboDetailFailure`() {
        coEvery { repository.getAmiiboById("1") } throws IllegalArgumentException()
        runBlocking { usecase.execute("1") }
    }

    @Test(expected = Exception::class)
    fun `when is executed and repostory throws an exception then it should rethrow it`() {
        coEvery { repository.getAmiiboById("1") } throws Exception()
        runBlocking { usecase.execute("1") }
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

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

import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class UpdateAmiibosTypeUseCaseTest {

    private lateinit var usecase: UpdateAmiiboTypeUseCase
    private val amiiboTypeRepository = relaxedMockk<AmiiboTypeRepository>()

    @Before
    fun setup() {
        coEvery { amiiboTypeRepository.hasTypes() } answers { false }
        coEvery { amiiboTypeRepository.updateTypes() } answers { AMIIBO_TYPES }
        usecase = UpdateAmiiboTypeUseCase(amiiboTypeRepository)
    }

    @Test
    fun `when is invoken then it should return the types as result`() {
        runBlocking { usecase.execute() } shouldBeEqualTo Unit
    }

    @Test
    fun `when there is a failure and there are types then it should be success`() {
        coEvery { amiiboTypeRepository.updateTypes() } answers { throw AmiiboTypeFailure.FetchTypesFailure() }
        coEvery { amiiboTypeRepository.hasTypes() } answers { true }
        runBlocking { usecase.execute() } shouldBeEqualTo Unit
    }

    @Test(expected = AmiiboTypeFailure::class)
    fun `when there is a failure and there are not types then it should be crash`() {
        coEvery { amiiboTypeRepository.updateTypes() } answers { throw AmiiboTypeFailure.FetchTypesFailure() }

        runBlocking { usecase.execute() }
    }

    @Test(expected = NullPointerException::class)
    fun `when there is a NPE and there are  types then it should be crash`() {
        coEvery { amiiboTypeRepository.hasTypes() } answers { true }
        coEvery { amiiboTypeRepository.updateTypes() } answers { throw NullPointerException() }

        runBlocking { usecase.execute() }
    }

    @Test(expected = NullPointerException::class)
    fun `when there is a NPE and there are not types then it should be crash`() {
        coEvery { amiiboTypeRepository.updateTypes() } answers { throw NullPointerException() }

        runBlocking { usecase.execute() }
    }
}

private val AMIIBO_TYPES = listOf(
    AmiiboType("1", "2")
)

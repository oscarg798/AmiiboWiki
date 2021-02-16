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

package com.oscarg798.amiibowiki.nfcreader

import android.nfc.Tag
import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.usecases.AuthenticateApplicationUseCase
import com.oscarg798.amiibowiki.nfcreader.errors.InvalidTagDataException
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.errors.WrongPageFormatException
import com.oscarg798.amiibowiki.nfcreader.repository.NFCReaderRepository
import com.oscarg798.amiibowiki.nfcreader.usecase.ReadTagUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class ReadTagUseCaseTest {

    private val tag = mockk<Tag>()
    private val nfcReaderRepository = mockk<NFCReaderRepository>()
    private val authenticateApplicationUseCase: AuthenticateApplicationUseCase = relaxedMockk()
    private lateinit var usecase: ReadTagUseCase

    @Before
    fun setup() {
        every { nfcReaderRepository.getamiiboIdentifierFromTag(tag) } answers { AMIIBO_IDENTIFIER }
        usecase = ReadTagUseCase(authenticateApplicationUseCase, nfcReaderRepository)
    }

    @Test
    fun `given a valid tag when its executed then it should return the amiibo identifier`() {
        runBlocking {
            usecase.execute(tag)
        } shouldBeEqualTo AMIIBO_IDENTIFIER
    }

    @Test(expected = NFCReaderFailure.TagNotSupported::class)
    fun `when its executed and there is an InvalidTagDataException then it should throw TagNotSupported`() {
        every { nfcReaderRepository.getamiiboIdentifierFromTag(tag) } answers { throw InvalidTagDataException() }
        runBlocking {
            usecase.execute(tag)
        }
    }

    @Test(expected = NFCReaderFailure.TagNotSupported::class)
    fun `when its executed and there is an WrongPageFormatException then it should throw TagNotSupported`() {
        every { nfcReaderRepository.getamiiboIdentifierFromTag(tag) } answers { throw WrongPageFormatException() }
        runBlocking {
            usecase.execute(tag)
        }
    }

    @Test(expected = NFCReaderFailure.Unknow::class)
    fun `when its executed and there is an UnknownReadError then it should throw TagNotSupported`() {
        every { nfcReaderRepository.getamiiboIdentifierFromTag(tag) } answers {
            throw NFCReaderFailure.Unknow(
                Exception()
            )
        }
        runBlocking {
            usecase.execute(tag)
        }
    }

    @Test(expected = Exception::class)
    fun `when its executed and there is an Exception then it should throw Exception`() {
        every { nfcReaderRepository.getamiiboIdentifierFromTag(tag) } answers { throw Exception() }
        runBlocking {
            usecase.execute(tag)
        }
    }
}

private val AMIIBO_IDENTIFIER =
    AmiiboIdentifier("00000000", "00000001")

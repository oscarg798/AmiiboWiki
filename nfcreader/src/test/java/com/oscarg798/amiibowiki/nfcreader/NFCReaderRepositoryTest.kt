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

import android.nfc.NfcAdapter
import android.nfc.Tag
import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.nfcreader.errors.InvalidTagDataException
import com.oscarg798.amiibowiki.nfcreader.errors.UnknownReadError
import com.oscarg798.amiibowiki.nfcreader.repository.NFCReaderRepositoryImpl
import com.oscarg798.amiibowiki.nfcreader.utils.ArrayCloner
import com.oscarg798.amiibowiki.nfcreader.utils.ByteWrapper
import com.oscarg798.amiibowiki.nfcreader.utils.TagTech
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.every
import io.mockk.verify
import java.nio.ByteBuffer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class NFCReaderRepositoryTest {

    private val nfcAdapter =
        relaxedMockk<NfcAdapter>()
    private val tagTech =
        relaxedMockk<TagTech>()
    private val arrayCloner =
        relaxedMockk<ArrayCloner>()
    private val byteWrapper =
        relaxedMockk<ByteWrapper>()
    private val tag =
        relaxedMockk<Tag>()
    private val byteBuffer =
        relaxedMockk<ByteBuffer>()

    private lateinit var repository: NFCReaderRepositoryImpl

    @Before
    fun setup() {
        every { tagTech.readPages(any()) } answers { BYTE_ARRAY }
        every { byteWrapper.wrap(any()) } answers { byteBuffer }
        every { byteBuffer.long } answers { LONG }

        repository = NFCReaderRepositoryImpl(nfcAdapter, tagTech, arrayCloner, byteWrapper)
    }

    @Test
    fun `given a valid tag when it want to be readed then it should return the tag associated with it`() {
        val result = repository.getamiiboIdentifierFromTag(tag)
        result shouldBeEqualTo AMIIBO_IDENTIFIER

        verify {
            tagTech.initTech(any())
            tagTech.close()
            byteWrapper.wrap(any())
        }
    }

    @Test(expected = UnknownReadError::class)
    fun `given a tag without pages when it want to be readed then it should throw UnknownReadError`() {
        every { tagTech.readPages(any()) } answers { null }
        val result = repository.getamiiboIdentifierFromTag(tag)
        result shouldBeEqualTo AMIIBO_IDENTIFIER

        verify {
            tagTech.initTech(any())
            tagTech.close()
            byteWrapper.wrap(any())
        }
    }

    @Test(expected = UnknownReadError::class)
    fun `when there is an exception reading a tag  then it should throw UnknownReadError`() {
        every { tagTech.readPages(any()) } answers { throw Exception() }
        val result = repository.getamiiboIdentifierFromTag(tag)
        result shouldBeEqualTo AMIIBO_IDENTIFIER

        verify {
            tagTech.initTech(any())
            tagTech.close()
            byteWrapper.wrap(any())
        }
    }

    @Test(expected = UnknownReadError::class)
    fun `when there is an RuntimeException reading a tag  then it should throw the UnknownReadError`() {
        every { tagTech.readPages(any()) } answers { throw RuntimeException() }
        val result = repository.getamiiboIdentifierFromTag(tag)
        result shouldBeEqualTo AMIIBO_IDENTIFIER

        verify {
            tagTech.initTech(any())
            tagTech.close()
            byteWrapper.wrap(any())
        }
    }

    @Test(expected = UnknownReadError::class)
    fun `when there is an InvalidTagDataException reading a tag  then it should throw the UnknownReadError`() {
        every { tagTech.readPages(any()) } answers { throw InvalidTagDataException() }
        val result = repository.getamiiboIdentifierFromTag(tag)
        result shouldBeEqualTo AMIIBO_IDENTIFIER

        verify {
            tagTech.initTech(any())
            tagTech.close()
            byteWrapper.wrap(any())
        }
    }
}

private const val LONG = 1L
private val BYTE_ARRAY = byteArrayOf(
    4, 27, -103, 14, -46,
    106, 95, -127, 102, 72, 15, 32, -15, 16, -1, -18
)
private val AMIIBO_IDENTIFIER =
    AmiiboIdentifier("00000000", "00000001")

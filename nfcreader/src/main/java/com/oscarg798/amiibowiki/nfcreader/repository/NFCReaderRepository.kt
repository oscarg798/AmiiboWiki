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

package com.oscarg798.amiibowiki.nfcreader.repository

import android.nfc.NfcAdapter
import android.nfc.Tag
import com.oscarg798.amiibowiki.core.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.extensions.getOrTransform
import com.oscarg798.amiibowiki.nfcreader.errors.InvalidTagDataException
import com.oscarg798.amiibowiki.nfcreader.errors.UnknowReadError
import com.oscarg798.amiibowiki.nfcreader.errors.WrongPageFormatException
import com.oscarg798.amiibowiki.nfcreader.utils.ArrayCloner
import com.oscarg798.amiibowiki.nfcreader.utils.ByteWrapper
import com.oscarg798.amiibowiki.nfcreader.utils.TagTech
import java.io.IOException
import javax.inject.Inject
import kotlin.experimental.and

class NFCReaderRepository @Inject constructor(
    private val nfcAdapter: NfcAdapter,
    private val tagTech: TagTech,
    private val arrayCloner: ArrayCloner,
    private val byteWrapper: ByteWrapper
) {

    fun getamiiboIdentifierFromTag(tag: Tag): AmiiboIdentifier {
        return runCatching {
            tagTech.initTech(tag)
            val amiiboData = readFromTech(tagTech)
            val id = getIdFromData(amiiboData)
            closeTagTech()
            AmiiboIdentifier(getHead(id), getTail(id))
        }.getOrTransform {
            closeTagTech()

            if (it !is IOException || it is InvalidTagDataException || it is WrongPageFormatException) {
                throw it
            }

            throw UnknowReadError(it)
        }
    }

    private fun closeTagTech() = tagTech.close()

    private fun readFromTech(tagTech: TagTech): ByteArray {
        val tagData = ByteArray(TAG_FILE_SIZE)
        val pageCount: Int = TAG_FILE_SIZE / PAGE_SIZE
        var i = INIT_POSITION

        while (i < pageCount) {
            val pages = tagTech.readPages(i)

            if (isPageAvailable(pages)) {
                throw WrongPageFormatException()
            }

            require(pages != null)

            val destinationIndex: Int = i * PAGE_SIZE
            val destinationLength =
                (BULK_READ_PAGE_COUNT * PAGE_SIZE).coerceAtMost(tagData.size - destinationIndex)

            arrayCloner.copy(pages, INIT_POSITION, tagData, destinationIndex, destinationLength)
            i += BULK_READ_PAGE_COUNT
        }

        return tagData
    }

    private fun bytesToHex(bytes: ByteArray): String? {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] =
                hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun getIdFromData(data: ByteArray): Long {
        if (data.size < TAG_FILE_SIZE) {
            throw InvalidTagDataException()
        }

        val amiiboId = ByteArray(AMIIBO_ID_SIZE)

        arrayCloner.copy(
            data,
            AMIIBO_ID_OFFSET,
            amiiboId,
            INIT_POSITION,
            amiiboId.size
        )

        return byteWrapper.wrap(amiiboId).long
    }

    private fun getTail(id: Long): String {
        return String.format(
            HEAD_TAIL_STRING_FORMAT_VALUE,
            (id and TAIL_MASK shr TAIL_BITSHIFT).toInt()
        )
    }

    private fun getHead(id: Long): String {
        return String.format(
            HEAD_TAIL_STRING_FORMAT_VALUE,
            (id and HEAD_MASK shr HEAD_BITSHIFT).toInt()
        )
    }


    private fun isPageAvailable(pages: ByteArray?) =
        pages == null || pages.size != PAGE_SIZE * BULK_READ_PAGE_COUNT
}

private val hexArray = "0123456789ABCDEF".toCharArray()
private const val EMPTY = ""
private const val INIT_POSITION = 0
private const val AMIIBO_ID_OFFSET = 0x54
private const val HEAD_TAIL_STRING_FORMAT_VALUE = "%08x"
private const val TAIL_BITSHIFT = 4 * 0
private const val TAIL_MASK = 0x00000000FFFFFFFFL
private const val HEAD_MASK = -0x100000000L
private const val HEAD_BITSHIFT = 4 * 8
private const val AMIIBO_ID_SIZE = 8
private const val TAG_FILE_SIZE = 532;
private const val PAGE_SIZE = 4
private const val BULK_READ_PAGE_COUNT = 4
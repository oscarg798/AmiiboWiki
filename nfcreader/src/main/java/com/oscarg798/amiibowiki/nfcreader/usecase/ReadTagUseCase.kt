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

package com.oscarg798.amiibowiki.nfcreader.usecase

import android.nfc.Tag
import com.oscarg798.amiibowiki.core.extensions.getOrTransform
import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.nfcreader.errors.InvalidTagDataException
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.errors.UnknownReadError
import com.oscarg798.amiibowiki.nfcreader.errors.WrongPageFormatException
import com.oscarg798.amiibowiki.nfcreader.repository.NFCReaderRepository
import javax.inject.Inject

class ReadTagUseCase @Inject constructor(
    private val repository: NFCReaderRepository
) {

    fun execute(tag: Tag): AmiiboIdentifier {
        return runCatching {
            repository.getamiiboIdentifierFromTag(tag)
        }.getOrTransform {
            throw when (it) {
                is IllegalStateException,
                is InvalidTagDataException,
                is WrongPageFormatException -> NFCReaderFailure.TagNotSupported(it)
                is UnknownReadError -> NFCReaderFailure.Unknow(it)
                else -> it
            }
        }
    }
}

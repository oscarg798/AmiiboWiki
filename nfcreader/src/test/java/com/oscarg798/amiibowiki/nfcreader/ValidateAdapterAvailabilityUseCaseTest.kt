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
import com.oscarg798.amiibowiki.nfcreader.usecase.ValidateAdapterAvailabilityUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import io.mockk.every
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class ValidateAdapterAvailabilityUseCaseTest {

    private val nfcAdapter =
        relaxedMockk<NfcAdapter>()
    private lateinit var usecase: ValidateAdapterAvailabilityUseCase

    @Before
    fun setup() {
        every { nfcAdapter.isEnabled } answers { true }
        usecase = ValidateAdapterAvailabilityUseCase(nfcAdapter)
    }

    @Test
    fun `given adapter enabled when usecase is executed then it should return true`() {
        usecase.execute() shouldBeEqualTo true

        verify {
            nfcAdapter.isEnabled
        }
    }

    @Test
    fun `given adapter disbaled when usecase is executed then it should return false`() {
        every { nfcAdapter.isEnabled } answers { false }
        usecase.execute() shouldBeEqualTo false

        verify {
            nfcAdapter.isEnabled
        }
    }
}

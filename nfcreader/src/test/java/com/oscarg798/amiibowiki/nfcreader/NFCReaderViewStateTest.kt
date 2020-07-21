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

import com.oscarg798.amiibowiki.core.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.nfcreader.errors.NFCReaderFailure
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderResult
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NFCReaderViewStateTest {

    private lateinit var state: NFCReaderViewState

    @Before
    fun setup() {
        state = NFCReaderViewState.init()
    }

    @Test
    fun `given reading result when state is reduced then new state should reflect this status`() {
        val newState = state.reduce(NFCReaderResult.Reading) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.Loading, newState.loading)
        assertEquals(NFCReaderViewState.AdapterStatus.Idle, newState.adapterStatus)
        assertEquals(NFCReaderViewState.Status.None, newState.status)
        assertNull(newState.error)
    }

    @Test
    fun `given read success result when state is reduced then new state should reflect this status`() {
        val newState =
            state.reduce(NFCReaderResult.ReadSuccessful(AMIIBO_IDENTIFIER)) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(NFCReaderViewState.AdapterStatus.Idle, newState.adapterStatus)
        assertEquals(
            AMIIBO_IDENTIFIER,
            (newState.status as NFCReaderViewState.Status.ReadSuccessful).amiiboIdentifier
        )
        assertNull(newState.error)
    }

    @Test
    fun `given  adapter disabled result when state is reduced then new state should reflect this status`() {
        val newState =
            state.reduce(NFCReaderResult.AdapterDisabled) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(NFCReaderViewState.AdapterStatus.Idle, newState.adapterStatus)
        assertEquals(NFCReaderViewState.Status.None, newState.status)
        assertNotNull(newState.error)
        assert(newState.error is NFCReaderFailure.AdapterDisabled)
    }

    @Test
    fun `given  adapter ready result when state is reduced then new state should reflect this status`() {
        val newState =
            state.reduce(NFCReaderResult.AdapterReady) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(NFCReaderViewState.AdapterStatus.AdapterAvailable, newState.adapterStatus)
        assertEquals(NFCReaderViewState.Status.None, newState.status)
        assertNull(newState.error)
    }

    @Test
    fun `given  adapter stoped result when state is reduced then new state should reflect this status`() {
        val newState =
            state.reduce(NFCReaderResult.AdapterStoped) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(
            NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped,
            newState.adapterStatus
        )
        assertEquals(NFCReaderViewState.Status.None, newState.status)
        assertNull(newState.error)
    }

    @Test
    fun `given error result when state is reduced then new state should reflect this status`() {
        val newState =
            state.reduce(NFCReaderResult.Error(NFCReaderFailure.Unknow(Exception()))) as NFCReaderViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertEquals(NFCReaderViewState.AdapterStatus.Idle, newState.adapterStatus)
        assertEquals(NFCReaderViewState.Status.None, newState.status)
        assertNotNull(newState.error)
        assert(newState.error is NFCReaderFailure.Unknow)
    }
}

private val AMIIBO_IDENTIFIER = AmiiboIdentifier("1", "2")

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

import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListFailure
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListResult
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.mvi.ViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test


class AmiiboListViewStateTest {

    private lateinit var state: AmiiboListViewState

    @Before
    fun setup() {
        state = AmiiboListViewState.init()
    }

    @Test
    fun `when init is called then loading and status are none`() {
        assertEquals(ViewState.LoadingState.None, state.loading)
        assertEquals(AmiiboListViewState.Status.None, state.status)
        assertEquals(AmiiboListViewState.Filtering.None, state.filtering)
        assertEquals(AmiiboListViewState.ShowingFilters.None, state.showingFilters)
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, state.showAmiiboDetail)
        assertNull(state.error)
    }

    @Test
    fun `when result is success then loading is none and status success`() {
        val newState =
            state.reduce(AmiiboListResult.FetchSuccess(AMIIBO_RESULT)) as AmiiboListViewState
        assertEquals(ViewState.LoadingState.None, newState.loading)
        assert(newState.status is AmiiboListViewState.Status.AmiibosFetched)
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, newState.showAmiiboDetail)
        assertEquals(
            VIEWAMIIBO,
            (newState.status as AmiiboListViewState.Status.AmiibosFetched).amiibos
        )
    }

    @Test
    fun `when result is error then loading is none and status error`() {
        val newState =
            state.reduce(AmiiboListResult.Error(AmiiboListFailure.UnknowError)) as AmiiboListViewState
        assertEquals(ViewState.LoadingState.None, newState.loading)
        assert(newState.error != null)
        assert(newState.error is AmiiboListFailure.UnknowError)
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, newState.showAmiiboDetail)
    }

    @Test
    fun `when result is loading then loading is loading and status None`() {
        val newState = state.reduce(AmiiboListResult.Loading) as AmiiboListViewState
        assertEquals(ViewState.LoadingState.Loading, newState.loading)
        assert(newState.status is AmiiboListViewState.Status.None)
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, newState.showAmiiboDetail)
    }

    @Test
    fun `when result is amiibo filters fetched then filtering is fetch success`() {
        val newState =
            state.reduce(AmiiboListResult.FiltersFetched(AMIIBO_TYPE)) as AmiiboListViewState
        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertNull(newState.error)
        assertEquals(AmiiboListViewState.Status.None, newState.status)
        assert(newState.showingFilters is AmiiboListViewState.ShowingFilters.FetchSuccess)
        assertEquals(
            listOf(ViewAmiiboType("1", "2")),
            (newState.showingFilters as AmiiboListViewState.ShowingFilters.FetchSuccess).filters
        )
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, newState.showAmiiboDetail)
    }

    @Test
    fun `when result is amiibo filtered then state is Filtering Fetch Success `() {
        val newState =
            state.reduce(AmiiboListResult.AmiibosFiltered(AMIIBO_RESULT)) as AmiiboListViewState
        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertNull(newState.error)
        assertEquals(AmiiboListViewState.Status.None, newState.status)
        assert(newState.showingFilters is AmiiboListViewState.ShowingFilters.None)
        assert(newState.filtering is AmiiboListViewState.Filtering.FetchSuccess)
        assertEquals(AmiiboListViewState.ShowAmiiboDetail.None, newState.showAmiiboDetail)
    }

    @Test
    fun `when result is show detail then state is ShowAmiiboDetail`() {
        val newState =
            state.reduce(AmiiboListResult.ShowAmiiboDetail("1")) as AmiiboListViewState

        assertEquals(ViewState.LoadingState.None, newState.loading)
        assertNull(newState.error)
        assertEquals(AmiiboListViewState.Status.None, newState.status)
        assert(newState.showingFilters is AmiiboListViewState.ShowingFilters.None)
        assert(newState.filtering is AmiiboListViewState.Filtering.None)
        assert(newState.showAmiiboDetail is AmiiboListViewState.ShowAmiiboDetail.Show)
        assertEquals("1", (newState.showAmiiboDetail as AmiiboListViewState.ShowAmiiboDetail.Show).tail)
    }
}

private val AMIIBO_TYPE = listOf(AmiiboType("1", "2"))
private val VIEWAMIIBO = listOf(ViewAmiibo("11", "12", "3", "5"))
private val AMIIBO_RESULT = listOf(
    Amiibo(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        AmiiboReleaseDate("7", "8", "9", "10"),
        "11", "12"
    )
)

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

package com.oscarg798.amiibowiki.settings

import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsResult
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class SettingsViewStateTest {

    private lateinit var state: SettingsViewState

    @Before
    fun setup() {
        state = SettingsViewState.init()
    }

    @Test
    fun `when state is initialized then it should be not loading nor have a result`() {
        state.loading shouldBeEqualTo ViewState.LoadingState.None
        state.createPreferencesStatus shouldBeEqualTo SettingsViewState.CreatePreferencesStatus.None
        state.showDarkModeDialog shouldBeEqualTo false
        state.showDevelopmentActivity shouldBeEqualTo false
        state.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.None
    }

    @Test
    fun `given loading result when state is reduced then state should be loading`() {
        val newState = state.reduce(SettingsResult.Loading) as SettingsViewState

        newState.loading shouldBeEqualTo ViewState.LoadingState.Loading
        newState.createPreferencesStatus shouldBeEqualTo SettingsViewState.CreatePreferencesStatus.None
        newState.showDarkModeDialog shouldBeEqualTo false
        newState.showDevelopmentActivity shouldBeEqualTo false
        state.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.None
    }

    @Test
    fun `given some preferences as result when state is reduced then state should not be loading and should have the preferences`() {
        val newState =
            state.reduce(SettingsResult.PreferencesCreated(PREFERENCES)) as SettingsViewState

        newState.loading shouldBeEqualTo ViewState.LoadingState.None
        assert(newState.createPreferencesStatus is SettingsViewState.CreatePreferencesStatus.PreferencesCreated)
        (newState.createPreferencesStatus as SettingsViewState.CreatePreferencesStatus.PreferencesCreated).preferences shouldBeEqualTo PREFERENCES
        newState.showDarkModeDialog shouldBeEqualTo false
        newState.showDevelopmentActivity shouldBeEqualTo false
        state.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.None
    }

    @Test
    fun `given show development activity as result when state is reduced then state should reflect this`() {
        val newState = state.reduce(SettingsResult.ShowDevelopmentActivity) as SettingsViewState

        newState.loading shouldBeEqualTo ViewState.LoadingState.None
        newState.createPreferencesStatus shouldBeEqualTo SettingsViewState.CreatePreferencesStatus.None
        newState.showDarkModeDialog shouldBeEqualTo false
        newState.showDevelopmentActivity shouldBeEqualTo true
        state.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.None
    }

    @Test
    fun `given show dark mode dialog as result when state is reduced then state should reflect this`() {
        val newState = state.reduce(SettingsResult.ShowDarkModeDialog) as SettingsViewState

        newState.loading shouldBeEqualTo ViewState.LoadingState.None
        newState.createPreferencesStatus shouldBeEqualTo SettingsViewState.CreatePreferencesStatus.None
        newState.showDarkModeDialog shouldBeEqualTo true
        newState.showDevelopmentActivity shouldBeEqualTo false
        state.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.None
    }

    @Test
    fun `given dark mode selection is saved when state is reduced then state should reflect the selected option`() {
        val newState = state.reduce(SettingsResult.DarkModeSelectionSaved("1")) as SettingsViewState

        newState.loading shouldBeEqualTo ViewState.LoadingState.None
        newState.createPreferencesStatus shouldBeEqualTo SettingsViewState.CreatePreferencesStatus.None
        newState.showDarkModeDialog shouldBeEqualTo false
        newState.showDevelopmentActivity shouldBeEqualTo false
        newState.darkModeSelectedStatus shouldBeEqualTo SettingsViewState.DarkModeSelectedStatus.Selected("1")
    }
}

private val PREFERENCES = setOf(
    PreferenceBuilder("1", "2"),
    PreferenceBuilder("3", "4")
)

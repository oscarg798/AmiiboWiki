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

import com.oscarg798.amiibowiki.settings.featurepoint.DARK_MODE_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.featurepoint.DEVELOPMENT_ACTIVITY_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsReducer
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.amiibowiki.settings.usecases.SaveDarkModeSelectionUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import com.oscarg798.flagly.featurepoint.SuspendFeaturePoint
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest : ViewModelTestRule.ViewModelCreator<SettingsViewState, SettingsViewModel> {

    @get: Rule
    val viewModelTestTule = ViewModelTestRule<SettingsViewState, SettingsViewModel>(this)

    private val featurePoint = relaxedMockk<SuspendFeaturePoint<PreferenceBuilder, Unit>>()
    private val saveDarkModeSelectionUseCase = relaxedMockk<SaveDarkModeSelectionUseCase>()
    private val reducer = spyk(SettingsReducer())

    override fun create(): SettingsViewModel = SettingsViewModel(
        saveDarkModeSelectionUseCase,
        featurePoint,
        reducer,
        viewModelTestTule.coroutineContextProvider
    )

    @Test
    fun `given create preference wish when wish is processed then it should emit two states`() {
        coEvery { featurePoint.createFeatures(Unit) } answers { PREFERENCES.toList() }

        viewModelTestTule.viewModel.onWish(SettingsWish.CreatePreferences)

        viewModelTestTule.testCollector wasValueEmiited SettingsViewState.Idling

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            SettingsViewState.Idling,
            SettingsViewState.Loading,
            SettingsViewState.Preferences(PREFERENCES.toList())
        )

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given dark mode preference clicked when wish is processed then it should emit an state to show the dark mode dialog`() {
        viewModelTestTule.viewModel.onWish(SettingsWish.PreferenceClicked(DARK_MODE_PREFERENCE_KEY))

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            SettingsViewState.Idling,
            SettingsViewState.ShowingDarkModeDialog
        )

        coVerify(exactly = 1) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given  development activity preference clicked when wish is processed then it should emit an state to show development activity`() {
        viewModelTestTule.viewModel.onWish(SettingsWish.PreferenceClicked(DEVELOPMENT_ACTIVITY_PREFERENCE_KEY))

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            SettingsViewState.Idling,
            SettingsViewState.ShowingDevelopmentActivity
        )

        coVerify(exactly = 1) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given dark mode options was selected when wish is processed then it should emit the state should be idling`() {
        viewModelTestTule.viewModel.onWish(SettingsWish.DarkModeOptionSelected("1"))

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            SettingsViewState.Idling,
            SettingsViewState.Loading,
            SettingsViewState.Idling
        )

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }
}

private val PREFERENCES = setOf(
    PreferenceBuilder.Clickable("1", "2"),
    PreferenceBuilder.Clickable("1", "2")
)

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
import com.oscarg798.amiibowiki.settings.featurepoint.DARK_MODE_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.featurepoint.DEVELOPMENT_ACTIVITY_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.amiibowiki.settings.usecases.SaveDarkModeSelectionUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.CoroutinesTestRule
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import com.oscarg798.flagly.featurepoint.FeaturePoint
import io.mockk.every
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    private val featurePoint = relaxedMockk<FeaturePoint<PreferenceBuilder, Unit>>()
    private val saveDarkModeSelectionUseCase = relaxedMockk<SaveDarkModeSelectionUseCase>()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var testCollector: TestCollector<SettingsViewState>

    @Before
    fun setup() {
        testCollector = TestCollector()
        viewModel = SettingsViewModel(
            saveDarkModeSelectionUseCase,
            featurePoint,
            coroutinesRule.coroutineContextProvider
        )
    }

    @Test
    fun `given create preference wish when wish is processed then it should emit two states`() {
        every { featurePoint.createFeatures(Unit) } answers { PREFERENCES.toList() }

        viewModel.onWish(SettingsWish.CreatePreferences)
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wasValueEmiited SettingsViewState.init()

        testCollector wasValueEmiited SettingsViewState(
            loading = ViewState.LoadingState.Loading,
            createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.None,
            darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.None
        )

        testCollector wasValueEmiited SettingsViewState(
            loading = ViewState.LoadingState.None,
            createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.PreferencesCreated(
                PREFERENCES.toList()
            ),
            darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.None
        )

        testCollector hasSize 3
    }

    @Test
    fun `given dark mode preference clicked when wish is processed then it should emit an state to show the dark mode dialog`() {
        viewModel.onWish(SettingsWish.PreferenceClicked(DARK_MODE_PREFERENCE_KEY))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            SettingsViewState.init(),
            SettingsViewState(
                loading = ViewState.LoadingState.None,
                createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.None,
                showDarkModeDialog = true,
                showDevelopmentActivity = false,
                darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.None
            )
        )
    }

    @Test
    fun `given  development activity preference clicked when wish is processed then it should emit an state to show the dark mode dialog`() {
        viewModel.onWish(SettingsWish.PreferenceClicked(DEVELOPMENT_ACTIVITY_PREFERENCE_KEY))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            SettingsViewState.init(),
            SettingsViewState(
                loading = ViewState.LoadingState.None,
                createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.None,
                showDarkModeDialog = false,
                showDevelopmentActivity = true,
                darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.None
            )
        )
    }

    @Test
    fun `given dark mode options was selected when wish is processed then it should emit the state with the rigth result`() {
        viewModel.onWish(SettingsWish.DarkModeOptionSelected("1"))
        testCollector.test(coroutinesRule.testCoroutineScope, viewModel.state)

        testCollector wereValuesEmitted listOf(
            SettingsViewState.init(),
            SettingsViewState(
                loading = ViewState.LoadingState.Loading,
                createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.None,
                darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.None,
                showDarkModeDialog = false,
                showDevelopmentActivity = false
            ),
            SettingsViewState(
                loading = ViewState.LoadingState.None,
                createPreferencesStatus = SettingsViewState.CreatePreferencesStatus.None,
                darkModeSelectedStatus = SettingsViewState.DarkModeSelectedStatus.Selected("1"),
                showDarkModeDialog = false,
                showDevelopmentActivity = false
            )
        )
    }
}

private val PREFERENCES = setOf(
    PreferenceBuilder("1", "2"),
    PreferenceBuilder("3", "4")
)

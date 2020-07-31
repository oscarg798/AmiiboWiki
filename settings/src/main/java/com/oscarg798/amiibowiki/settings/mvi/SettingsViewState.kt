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

package com.oscarg798.amiibowiki.settings.mvi

import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder

data class SettingsViewState(
    val loading: ViewState.LoadingState,
    val createPreferencesStatus: CreatePreferencesStatus,
    val darkModeSelectedStatus: DarkModeSelectedStatus,
    val showDevelopmentActivity: Boolean = false,
    val showDarkModeDialog: Boolean = false,
    val shouldActivityBeRecreated: Boolean = true
) : ViewState<SettingsResult> {

    sealed class CreatePreferencesStatus {
        object None : CreatePreferencesStatus()
        data class PreferencesCreated(val preferences: Collection<PreferenceBuilder>) :
            CreatePreferencesStatus()
    }

    sealed class DarkModeSelectedStatus {
        object None : DarkModeSelectedStatus()
        data class Selected(val option: String) : DarkModeSelectedStatus()
    }

    override fun reduce(result: SettingsResult): ViewState<SettingsResult> = when (result) {
        is SettingsResult.PreferencesCreated -> copy(
            loading = ViewState.LoadingState.None,
            createPreferencesStatus = CreatePreferencesStatus.PreferencesCreated(result.preferences),
            darkModeSelectedStatus = DarkModeSelectedStatus.None,
            showDevelopmentActivity = false,
            showDarkModeDialog = false
        )
        is SettingsResult.ShowDarkModeDialog -> copy(
            loading = ViewState.LoadingState.None,
            createPreferencesStatus = CreatePreferencesStatus.None,
            darkModeSelectedStatus = DarkModeSelectedStatus.None,
            showDevelopmentActivity = false,
            showDarkModeDialog = true
        )
        is SettingsResult.ShowDevelopmentActivity -> copy(
            loading = ViewState.LoadingState.None,
            createPreferencesStatus = CreatePreferencesStatus.None,
            darkModeSelectedStatus = DarkModeSelectedStatus.None,
            showDevelopmentActivity = true,
            showDarkModeDialog = false
        )
        is SettingsResult.DarkModeSelectionSaved -> copy(
            loading = ViewState.LoadingState.None,
            createPreferencesStatus = CreatePreferencesStatus.None,
            darkModeSelectedStatus = DarkModeSelectedStatus.Selected(result.optionSelected),
            showDevelopmentActivity = false,
            showDarkModeDialog = false
        )
        is SettingsResult.Loading -> copy(
            loading = ViewState.LoadingState.Loading,
            createPreferencesStatus = CreatePreferencesStatus.None,
            darkModeSelectedStatus = DarkModeSelectedStatus.None,
            showDevelopmentActivity = false,
            showDarkModeDialog = false
        )
    }

    companion object {
        fun init() = SettingsViewState(
            ViewState.LoadingState.None, CreatePreferencesStatus.None,
            DarkModeSelectedStatus.None
        )
    }
}

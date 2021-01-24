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

import com.oscarg798.amiibowiki.core.mvi.Reducer

import javax.inject.Inject

class SettingsReducer @Inject constructor() : Reducer<SettingsResult, SettingsViewState> {

    override suspend fun reduce(
        state: SettingsViewState,
        from: SettingsResult
    ): SettingsViewState = when (from) {
        is SettingsResult.Loading -> SettingsViewState.Loading
        is SettingsResult.PreferencesCreated -> SettingsViewState.Preferences(from.preferences)
        is SettingsResult.DarkModeSelectionSaved -> SettingsViewState.ActivityShouldBeRecreated
        is SettingsResult.ShowDevelopmentActivity -> SettingsViewState.ShowingDevelopmentActivity
        is SettingsResult.ShowDarkModeDialog -> SettingsViewState.ShowingDarkModeDialog
    }
}

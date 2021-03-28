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

import androidx.lifecycle.viewModelScope
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.utils.CoroutineContextProvider
import com.oscarg798.amiibowiki.settings.featurepoint.DARK_MODE_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.amiibowiki.settings.mvi.UiEffect
import com.oscarg798.amiibowiki.settings.mvi.ViewState
import com.oscarg798.amiibowiki.settings.usecases.SaveDarkModeSelectionUseCase
import com.oscarg798.flagly.featurepoint.SuspendFeaturePoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val saveDarkModeSelectionUseCase: SaveDarkModeSelectionUseCase,
    private val featurePoint: SuspendFeaturePoint<@JvmSuppressWildcards PreferenceBuilder, @JvmSuppressWildcards Unit>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<ViewState, UiEffect, SettingsWish>(ViewState()) {

    override fun processWish(wish: SettingsWish) {
        when (wish) {
            is SettingsWish.CreatePreferences -> createPreferences()
            is SettingsWish.PreferenceClicked -> onPreferenceClicked(wish)
            is SettingsWish.DarkModeOptionSelected -> saveDarkMode(wish)
        }
    }

    private fun saveDarkMode(wish: SettingsWish.DarkModeOptionSelected) {
        viewModelScope.launch {
            updateState { it.copy(loading = true) }
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    saveDarkModeSelectionUseCase.execute(wish.option)
                }
            }.onSuccess {
                _uiEffect.tryEmit(UiEffect.RecreateActivity)
                updateState { it.copy(loading = false) }
            }
        }
    }

    private fun onPreferenceClicked(wish: SettingsWish.PreferenceClicked) {
        when (wish.preferenceKey) {
            DARK_MODE_PREFERENCE_KEY -> _uiEffect.tryEmit(UiEffect.ShowingDarkModeDialog)
            else -> _uiEffect.tryEmit(UiEffect.ShowingDevelopmentActivity)
        }
    }

    private fun createPreferences() {
        viewModelScope.launch {
            updateState { it.copy(loading = true) }
            runCatching {
                withContext(coroutineContextProvider.backgroundDispatcher) {
                    featurePoint.createFeatures(Unit)
                }
            }.onSuccess { preferences ->
                updateState { it.copy(loading = false, preferences = preferences) }
            }
        }
    }
}

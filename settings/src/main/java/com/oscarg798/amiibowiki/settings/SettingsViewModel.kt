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

import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.base.AbstractViewModel
import com.oscarg798.amiibowiki.core.mvi.Reducer
import com.oscarg798.amiibowiki.settings.featurepoint.DARK_MODE_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsResult
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.amiibowiki.settings.usecases.SaveDarkModeSelectionUseCase
import com.oscarg798.flagly.featurepoint.SuspendFeaturePoint
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class SettingsViewModel @Inject constructor(
    private val saveDarkModeSelectionUseCase: SaveDarkModeSelectionUseCase,
    private val featurePoint: SuspendFeaturePoint<@JvmSuppressWildcards PreferenceBuilder, @JvmSuppressWildcards Unit>,
    override val reducer: Reducer<@JvmSuppressWildcards SettingsResult, @JvmSuppressWildcards SettingsViewState>,
    override val coroutineContextProvider: CoroutineContextProvider
) : AbstractViewModel<SettingsWish, SettingsResult, SettingsViewState>(SettingsViewState.init()) {

    override suspend fun getResult(wish: SettingsWish): Flow<SettingsResult> = when (wish) {
        is SettingsWish.CreatePreferences -> getCreatePreferenceResult()
        is SettingsWish.PreferenceClicked -> getPreferenceClickedResult(wish)
        is SettingsWish.DarkModeOptionSelected -> getSaveDarkModeSelection(wish)
    }

    private fun getSaveDarkModeSelection(wish: SettingsWish.DarkModeOptionSelected) =
        flow<SettingsResult> {
            saveDarkModeSelectionUseCase.execute(wish.option)
            emit(SettingsResult.DarkModeSelectionSaved(wish.option))
        }.onStart {
            emit(SettingsResult.Loading)
        }.flowOn(coroutineContextProvider.backgroundDispatcher)

    private fun getPreferenceClickedResult(wish: SettingsWish.PreferenceClicked) = flowOf(
        when (wish.preferenceKey) {
            DARK_MODE_PREFERENCE_KEY -> SettingsResult.ShowDarkModeDialog
            else -> SettingsResult.ShowDevelopmentActivity
        }
    )

    private fun getCreatePreferenceResult() = flow {
        emit(SettingsResult.PreferencesCreated(featurePoint.createFeatures(Unit)) as SettingsResult)
    }.onStart {
        emit(SettingsResult.Loading)
    }.flowOn(coroutineContextProvider.backgroundDispatcher)
}

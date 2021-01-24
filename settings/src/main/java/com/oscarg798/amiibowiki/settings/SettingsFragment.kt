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

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType as EditTextInputType
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.oscarg798.amiibowiki.core.utils.TextChangeListenerAdapter
import com.oscarg798.amiibowiki.settings.featurepoint.DARK_MODE_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.featurepoint.DEVELOPMENT_ACTIVITY_PREFERENCE_KEY
import com.oscarg798.amiibowiki.settings.models.PreferenceBuilder
import com.oscarg798.amiibowiki.settings.mvi.SettingsViewState
import com.oscarg798.amiibowiki.settings.mvi.SettingsWish
import com.oscarg798.flagly.developeroptions.FeatureFlagHandlerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModelCompat by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect { state->
                when(state){
                    is SettingsViewState.Preferences -> {
                        /**
                         * Preferences must exists before setting click listener
                         */
                        setupPreferencesClickListener(viewModel)
                        addPreferences(state.preferences)
                    }
                    SettingsViewState.ShowingDevelopmentActivity -> startActivity(
                        Intent(
                            requireContext(),
                            FeatureFlagHandlerActivity::class.java
                        )
                    )
                    SettingsViewState.ActivityShouldBeRecreated ->  {
                        startActivity(requireActivity().intent)
                        requireActivity().finish()
                    }
                    SettingsViewState.ShowingDarkModeDialog ->showDarkModeDialog()
                }
            }
        }

        viewModel.onWish(SettingsWish.CreatePreferences)
    }

    private fun showDarkModeDialog() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.select_dialog_singlechoice,
            listOf<String>(
                getString(R.string.system_default_dark_mode_option),
                getString(R.string.ligth_dark_mode_option),
                getString(R.string.dark_mode_option)
            )
        )
        val builder = AlertDialog.Builder(requireContext())

        builder.setAdapter(adapter) { _, which ->
            val selectedOption = adapter.getItem(which)
            require(selectedOption != null)
            viewModel.onWish(SettingsWish.DarkModeOptionSelected(selectedOption))
        }
        builder.show()
    }

    private fun setupPreferencesClickListener(viewModel: SettingsViewModelCompat) {
        merge(getDarkModePreferenceClick(), getDevelopmentActivityClick())
            .onEach {
                viewModel.onWish(it)
            }.launchIn(lifecycleScope)
    }

    private fun getDarkModePreferenceClick(): Flow<SettingsWish> = callbackFlow {
        findPreference<Preference>(DARK_MODE_PREFERENCE_KEY)?.setOnPreferenceClickListener { preference ->
            offer(SettingsWish.PreferenceClicked(preference.key))
        }

        awaitClose {
            preferenceScreen.setOnPreferenceClickListener { false }
        }
    }

    private fun getDevelopmentActivityClick(): Flow<SettingsWish> = callbackFlow {
        findPreference<Preference>(DEVELOPMENT_ACTIVITY_PREFERENCE_KEY)?.setOnPreferenceClickListener { preference ->
            offer(SettingsWish.PreferenceClicked(preference.key))
        }

        awaitClose {
            preferenceScreen.setOnPreferenceClickListener { false }
        }
    }

    private fun addPreferences(preferences: Collection<PreferenceBuilder>) {
        preferences.map { preferenceBuilder ->
            when (preferenceBuilder) {
                is PreferenceBuilder.Text -> preferenceBuilder.mapToEditTextPreference()
                is PreferenceBuilder.Clickable -> preferenceBuilder.mapSimplePreference()
            }
        }.forEach { preference ->
            preferenceScreen.addPreference(preference)
        }
    }

    private fun PreferenceBuilder.Text.mapToEditTextPreference(): Preference {
        val preference = EditTextPreference(requireContext())
        preference.setOnBindEditTextListener { editText ->
            editText.inputType = when (inputType) {
                is PreferenceBuilder.Text.InputType.Number -> EditTextInputType.TYPE_CLASS_NUMBER
            }

            editText.addTextChangedListener(object : TextChangeListenerAdapter {
                override fun afterTextChanged(newText: Editable?) {
                    this@mapToEditTextPreference.textPreferenceChangeListener?.onPreferenceChange(
                        newText?.toString(),
                        editText,
                        this
                    )
                }
            })
        }

        preference.key = key
        preference.title = title
        preference.setDefaultValue(defaultValue)

        return preference
    }

    private fun PreferenceBuilder.Clickable.mapSimplePreference(): Preference {
        val preference = Preference(requireContext())
        preference.key = key
        preference.title = title
        iconResourceId?.let { drawableKey ->
            preference.icon = ContextCompat.getDrawable(requireContext(), drawableKey)
        }

        return preference
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}

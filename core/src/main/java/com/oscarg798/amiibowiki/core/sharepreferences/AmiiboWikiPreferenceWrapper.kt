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

package com.oscarg798.amiibowiki.core.sharepreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject

class AmiiboWikiPreferenceWrapper @Inject constructor(private val context: Context) :
    SharedPreferencesWrapper {

    private lateinit var preferences: SharedPreferences
    private lateinit var currentPreferencesName: String
    private val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun addStringValue(key: String, value: String, preferenceName: String) {
        val preferenceEditor = getPreferences(preferenceName).edit()
        preferenceEditor.putString(key, value)
        preferenceEditor.apply()
    }

    override fun getStringValue(key: String, preferenceName: String): String? {
        val preference = getPreferences(preferenceName)
        return preference.getString(key, null)
    }

    override fun getIntValueFromUserPreferences(key: String): Int {
        val preference = getPreferences(USER_PREFERENCES_NAME)
        return (preference.getString(key, null) ?: DEFAULT_USER_PREFERENCES_INT_VALUE).toInt()
    }

    override fun removePreferenceKey(key: String, preferenceName: String) {
        val preferenceEditor = getPreferences(preferenceName).edit()
        preferenceEditor.remove(key)
        preferenceEditor.apply()
    }

    private fun getPreferences(preferenceName: String) =
        if (preferenceName == USER_PREFERENCES_NAME) {
            userPreferences
        } else if (!::preferences.isInitialized || preferenceName != currentPreferencesName) {
            context.getSharedPreferences(preferenceName, MODE_PRIVATE)
        } else {
            preferences
        }
}

const val DEFAULT_USER_PREFERENCES_INT_VALUE = "0"
const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"

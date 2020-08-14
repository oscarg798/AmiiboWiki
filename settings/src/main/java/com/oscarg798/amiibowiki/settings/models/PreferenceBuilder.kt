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

package com.oscarg798.amiibowiki.settings.models

import android.text.TextWatcher
import android.widget.EditText

sealed class PreferenceBuilder(
    private val key: String,
    private val title: String,
    private val iconResourceId: Int? = null
) {

    data class Clickable(
        val key: String,
        val title: String,
        val iconResourceId: Int? = null
    ) : PreferenceBuilder(key, title, iconResourceId)

    data class Text(
        val key: String,
        val title: String,
        val inputType: InputType,
        val defaultValue: String,
        val textPreferenceChangeListener: TextPreferenceChangeListener? = null,
        val iconResourceId: Int? = null
    ) : PreferenceBuilder(key, title, iconResourceId) {
        sealed class InputType {
            data class Number(val minumum: Int? = 0, val maximun: Int? = 0) : InputType()
        }
    }
}

interface TextPreferenceChangeListener {

    fun onPreferenceChange(newPreferenceText: String?, editText: EditText, watcher: TextWatcher)
}

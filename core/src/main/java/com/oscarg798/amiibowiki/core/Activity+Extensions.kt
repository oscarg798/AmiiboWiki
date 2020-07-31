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

package com.oscarg798.amiibowiki.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

typealias DeepLink = String

fun AppCompatActivity.startDeepLinkIntent(deepLink: DeepLink, arguments: Bundle? = null) {
    val intent = createDeepLinkIntent(deepLink)
    startIntent(arguments, intent)
}

fun AppCompatActivity.startDeepLinkIntent(
    deepLink: DeepLink,
    arguments: Bundle? = null,
    flags: Int
) {
    val intent = createDeepLinkIntent(deepLink)
    intent.flags = flags
    startIntent(arguments, intent)
}

private fun AppCompatActivity.startIntent(
    arguments: Bundle?,
    intent: Intent
) {
    arguments?.let {
        intent.putExtras(it)
    }
    startActivity(intent)
}

private fun createDeepLinkIntent(deepLink: DeepLink): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(deepLink)
    return intent
}

fun AppCompatActivity.verifyNightMode() {
    if (!isAndroidQOrHigher()) {
        return
    }

    val preference = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    val userSelectedNigthMode = getNightMode(preference.getString(DARK_MODE_SELECTION_KEY, null))
    val nightMode = AppCompatDelegate.getDefaultNightMode()

    if (nightMode == userSelectedNigthMode) {
        return
    }

    AppCompatDelegate.setDefaultNightMode(userSelectedNigthMode)
}

private fun AppCompatActivity.getNightMode(nigthMode: String?) = when (nigthMode) {
    getString(R.string.system_default_dark_mode_option),
    null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    getString(R.string.ligth_dark_mode_option) -> AppCompatDelegate.MODE_NIGHT_NO
    else -> AppCompatDelegate.MODE_NIGHT_YES
}

fun isAndroidQOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

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

package com.oscarg798.amiibowiki.core.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.oscarg798.amiibowiki.core.R
import com.oscarg798.amiibowiki.core.constants.DARK_MODE_SELECTION_KEY
import com.oscarg798.amiibowiki.core.constants.PREFERENCE_NAME
import com.stfalcon.imageviewer.StfalconImageViewer
import java.util.stream.Collectors.toList

typealias DeepLink = String

fun AppCompatActivity.startDeepLinkIntent(deepLink: DeepLink, arguments: Bundle? = null) {
    val intent =
        createDeepLinkIntent(deepLink)
    startIntent(intent, arguments)
}

fun AppCompatActivity.startDeepLinkIntentWithShareElementTransition(
    deepLink: DeepLink,
    imageView: ImageView,
    arguments: Bundle? = null
) {
    val intent = createDeepLinkIntent(deepLink)
    val options = getSceneTransitionAnimationOptions(imageView)

    startIntent(intent, options, arguments)
}

fun AppCompatActivity.startDeepLinkIntent(
    deepLink: DeepLink,
    arguments: Bundle? = null,
    flags: Int
) {
    val intent =
        createDeepLinkIntent(deepLink)
    intent.flags = flags
    startIntent(intent, arguments)
}

fun AppCompatActivity.showExpandedImages(imagesUrl: Collection<String>) {
    StfalconImageViewer.Builder<String>(
        this,
        imagesUrl.toList()
    ) { view, image ->
        view.setImage(image)
    }.build().show()
}

private fun AppCompatActivity.startIntent(
    intent: Intent,
    options: ActivityOptionsCompat,
    arguments: Bundle?
) {
    arguments?.let {
        intent.putExtras(it)
    }

    startActivity(intent, options.toBundle())
}

private fun AppCompatActivity.startIntent(
    intent: Intent,
    arguments: Bundle?
) {
    arguments?.let {
        intent.putExtras(it)
    }

    startActivity(intent)
}

private fun AppCompatActivity.getSceneTransitionAnimationOptions(imageView: ImageView) =
    ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, imageView.transitionName)

private fun createDeepLinkIntent(deepLink: DeepLink): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(deepLink)
    return intent
}

/**
 * We let the user decide if night mode should be turned on/off without following
 * the device config. Here we check the user preferences and if the activity was
 *  opened with the wrong config we finish it as the system might recreated it.
 *  The system will not recreate the activity in all the cases for example
 *  when it's the started activity so we pass a flag to control it.
 */
fun AppCompatActivity.verifyNightMode(finishOnChange: Boolean = true) {
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

    if (finishOnChange) {
        finish()
    }
}

/**
 * On Compose beta03 the app crashes
 * if you donot set the lifecycle observer dunno why
 * but this is a workaround
 */
fun AppCompatActivity.setViewTreeObserver() {
    ViewTreeLifecycleOwner.set(window.decorView, this)
}

private fun AppCompatActivity.getNightMode(nigthMode: String?) = when (nigthMode) {
    getString(R.string.system_default_dark_mode_option),
    null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    getString(R.string.ligth_dark_mode_option) -> AppCompatDelegate.MODE_NIGHT_NO
    else -> AppCompatDelegate.MODE_NIGHT_YES
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isAndroidQOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

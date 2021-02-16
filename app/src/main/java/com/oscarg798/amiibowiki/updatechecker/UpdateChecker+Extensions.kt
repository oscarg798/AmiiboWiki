/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.updatechecker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.navigation.mvi.DashboardViewState

internal fun AppCompatActivity.requestUpdate(updateType: UpdateType) {
    val builder = AlertDialog.Builder(this).setMessage(getString(R.string.update_available))
        .setCancelable(updateType is UpdateType.Flexible)
        .setPositiveButton(getString(R.string.update_dialog_positive_button)) { _, _ ->
            openPlayStore()
        }

    if (updateType !is UpdateType.Immediate) {
        builder.setNegativeButton(getString(R.string.update_dialog_negative_button)) { dialog, _ ->
            dialog.dismiss()
        }
    }

    builder.show()
}

private fun AppCompatActivity.openPlayStore() {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$GOOGLE_PLAY_URL$packageName")
        )
    )
}

private const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id="

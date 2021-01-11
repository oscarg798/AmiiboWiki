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

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

interface ImageLoaderCallback {

    fun onSuccess()
    fun onFailure()
}

fun ImageView.setImage(url: String) {
    Picasso.get().load(url).into(this)
}

fun ImageView.setImage(url: String, callback: ImageLoaderCallback) {
    Picasso.get().load(url).into(
        this,
        object : Callback {
            override fun onSuccess() {
                callback.onSuccess()
            }

            override fun onError(e: Exception) {
                callback.onFailure()
            }
        }
    )
}

fun ImageView.setImage(url: String, @DrawableRes fallback: Int) {
    Picasso.get().load(url).into(
        this,
        object : Callback {
            override fun onSuccess() {
                // NO_OP
            }

            override fun onError(e: Exception) {
                setImageDrawable(ContextCompat.getDrawable(context, fallback))
            }
        }
    )
}

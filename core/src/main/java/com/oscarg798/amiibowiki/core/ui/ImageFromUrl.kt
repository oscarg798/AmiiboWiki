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

package com.oscarg798.amiibowiki.core.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.oscarg798.amiibowiki.core.extensions.ComposeImageTarget
import com.oscarg798.amiibowiki.core.extensions.ImageCallbacks
import com.oscarg798.amiibowiki.core.extensions.LoadImageFromURLState
import com.squareup.picasso.Picasso

@SuppressLint("ModifierParameter")
@Composable
fun ImageFromUrl(
    url: String,
    imageModifier: Modifier,
    loadingModifier: Modifier = imageModifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null

) {

    var state by remember { mutableStateOf<LoadImageFromURLState>(LoadImageFromURLState.Loading) }

    val target = ComposeImageTarget(object : ImageCallbacks {
        override fun onFailure(error: Exception) {
            state = LoadImageFromURLState.Error(error)
        }

        override fun onLoaded(bitmap: Bitmap) {
            state = LoadImageFromURLState.Image(bitmap)
        }
    })

    Picasso.get().load(url).into(target)

    Row {
        when (state) {
            is LoadImageFromURLState.Error,
            is LoadImageFromURLState.Loading -> LoadingImage(modifier = loadingModifier)
            is LoadImageFromURLState.Image -> Image(
                bitmap = (state as LoadImageFromURLState.Image).image.asImageBitmap(),
                contentScale = contentScale,
                colorFilter = colorFilter,
                contentDescription = contentDescription,
                modifier = imageModifier
            )
        }
    }
}

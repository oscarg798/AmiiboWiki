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

package com.oscarg798.amiibowiki.core.commonui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.oscarg798.amiibowiki.core.extensions.ComposeImageTarget
import com.oscarg798.amiibowiki.core.extensions.LoadImageFromURLState
import com.squareup.picasso.Picasso

@SuppressLint("ModifierParameter")
@Composable
fun ImageFromUrl(url: String, imageModifier: Modifier, loadingModifier: Modifier) {
    val state by loadImage(url)

    Row {
        when (state) {
            is LoadImageFromURLState.Error,
            is LoadImageFromURLState.Loading -> LoadingImage(modifier = loadingModifier)
            is LoadImageFromURLState.Image -> Image(
                bitmap = (state as LoadImageFromURLState.Image).image.asImageBitmap(),
                contentDescription = null,
                modifier = imageModifier
            )
        }
    }
}

private fun loadImage(url: String): MutableState<LoadImageFromURLState> {
    val state = mutableStateOf<LoadImageFromURLState>(LoadImageFromURLState.Loading)
    val target = ComposeImageTarget(state)

    Picasso.get().load(url).into(target)
    return state
}

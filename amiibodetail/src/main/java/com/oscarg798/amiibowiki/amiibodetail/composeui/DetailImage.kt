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

package com.oscarg798.amiibowiki.amiibodetail.composeui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.commonui.LoadingImage
import com.oscarg798.amiibowiki.core.extensions.LoadImageFromURLState
import com.oscarg798.amiibowiki.core.extensions.loadImage
import com.oscarg798.amiibowiki.core.spacingMedium
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

@Composable
internal fun ImageDetail(url: String, onImageClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(spacingMedium)
            .height(AMIIBO_IMAGE_SIZE)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AmiiboImage(url, onImageClick)
    }
}

@Composable
private fun AmiiboImage(url: String, onImageClick: (String) -> Unit) {
    val state by remember { loadImage(url) }

    Row {
        when (state) {
            is LoadImageFromURLState.Error,
            is LoadImageFromURLState.Loading -> LoadingImage(modifier = Modifier.getAmiiboImageModifiers())
            is LoadImageFromURLState.Image -> Image(
                bitmap = (state as LoadImageFromURLState.Image).image.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .getAmiiboImageModifiers()
                    .clickable { onImageClick(url) }

            )
        }
    }
}

private fun Modifier.getAmiiboImageModifiers() = Modifier
    .fillMaxWidth()
    .fillMaxHeight()

internal val AMIIBO_IMAGE_SIZE = 250.dp

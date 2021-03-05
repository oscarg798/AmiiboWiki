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
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


@Composable
internal fun ImageDetail(url: String, onImageClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .height(250.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AmiiboImage(url, onImageClick)
    }
}

private sealed class ImageResource {

    object Loading : ImageResource()
    data class Image(val image: Bitmap) : ImageResource()
}

@Composable
private fun AmiiboImage(url: String, onImageClick: (String) -> Unit) {

    var bitmapState by remember { mutableStateOf(ImageResource.Loading) as MutableState<ImageResource> }

    val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
            bitmapState = ImageResource.Image(bitmap)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            TODO("Not yet implemented")
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            TODO("Not yet implemented")
        }

    }
    Picasso.get().load(url).into(target)

    Row {
        when (bitmapState) {
            is ImageResource.Loading -> Text("Loading")
            is ImageResource.Image -> Image(
                bitmap = (bitmapState as ImageResource.Image).image.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onImageClick(url) }
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
    }
}

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

package com.oscarg798.amiibowiki.searchgamesresults.composeui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.commonui.LoadingImage
import com.oscarg798.amiibowiki.core.extensions.LoadImageFromURLState
import com.oscarg798.amiibowiki.core.extensions.loadImage
import com.oscarg798.amiibowiki.core.spacingSmall
import com.oscarg798.amiibowiki.searchgamesresults.gameImageId

@Composable
internal fun GameImage(url: String?) {
    val containerModifier = Modifier.getAmiiboImageContainerModifier()

    if (url == null) {
        Column(containerModifier.background(MaterialTheme.colors.background)) {}
        return
    }

    val state by remember { loadImage(url) }

    val imageModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()

    Column(containerModifier) {
        when (state) {
            is LoadImageFromURLState.Loading -> LoadingImage(
                modifier = imageModifier
            )
            is LoadImageFromURLState.Image -> Image(
                bitmap = (state as LoadImageFromURLState.Image).image.asImageBitmap(),
                contentDescription = null,
                modifier = imageModifier
            )
        }
    }
}

internal fun Modifier.getAmiiboImageContainerModifier() = Modifier
    .width(AMIIBO_IMAGE_WIDTH)
    .height(AMIIBO_IMAGE_HEIGHT)
    .padding(top = spacingSmall, bottom = spacingSmall)
    .layoutId(gameImageId)

private val AMIIBO_IMAGE_WIDTH = 90.dp
private val AMIIBO_IMAGE_HEIGHT = 110.dp

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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.oscarg798.amiibowiki.core.R
import com.oscarg798.amiibowiki.core.spacingMedium
import com.oscarg798.amiibowiki.core.ui.Shimmer

@Composable
fun LoadingImage(modifier: Modifier) {
    Box {
        Box(contentAlignment = Alignment.TopCenter) {
            Image(
                alpha = IMAGE_ALPHA,
                painter = painterResource(id = R.drawable.ic_placeholder_gray),
                contentDescription = null,
                modifier = modifier.padding(spacingMedium),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }

        Box(contentAlignment = Alignment.TopCenter) {
            Shimmer(
                modifier = modifier
            )
        }
    }
}

private const val SHADER_DURATION = 1000
private const val ALPHA_TRANSPARENT = 0F
private const val NO_ALPHA = 1F
private const val IMAGE_ALPHA = 0.4F

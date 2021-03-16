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

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.core.graphics.toColorInt

@Composable
fun Shimmer(modifier: Modifier, colors: List<Color> = shaderColors) {
    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = ALPHA_TRANSPARENT,
        targetValue = NO_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHADER_DURATION),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier) {
        drawRect(
            ShaderBrush(
                LinearGradientShader(
                    from = Offset.Zero,
                    to = Offset(size.width, size.height),
                    colors = colors,
                    colorStops = listOf(ALPHA_TRANSPARENT, alpha, NO_ALPHA),
                    tileMode = TileMode.Mirror
                )
            ),
            size = size
        )
    }
}

private val shaderColors = listOf(
    Color.Transparent,
    "#40AAAAAA".toColor(),
    Color.Transparent
)

fun String.toColor() = Color(toColorInt())

private const val SHADER_DURATION = 1000
private const val ALPHA_TRANSPARENT = 0F
private const val NO_ALPHA = 1F

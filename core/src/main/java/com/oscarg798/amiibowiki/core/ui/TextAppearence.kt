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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.oscarg798.amiibowiki.core.rubik

private val H1FontSize = 28.sp
private val H2FontSize = 24.sp
private val H3FontSize = 16.sp
private val BODY_1_FONT_SIZE = 14.sp
private val BODY_2_FONT_SIZE = 12.sp

private val H1 =
    TextStyle(fontWeight = FontWeight.Medium, fontSize = H1FontSize, fontFamily = rubik)
private val H2 =
    TextStyle(fontWeight = FontWeight.Medium, fontSize = H2FontSize, fontFamily = rubik)
private val H3 =
    TextStyle(fontWeight = FontWeight.Medium, fontSize = H3FontSize, fontFamily = rubik)
private val Body1 =
    TextStyle(fontWeight = FontWeight.Normal, fontSize = BODY_1_FONT_SIZE, fontFamily = rubik)
private val Body2 =
    TextStyle(fontWeight = FontWeight.Normal, fontSize = BODY_2_FONT_SIZE, fontFamily = rubik)

val AmiiboWikiTextAppearence = Typography(h1 = H1, h2 = H2, h3 = H3, body1 = Body1, body2 = Body2)

@Composable
fun ThemeContainer(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AmiiboWikiTextAppearence,
        colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    ) { content() }
}

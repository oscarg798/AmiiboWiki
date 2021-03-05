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

package com.oscarg798.amiibowiki.core

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val alto = Color(0xffE0E0E0)
val white = Color(0xffffffff)
val whiteLilac = Color(0xffF8F9FC)
val royalBlue = Color(0Xff4363f6)
val malibu = Color(0Xff8390ff)
val mineShaft = Color(0Xff212121)
val mineShaft800 = Color(0xff424242)
val orange = Color(0xFFFF5722)
val shark = Color(0xff25272A)
val shark800 = Color(0xff1B1C1F)

val lightColors = lightColors(
    primary = white,
    primaryVariant = white,
    secondary = royalBlue,
    secondaryVariant = malibu,
    background = whiteLilac,
    surface = white,
    error = orange,
    onPrimary = mineShaft,
    onSecondary = white,
    onBackground = mineShaft,
    onSurface = mineShaft,
    onError = white
)

val darkColors = darkColors(
    primary = shark,
    primaryVariant = shark800,
    secondary = royalBlue,
    secondaryVariant = malibu,
    background = shark,
    surface = shark800,
    error = orange,
    onPrimary = white,
    onSecondary = alto,
    onBackground = white,
    onSurface = alto,
    onError = white
)


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

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

private val Alto = Color(0xffE0E0E0)
private val White = Color(0xffffffff)
private val WhiteLilac = Color(0xffF8F9FC)
private val RoyalBlue = Color(0Xff4363f6)
private val Malibu = Color(0Xff8390ff)
private val MineShaft = Color(0Xff212121)
private val MineShaft800 = Color(0xff424242)
private val Orange = Color(0xFFFF5722)
private val Shark = Color(0xff25272A)
private val Shark800 = Color(0xff1B1C1F)
val Overlay = Color(MineShaft800.red, MineShaft800.green, MineShaft800.blue, 0.9f)

val LightColors = lightColors(
    primary = White,
    primaryVariant = White,
    secondary = RoyalBlue,
    secondaryVariant = Malibu,
    background = WhiteLilac,
    surface = White,
    error = Orange,
    onPrimary = MineShaft,
    onSecondary = White,
    onBackground = MineShaft,
    onSurface = MineShaft,
    onError = White
)

val DarkColors = darkColors(
    primary = Shark,
    primaryVariant = Shark800,
    secondary = RoyalBlue,
    secondaryVariant = Malibu,
    background = Shark,
    surface = Shark800,
    error = Orange,
    onPrimary = White,
    onSecondary = Alto,
    onBackground = White,
    onSurface = Alto,
    onError = White
)

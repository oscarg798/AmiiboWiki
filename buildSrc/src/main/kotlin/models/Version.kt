/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package models

import javax.sound.midi.Patch
import kotlin.math.min

/**
 * Using semantic versioning
 * https://semver.org/lang/es/
 */
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val buildNumber: Int
) {

    override fun toString(): String = "$major.$minor.$patch($buildNumber)"
}

const val MAJOR_ATTRIBUTE_NAME = "major"
const val MINOR_ATTRIBUTE_NAME = "minor"
const val PATCH_ATTRIBUTE_NAME = "patch"
const val BUILD_NUMBER_ATTRIBUTE_NAME = "buildNumber"

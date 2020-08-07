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

sealed class BuildType(val task: String, val type: String) {

    object Debug : BuildType(DEBUG_BUILD_TAKS, DEBUG_TYPE)
    object Alpha : BuildType(ALPHA_BUILD_TASK, ALPHA_TYPE)
    object Release : BuildType(RELEASE_BUILD_TASK, RELEASE_TYPE)

    companion object {

        fun createFromParam(param: String) = when (param) {
            "debug" -> Debug
            "alpha" -> Alpha
            "release" -> Release
            else -> throw IllegalArgumentException("There is no BuildType associated with param $param")
        }
    }
}

private const val DEBUG_TYPE = "debug"
private const val ALPHA_TYPE = "alpha"
private const val RELEASE_TYPE = "release"
private const val DEBUG_BUILD_TAKS = "assembleDebug"
private const val ALPHA_BUILD_TASK = "assembleAlpha"
private const val RELEASE_BUILD_TASK = "assembleRelease"

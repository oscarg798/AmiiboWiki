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

package com.oscarg798.amiibowiki.logger.events

sealed class WidgetType(val name: String) : CharSequence {

    object Button : WidgetType(BUTTON_TYPE_NAME)
    object ImageView : WidgetType(IMAGE_VIEW_TYPE_NAME)

    override fun get(index: Int): Char = name[index]
    override val length: Int = name.length
    override fun toString(): String = name
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        name.subSequence(startIndex, endIndex)

    companion object {
        fun createFromString(type: String) = when (type) {
            BUTTON_TYPE_NAME -> Button
            IMAGE_VIEW_TYPE_NAME -> ImageView
            else -> throw IllegalArgumentException("$type does not exist")
        }
    }
}

const val BUTTON_TYPE_NAME = "BUTTON"
const val IMAGE_VIEW_TYPE_NAME = "IMAGE_VIEW"

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

package com.oscarg798.amiibowiki.loggerdecoratorprocessor.methodprocessors

import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import javax.annotation.processing.Messager
import javax.lang.model.element.Element

/**
 * A Method Processor is in charge of transform an Element of ExecutableElement into a MethodDecorator
 */
interface MethodProcessor {

    fun process(methodElement: Element, messager: Messager): MethodDecorator
}

internal const val PROPERTIES_POSITION_IN_PARAMETER = 0
internal const val ALLOWED_PARAMETERS_SIZE = 1
internal const val WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS =
    "Annotated methods can only have 1 paramter and it should be annotated withLogEventProperties"

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

package com.oscarg798.amiibowiki.loggerdecoratorprocessor

import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic

class ScreenShownMethodProcessor {

    fun process(methodElement: Element, messager: Messager): MethodDecorator {
        val methodName = methodElement.simpleName.toString()
        val screeName =
            (methodElement.getAnnotation(ScreenShown::class.java) as ScreenShown).name

        val methodDecoratorBuilder = MethodDecorator.Builder(screeName, methodName)

        val method = methodElement as ExecutableElement
        if (method.parameters.size > ALLOWED_PARAMETERS_SIZE) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS
            )
        }

        if (method.parameters.size == ALLOWED_PARAMETERS_SIZE) {
            val paramether = method.parameters[PROPERTIES_POSITION_IN_PARAMETER]
            methodDecoratorBuilder.withPropertiesName(paramether.simpleName.toString())

        }

        return methodDecoratorBuilder.build()
    }
}

private const val PROPERTIES_POSITION_IN_PARAMETER = 0
private const val ALLOWED_PARAMETERS_SIZE = 1
private const val WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS =
    "Annotated methods can only have 1 paramter"
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

import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
import com.oscarg798.amiibowiki.logger.events.WidgetType
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.WidgetClickedMethodDecorator
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic

class WidgetClickedMethodProcessor(nextProcessor: MethodProcessor? = null) :
    AbstractMethodProcessor(nextProcessor) {

    override fun processInternally(
        methodElement: ExecutableElement,
        messager: Messager
    ): MethodDecorator {
        val methodDecoratorBuilder = getMethodMethodDecoratorBuilder(methodElement)

        if (methodElement.parameters.size == ALLOWED_PARAMETERS_SIZE) {
            val paramether = methodElement.parameters[PROPERTIES_POSITION_IN_PARAMETER]
            methodDecoratorBuilder.withPropertiesName(paramether.simpleName.toString())
        }

        return methodDecoratorBuilder.build()
    }

    private fun getMethodMethodDecoratorBuilder(methodElement: ExecutableElement): WidgetClickedMethodDecorator.Builder {
        val widgetClickedAnnotation =
            methodElement.getAnnotation(WidgetClicked::class.java) as WidgetClicked

        return WidgetClickedMethodDecorator.Builder(
            widgetClickedAnnotation.widgetName,
            WidgetType.createFromString(widgetClickedAnnotation.widgetType),
            getMethodName(methodElement)
        )
    }

    override fun canMethodBeProcessed(methodElement: Element): Boolean =
        methodElement.getAnnotationsByType(WidgetClicked::class.java).isNotEmpty()
}


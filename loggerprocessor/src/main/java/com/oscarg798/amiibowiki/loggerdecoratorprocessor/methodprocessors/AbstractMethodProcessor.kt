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

import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
import com.oscarg798.amiibowiki.logger.annotations.LogSources
import com.oscarg798.amiibowiki.logger.sources.toLogSource
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.exceptions.IllegalMethodToBeProcesseedException
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.exceptions.NoMethodProcessorFoundForMethodException
import com.oscarg798.lomeno.event.LogSource
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic

abstract class AbstractMethodProcessor(private val nextProcessor: MethodProcessor? = null) :
    MethodProcessor {

    protected abstract fun processInternally(
        methodElement: ExecutableElement,
        messager: Messager
    ): MethodDecorator

    protected abstract fun canMethodBeProcessed(methodElement: Element): Boolean

    override fun process(methodElement: Element, messager: Messager): MethodDecorator {
        require(methodElement is ExecutableElement)
        assert(checkMethodParamterSize(methodElement, messager))

        return if (canMethodBeProcessed(methodElement)) {
            processInternally(methodElement, messager)
        } else {
            nextProcessor?.process(methodElement, messager)
                ?: throw NoMethodProcessorFoundForMethodException(getMethodName(methodElement))
        }
    }

    private fun checkMethodParamterSize(
        methodElement: ExecutableElement,
        messager: Messager
    ): Boolean {
        if (methodElement.parameters.size > ALLOWED_PARAMETERS_SIZE) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS
            )
            throw IllegalMethodToBeProcesseedException(getMethodName(methodElement))
        } else if (methodElement.parameters.size == ALLOWED_PARAMETERS_SIZE) {
            val logEventPropertiesCount =
                methodElement.parameters[PROPERTIES_POSITION_IN_PARAMETERS].getAnnotationsByType(
                    LogEventProperties::class.java
                ).count()

            if (logEventPropertiesCount != ALLOWED_PARAMETERS_SIZE) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS
                )
                throw IllegalMethodToBeProcesseedException(getMethodName(methodElement))
            }
        }

        return true
    }

    protected fun getSources(methodElement: ExecutableElement): Set<LogSource>? {
        if (methodElement.getAnnotationsByType(LogSources::class.java).isEmpty()) {
            return null
        }

        val logSources = methodElement.getAnnotation(LogSources::class.java) as LogSources

        return logSources.logSources.map {
            it.toLogSource()
        }.toSet()
    }

    protected fun getMethodName(methodElement: ExecutableElement) =
        methodElement.simpleName.toString()
}

private const val PROPERTIES_POSITION_IN_PARAMETERS = 0

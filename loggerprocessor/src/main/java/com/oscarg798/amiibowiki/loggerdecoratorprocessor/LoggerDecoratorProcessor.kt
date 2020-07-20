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

import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
import com.oscarg798.amiibowiki.logger.events.ScreenViewEvent
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.*
import com.oscarg798.lomeno.logger.Logger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Useful resource to understand how it works:
 * https://medium.com/@iammert/annotation-processing-dont-repeat-yourself-generate-your-code-8425e60c6657
 */
class LoggerDecoratorProcessor : AbstractProcessor() {

    private val decoratorBuilders = mutableSetOf<DecoratorBuilder>()
    private val screenShownMethodProcessor = ScreenShownMethodProcessor()

    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var processingEnvironment: ProcessingEnvironment


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(LoggerDecorator::class.java.canonicalName)
    }

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.processingEnvironment = processingEnvironment
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
    }

    override fun process(
        typeElementSet: MutableSet<out TypeElement>,
        rounEnvironment: RoundEnvironment
    ): Boolean {

        val elements = getDecorators(rounEnvironment)

        if (elements == null || elements.isEmpty()) {
            return true
        }

        val elementsWrongAnnotated = elements.filter {
            it.kind != ElementKind.INTERFACE
        }

        if (elementsWrongAnnotated.isNotEmpty()) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                LOGGER_ANNOTATION_WRONG_PLACE_ERROR_MESSAGE
            )
        }

        elements.map { interfaceElement ->
            val screenShownMethods = mutableSetOf<MethodDecorator>()
            interfaceElement.enclosedElements.mapTo(screenShownMethods) { methodElement ->
                screenShownMethodProcessor.process(methodElement, messager)
            }

            decoratorBuilders.add(
                DecoratorBuilder(
                    "${interfaceElement.simpleName}$LOGGER_DECORATOR_SUFFIX",
                    processingEnvironment.elementUtils.getPackageOf(interfaceElement).qualifiedName.toString(),
                    interfaceElement.simpleName.toString(),
                    screenShownMethods
                )
            )
        }

        writeImplementations()
        return true
    }

    private fun writeImplementations() {
        decoratorBuilders.forEach { decorator ->
            val constructor = FunSpec.constructorBuilder()
                .addParameter(LOGGER_CONTRUCTOR_PARAM, Logger::class, KModifier.PRIVATE)
                .build()

            val property = PropertySpec.builder(LOGGER_CONTRUCTOR_PARAM, Logger::class)
                .initializer(LOGGER_CONTRUCTOR_PARAM)
                .build()

            val classBuilder = TypeSpec.classBuilder(decorator.className)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
                .primaryConstructor(
                    constructor
                )
                .addProperty(property)
                .addSuperinterface(
                    ClassName(
                        decorator.classPackage,
                        decorator.interfaceName
                    )
                )
            /**
             * TODO: This makes sense to have it here now but will be a good idea
             * to have a class than can create the screen methods
             * or the widget clicks methods etc..
             */
            decorator.methodDecorators.forEach { methodDecorator ->
                classBuilder.addFunction(
                    getFunctionSpecFromMethodDecorator(methodDecorator)
                )
            }

            FileSpec.builder(decorator.classPackage, decorator.className)
                .addImport(
                    ScreenViewEvent::class.java.`package`.name,
                    ScreenViewEvent::class.java.simpleName
                )
                .addImport(Logger::class.java.`package`.name, Logger::class.java.simpleName)
                .addType(classBuilder.build())
                .build().writeTo(filer)
        }
    }

    private fun getFunctionSpecFromMethodDecorator(methodDecorator: MethodDecorator): FunSpec {
        val funBuilder = FunSpec.builder(methodDecorator.methodName)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)

        if (methodDecorator.propertiesName != null) {
            funBuilder.addParameter(
                methodDecorator.propertiesName,
                Map::class.parameterizedBy(String::class, String::class)
            )
        }

        funBuilder.returns(Unit::class.javaObjectType)
            .addStatement(getMethodStatementFromMethodDecorator(methodDecorator))

        return funBuilder.build()
    }

    private fun getMethodStatementFromMethodDecorator(methodDecorator: MethodDecorator): String {
        return if (methodDecorator.propertiesName == null) {
            "logger.log(ScreenViewEvent(\"${methodDecorator.screenShownName}\"))"
        } else {
            "logger.log(ScreenViewEvent(\"${methodDecorator.screenShownName}\", ${methodDecorator.propertiesName}))"
        }
    }

    private fun getDecorators(rounEnvironment: RoundEnvironment): Set<Element>? =
        rounEnvironment.getElementsAnnotatedWith(LoggerDecorator::class.java)
}

private const val PROPERTIES_POSITION_IN_PARAMETER = 0
private const val ALLOWED_PARAMETERS_SIZE = 1
private const val LOGGER_DECORATOR_SUFFIX = "Impl"
private const val LOGGER_CONTRUCTOR_PARAM = "logger"

private const val LOGGER_ANNOTATION_WRONG_PLACE_ERROR_MESSAGE =
    "Annotation should only be present in interfaces"
private const val WRONG_NUMBERS_OF_PARAMTERS_FOR_ANNOTATED_METHODS =
    "Annotated methods can only have 1 paramter"
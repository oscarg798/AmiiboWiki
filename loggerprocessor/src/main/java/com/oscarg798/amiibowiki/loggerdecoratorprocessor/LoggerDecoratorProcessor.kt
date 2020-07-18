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

import com.oscarg798.lomeno.logger.Logger
import com.squareup.kotlinpoet.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class LoggerDecoratorProcessor : AbstractProcessor() {

    private val decoratorBuilders = mutableSetOf<DecoratorBuilder>()
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

        elements.forEach {

            val screenShownMethods = mutableMapOf<MethodName, ScreenShownName>()
            it.enclosedElements.map { element ->
                Pair(
                    element.simpleName.toString(),
                    (element.getAnnotation(ScreenName::class.java) as ScreenName).name
                )
            }.forEach { shownMethod ->
                screenShownMethods[shownMethod.first] = shownMethod.second
            }


            decoratorBuilders.add(
                DecoratorBuilder(
                    "${it.simpleName}$LOGGER_DECORATOR_SUFFIX",
                    processingEnvironment.elementUtils.getPackageOf(it).qualifiedName.toString(),
                    it.simpleName.toString(),
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

            decorator.screenShownMethods.forEach { screenShownMethod ->
                classBuilder.addFunction(
                    FunSpec.builder(screenShownMethod.key)
                        .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                        .returns(Unit::class.javaObjectType)
                        .addStatement("logger.log(ScreenViewEvent(\"${screenShownMethod.value}\"))")
                        .build()
                )
            }

            FileSpec.builder(decorator.classPackage, decorator.className)
                .addImport(SCREEN_VIEW_EVENT_PACKAGE_NAME, SCREEN_VIEW_EVENT_CLASS_NAME)
                .addImport(Logger::class.java.`package`.name, Logger::class.simpleName!!)
                .addType(classBuilder.build())
                .build().writeTo(filer)

        }
    }

    private fun getDecorators(rounEnvironment: RoundEnvironment): Set<Element>? =
        rounEnvironment.getElementsAnnotatedWith(LoggerDecorator::class.java)

}

private const val LOGGER_ANNOTATION_WRONG_PLACE_ERROR_MESSAGE =
    "Annotation should only be present in interfaces"
private const val LOGGER_DECORATOR_SUFFIX = "Impl"
private const val SCREEN_VIEW_EVENT_CLASS_NAME = "ScreenViewEvent"
private const val SCREEN_VIEW_EVENT_PACKAGE_NAME = "com.oscarg798.amiibowiki.logger"
private const val LOGGER_CONTRUCTOR_PARAM = "logger"
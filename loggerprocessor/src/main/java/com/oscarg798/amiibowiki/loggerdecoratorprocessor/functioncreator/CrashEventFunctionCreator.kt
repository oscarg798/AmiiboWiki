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

package com.oscarg798.amiibowiki.loggerdecoratorprocessor.functioncreator

import com.oscarg798.amiibowiki.logger.events.CrashEvent
import com.oscarg798.amiibowiki.logger.events.RegularLogEvent
import com.oscarg798.amiibowiki.logger.sources.CrashlytcisSource
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.CrashEventDecorator
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.RegularEventDecorator
import com.oscarg798.lomeno.event.LogEvent
import com.oscarg798.lomeno.event.LogSource
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class CrashEventFunctionCreator : AbstractFunctionCreator<CrashEventDecorator>() {

    override fun getMethodStatement(methodDecorator: CrashEventDecorator): String {
        return """
                logger.log(CrashEvent(exception=${methodDecorator.propertiesName}${
            getSourcesStatement(
                methodDecorator.sources
            )
        })) """.trimIndent()

    }

    override fun isApplicable(methodDecorator: MethodDecorator): Boolean =
        methodDecorator is CrashEventDecorator

    override fun addEventImportToFileSpecBuilder(fileSpecBuilder: FileSpec.Builder) {
        fileSpecBuilder.addImport(
            CrashEvent::class.java.`package`.name,
            CrashEvent::class.java.simpleName
        )
    }

    protected override fun addProperties(
        funSpecBuilder: FunSpec.Builder,
        methodDecorator: CrashEventDecorator
    ): FunSpec.Builder = funSpecBuilder.addParameter(
        methodDecorator.propertiesName,
        Exception::class
    )

}

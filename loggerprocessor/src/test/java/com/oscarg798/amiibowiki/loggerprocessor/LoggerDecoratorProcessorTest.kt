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

package com.oscarg798.amiibowiki.loggerprocessor


import com.oscarg798.amiibowiki.loggerdecoratorprocessor.LoggerDecoratorProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Assert
import org.junit.Test

class LoggerDecoratorProcessorTest {

    @Test
    fun `given a valid class with screen shown method to be mapped when processor runs then it should generate the implementation class`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLogger.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLogger {

            @ScreenShown("SCREEN_NAME")
            fun trackScreenViewed()
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)
        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "trackScreenViewed"
        })
    }

    @Test
    fun `given a valid class with screen shown method and properties to be mapped when processor runs then it should generate the implementation class`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithProperties.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithProperties {

            @ScreenShown("SCREEN_NAME")
            fun trackScreenViewed(@LogEventProperties someProper: Map<String, String>)
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)
        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerWithPropertiesImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "trackScreenViewed"
        })

        Assert.assertEquals(1, generatedClass.declaredMethods[0].parameters.size)
    }

    @Test
    fun `given a valid class with screen shown method and a param not annotated properly when processor runs then it should not compile`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithProperties.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithProperties {

            @ScreenShown("SCREEN_NAME")
            fun trackScreenViewed(someProper: Map<String, String>)
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    }

    @Test
    fun `given a valid class with widget click and IMAGE_VIEW as widget type method to be mapped when processor runs then it should generate the implementation class`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithWidgetClick.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithWidgetClick {

            @WidgetClicked("1", "IMAGE_VIEW")
            fun trackWidgetClicked()
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)
        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerWithWidgetClickImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "trackWidgetClicked"
        })
    }

    @Test
    fun `given a valid class with widget click and BUTTON as widget type method to be mapped when processor runs then it should generate the implementation class`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithWidgetClick.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithWidgetClick {

            @WidgetClicked("1", "BUTTON")
            fun trackWidgetClicked()
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)
        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerWithWidgetClickImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "trackWidgetClicked"
        })
    }

    @Test
    fun `given a valid class with widget click and a supported widget type method and properties to be mapped when processor runs then it should generate the implementation class`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithWidgetClick.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithWidgetClick {

            @WidgetClicked("1", "BUTTON")
            fun trackWidgetClicked(@LogEventProperties myProperties: Map<String, String>)
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)
        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerWithWidgetClickImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "trackWidgetClicked"
        })
        Assert.assertEquals(1, generatedClass.declaredMethods[0].parameters.size)
    }

    @Test
    fun `given a valid class with widget click and not support ype as widget type method to be mapped when processor runs it should not compile`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithWidgetClick.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
        import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME


        @LoggerDecorator
        interface TestLoggerWithWidgetClick {

            @WidgetClicked("1", "2")
            fun trackWidgetClicked()
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    }

    @Test
    fun `given a valid class with widget click and screen show events when processor runs it should compile properly`() {
        val kotlinSource = SourceFile.kotlin(
            "TestLoggerWithWidgetClick.kt", """
        import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
        import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
        import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
        import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked


        @LoggerDecorator
        interface TestLogger {

            @ScreenShown("1")
            fun screenViewed()

            @WidgetClicked("2", "IMAGE_VIEW")
            fun widgetClicked(@LogEventProperties properties: Map<String, String>)
        }
    """
        )

        fun compileSource(source: SourceFile): KotlinCompilation.Result =
            KotlinCompilation().apply {
                sources = listOf(source)
                annotationProcessors = listOf(LoggerDecoratorProcessor())
                inheritClassPath = true
                verbose = false
            }.compile()

        val compilationResult = compileSource(kotlinSource)

        Assert.assertEquals(compilationResult.exitCode, KotlinCompilation.ExitCode.OK)

        val generatedClass = compilationResult.classLoader.loadClass("TestLoggerImpl")

        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "screenViewed"
        })
        Assert.assertEquals(1, generatedClass.declaredMethods.count {
            it.name == "widgetClicked"
        })
        Assert.assertEquals(0, generatedClass.declaredMethods[0].parameters.size)
        Assert.assertEquals(1, generatedClass.declaredMethods[1].parameters.size)
    }
}
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

package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import showMessage
import runCommand
import models.BuildType
import models.MAJOR_ATTRIBUTE_NAME
import org.apache.tools.ant.taskdefs.ExecTask
import java.io.File
import java.util.Properties

open class BuildGenerator : DefaultTask() {

    private var buildType: BuildType = BuildType.Debug

    @Option(option = "buildType", description = "build type")
    fun setBuildType(buildType: String) {
        this.buildType = BuildType.createFromParam(buildType)
    }

    @Input
    fun getBuildType() = buildType

    @TaskAction
    fun run() {
        if (buildType is BuildType.Release) {
            throw getIllegalBuildTypeException()
        }

        showMessage("Creating build")
        val assembleResult = runCommand("./gradlew", listOf(buildType.task))
        showMessage("Build generation result= $assembleResult")
    }

    private fun getIllegalBuildTypeException() =
        IllegalArgumentException("${buildType.type} is not supported to firebase distribution")
}



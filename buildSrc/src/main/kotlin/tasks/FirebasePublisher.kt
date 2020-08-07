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

import models.BuildType
import org.apache.tools.ant.taskdefs.ExecTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.util.Properties
import showMessage
import runCommand

open class FirebasePublisher : DefaultTask() {

    private val properties = Properties()
    private val propertiesFile: File = File(PROPERTIES_FILE)

    private lateinit var distributionGroup: String
    private var buildType: BuildType = BuildType.Debug

    init {
        properties.load(propertiesFile.inputStream())
    }

    @Option(
        option = "distributionGroup",
        description = "distribution group to use with firebase type"
    )
    fun setDistributionGroup(distributionGroup: String) {
        this.distributionGroup = distributionGroup
    }

    @Option(option = "buildType", description = "build type")
    fun setBuildType(buildType: String) {
        this.buildType = BuildType.createFromParam(buildType)
    }

    @Input
    fun getBuildType() = buildType

    @Input
    fun getDistributionGroup() = distributionGroup

    @TaskAction
    fun run() {
        if (buildType is BuildType.Release) {
            throw getIllegalBuildTypeException()
        }

        val result = runCommand("firebase", getFirebaseDistributionCommandParams())

        if (result.contains("Error")) {
            throw RuntimeException(result)
        }

        showMessage(result)
    }

    private fun getFirebaseDistributionCommandParams() = listOf(
        "appdistribution:distribute",
        getBuildPath(),
        "--app",
        getFirebaseProjectId(),
        "--token",
        getFirebaseToken(),
        "--groups",
        distributionGroup
    )

    private fun getBuildPath() = when (buildType) {
        is BuildType.Alpha -> "app/build/outputs/apk/alpha/app-alpha.apk"
        is BuildType.Debug -> "app/build/outputs/apk/debug/app-debug.apk"
        else -> throw getIllegalBuildTypeException()
    }

    private fun getFirebaseToken(): String = properties.getProperty(FIREBASE_TOKEN_PROPERTY_KEY)
    private fun getFirebaseProjectId(): String = properties.getProperty(FIREBASE_ID_PROPERTY_KEY)

    private fun getIllegalBuildTypeException() =
        IllegalArgumentException("${buildType.type} is not supported to firebase distribution")
}

private const val FIREBASE_TOKEN_PROPERTY_KEY = "firebaseToken"
private const val FIREBASE_ID_PROPERTY_KEY = "firebaseProjectId"
private const val PROPERTIES_FILE = "local.properties"

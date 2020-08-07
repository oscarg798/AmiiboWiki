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
import runCommand
import java.io.File
import java.util.Properties
import models.Version
import models.MAJOR_ATTRIBUTE_NAME
import models.MINOR_ATTRIBUTE_NAME
import models.PATCH_ATTRIBUTE_NAME
import models.BUILD_NUMBER_ATTRIBUTE_NAME
import showMessage

open class VersionUpdate : DefaultTask() {

    private var versionUpdateParam: VersionUpdateParam = VersionUpdateParam.IncreaseBuildNumber
    private val properties = Properties()
    private val propertiesFile: File = File(PROPERTIES_FILE)

    init {
        properties.load(propertiesFile.inputStream())
    }

    @Option(
        option = "versionUpdateParam",
        description = "which component of the version will be updated"
    )
    fun setVersionUpdateParam(versionUpdateParamAsString: String) {
        this.versionUpdateParam = VersionUpdateParam.getFromParam(versionUpdateParamAsString)
    }

    @Input
    fun getVersionUpdateParam() = versionUpdateParam

    @TaskAction
    fun run() {
        val currentVersion = getCurrentVersion()
        showMessage("I will update the field ${versionUpdateParam.name} in the current version $currentVersion")

        val newVersion = getVersionToUpdate(currentVersion)

        writeNewVersion(newVersion)
        showMessage("version $currentVersion updated to $newVersion")
    }

    private fun getVersionToUpdate(currentVersion: Version): Version {
        return when (versionUpdateParam) {
            is VersionUpdateParam.IncreaseMajor -> currentVersion.copy(major = currentVersion.major + VERSION_INCREASE_ADDEND)
            is VersionUpdateParam.IncreaserMinor -> currentVersion.copy(minor = currentVersion.minor + VERSION_INCREASE_ADDEND)
            is VersionUpdateParam.IncreasePatch -> currentVersion.copy(patch = currentVersion.patch + VERSION_INCREASE_ADDEND)
            is VersionUpdateParam.IncreaseBuildNumber -> currentVersion.copy(buildNumber = currentVersion.buildNumber + VERSION_INCREASE_ADDEND)
        }
    }

    private fun writeNewVersion(version: Version) {
        properties.setProperty(MAJOR_ATTRIBUTE_NAME, version.major.toString())
        properties.setProperty(MINOR_ATTRIBUTE_NAME, version.minor.toString())
        properties.setProperty(PATCH_ATTRIBUTE_NAME, version.patch.toString())
        properties.setProperty(BUILD_NUMBER_ATTRIBUTE_NAME, version.buildNumber.toString())
        properties.store(propertiesFile.writer(), null)
    }

    private fun getCurrentVersion(): Version {
        return Version(
            properties.getProperty(MAJOR_ATTRIBUTE_NAME).toInt(),
            properties.getProperty(MINOR_ATTRIBUTE_NAME).toInt(),
            properties.getProperty(PATCH_ATTRIBUTE_NAME).toInt(),
            properties.getProperty(BUILD_NUMBER_ATTRIBUTE_NAME).toInt()
        )
    }

}

sealed class VersionUpdateParam(val name: String) {

    object IncreaseMajor : VersionUpdateParam(UPDATE_MAJOR_PARAM)
    object IncreaserMinor : VersionUpdateParam(UPDATE_MINOR_PARAM)
    object IncreasePatch : VersionUpdateParam(UPDATE_PATCH_PARAM)
    object IncreaseBuildNumber : VersionUpdateParam(UPDATE_BUILD_NUMBER_PARAM)

    companion object {
        fun getFromParam(param: String) = when (param) {
            UPDATE_MAJOR_PARAM -> IncreaseMajor
            UPDATE_MINOR_PARAM -> IncreaserMinor
            UPDATE_PATCH_PARAM -> IncreasePatch
            UPDATE_BUILD_NUMBER_PARAM -> IncreaseBuildNumber
            else -> throw IllegalArgumentException("Version param not supported $param")
        }
    }

}

private const val VERSION_INCREASE_ADDEND = 1
private const val UPDATE_MAJOR_PARAM = "major"
private const val UPDATE_MINOR_PARAM = "minor"
private const val UPDATE_PATCH_PARAM = "patch"
private const val UPDATE_BUILD_NUMBER_PARAM = "buildNumber"
private const val PROPERTIES_FILE = "gradle.properties"

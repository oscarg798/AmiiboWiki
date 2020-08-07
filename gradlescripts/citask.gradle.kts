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
tasks.register<tasks.VersionUpdate>("updateVersion") {
    group = "CI Tasks"
    description =
        "It will update the app version using the param [versionUpdateParam] that should be major, minor, patch o buildNumber"
}

tasks.register<tasks.BuildGenerator>("createBuild") {
    group = "CI Tasks"
    description =
        "creates an apk based on param [buildType] should debug or alpha"
}

tasks.register<tasks.FirebasePublisher>("firebaseDistribution") {
    group = "CI Tasks"
    description =
        "Build the app and upload it to firebase distribution, param [buildType] should debug or alpha"

    dependsOn("clean")

    val cleanTaks = getTasksByName("clean", false).first()
    val unitTestTask = getTasksByName("unitTests", false).first()
    val createBuildTask = getTasksByName("createBuild", false).first()
    val updateVersionTask = getTasksByName("updateVersion", false).first()

    unitTestTask.dependsOn(cleanTaks)
    createBuildTask.dependsOn(unitTestTask)
    createBuildTask.dependsOn(unitTestTask)

    this.dependsOn(createBuildTask)
    this.finalizedBy("updateVersion")
}


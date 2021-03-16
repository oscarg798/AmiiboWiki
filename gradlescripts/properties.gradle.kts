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
import java.util.Properties
import java.io.FileInputStream

val properties = Properties()
properties.load(FileInputStream("local.properties"))

private val major: String by project
private val minor: String by project
private val patch: String by project
private val buildNumber: String by project

extra.apply {
    set("mixPanelApiKey", getVariable("mixpanelToken"))
    set("appVersionName", "${major}.${minor}.${patch}")
    set("appMinSdkVersion", 23)
    set("appTargetSdkVersion", 30)
    set("appCompileSdkVersion", 30)
    set("appVersionCode", getReleaseVersionCode())
    set("appBuildToolsVersion", "30.0.2")
    set("googleAPIKey", getVariable("googleApiKey"))
    set("gameAPIAuthUrl", getVariable("gameAuthUrl"))
    set("gameAPIClientId", getVariable("gameAPIClientId"))
    set("googleAccountServiceFile", getVariable("googleAccountServiceFile"))
    set("firebaseAuthToken", getVariable("firebaseToken"))
}

fun getReleaseVersionCode(): Int {
    return "${major}".toInt() * 10000 + "${minor}".toInt() * 1000 + "${patch}".toInt() * 100 + "${buildNumber}".toInt()
}

fun getVariable(key: String): String {
    return System.getenv(key) ?: properties.getProperty(key)
}


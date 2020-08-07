import org.gradle.api.DefaultTask
import java.util.concurrent.TimeUnit

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

fun DefaultTask.runCommand(
    command: String,
    arguments: List<String> = listOf(),
    commandTimeOutInMinutes: Long = COMMAND_TIMEOUT
): String {
    val splitCommand = listOf(command) + arguments
    val process = processBuilder
        .command(splitCommand)
        .start()

    process.waitFor(commandTimeOutInMinutes, TimeUnit.MINUTES)
    return process.retrieveOutput()
}

fun DefaultTask.showMessage(message: String){
    project.logger.lifecycle(message)
}


private val processBuilder = ProcessBuilder(listOf())
    .directory(null)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)


private fun Process.retrieveOutput(): String {
    val outputText = inputStream.bufferedReader().use(java.io.BufferedReader::readText)
    val exitCode = exitValue()
    if (exitCode != 0) {
        val errorText = errorStream.bufferedReader().use(java.io.BufferedReader::readText)
        if (errorText.isNotEmpty()) {
            throw ShellRunException(exitCode, errorText.trim())
        }
    }
    return outputText.trim()
}

private const val COMMAND_TIMEOUT = 10L

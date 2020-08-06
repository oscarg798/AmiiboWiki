open class UnitTests : DefaultTask() {

    private val COMMAND_TIMEOUT = 10L

    @TaskAction
    fun run() {
        val result = command("./gradlew", listOf("testDebugUnitTest"))
        print(result)
    }

    private val processBuilder = ProcessBuilder(listOf())
        .directory(null)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)

    private fun command(
        command: String,
        arguments: List<String> = listOf()
    ): String {
        val splitCommand = listOf(command) + arguments
        val process = processBuilder
            .command(splitCommand)
            .start()
        process.waitFor(COMMAND_TIMEOUT, TimeUnit.MINUTES)
        return process.retrieveOutput()
    }

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
}

class ShellRunException(val exitCode: Int, override val message: String? = null) :
    kotlin.Exception(message)

tasks.register<UnitTests>("unitTests") {
    group = "Unit Tests"
    description = "It runs the unit tests from app module"
}

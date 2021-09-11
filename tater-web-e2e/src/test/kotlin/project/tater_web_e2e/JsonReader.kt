package project.tater_web_e2e

object JsonReader {

    fun read(filePath: String) = JsonReader::class.java.getResource(filePath).readText()
}
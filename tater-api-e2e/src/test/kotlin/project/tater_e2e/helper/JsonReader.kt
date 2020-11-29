package project.tater_e2e.helper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JsonReader {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

        fun fromFilePath(fileName: String): Map<Any, Any>? {
            val reader = Companion::class.java.getResource(fileName).openStream().bufferedReader()
            return mapper.readValue(reader.readText(), object : TypeReference<Map<Any, Any>>() {})
        }
        fun fromRawString(rawString: String): Map<Any, Any>? {
            return mapper.readValue(rawString, object : TypeReference<Map<Any, Any>>() {})
        }

        fun read(fileName: String): String {
            return Companion::class.java.getResource(fileName).readText()
        }
    }
}
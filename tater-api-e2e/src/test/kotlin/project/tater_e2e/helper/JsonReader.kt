package project.tater_e2e.helper

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.squareup.okhttp.Response

class JsonReader {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

        fun isJson(response: Response): Boolean {
            return try {
                readRawString(response.bodyString())
                true
            } catch (e: JsonProcessingException) {
                false
            }
        }

        fun parseResponseBody(response: Response): AssertableJson {
            return AssertableJson(readRawString(response.bodyString()) ?: emptyMap())
        }

        fun readFile(fileName: String): String {
            return Companion::class.java.getResource(fileName).readText()
        }

        private fun readRawString(rawString: String): Map<Any, Any>? {
            return try {
                mapper.readValue(rawString, object : TypeReference<Map<Any, Any>>() {})
            } catch (e: JsonProcessingException) {
                null
            }
        }

        private fun Response.bodyString() = this.body().string()
    }
}

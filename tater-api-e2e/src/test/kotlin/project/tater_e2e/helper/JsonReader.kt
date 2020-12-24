package project.tater_e2e.helper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.squareup.okhttp.Response

class JsonReader {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

        fun fromResponseBody(response: Response): AssertableJson {
            return fromRawString(response.body().string())
        }

        fun read(fileName: String): String {
            return Companion::class.java.getResource(fileName).readText()
        }

        private fun fromRawString(rawString: String): AssertableJson {
            return try {
                AssertableJson(mapper.readValue(rawString, object : TypeReference<Map<Any, Any>>() {}))
            } catch (e: MismatchedInputException) {
                AssertableJson(mapOf())
            }
        }
    }
}

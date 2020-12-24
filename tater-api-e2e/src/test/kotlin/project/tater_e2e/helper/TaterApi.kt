package project.tater_e2e.helper

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class TaterApi {
    companion object {
        private val client = OkHttpClient()
    }

    fun getV1Watched(userId: String): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/watched", userId)
    }

    fun getV1WatchedWithoutUserId(): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/watched")
    }

    fun getV1Recommended(userId: String): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/recommended", userId)
    }

    fun getV1RecommendedWithoutUserId(): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/recommended")
    }

    private fun getRequestWith(path: String, userId: String? = null): Response {
        val builder = if (userId != null ) {
            Request.Builder().url(path).header("tater-user-id", userId)
        } else {
            Request.Builder().url(path)
        }
        return builder
                .build()
                .let {
                    client.newCall(it).execute()!!
                }
    }
}

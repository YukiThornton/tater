package project.tater_e2e.helper

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class TaterApi {
    companion object {
        private val client = OkHttpClient()
    }

    fun getV1WatchedForUser(userId: String): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/watched", userId)
    }

    fun getV1WatchedWithoutUserId(): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/watched")
    }

    fun getV1TopRatedForUser(userId: String): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/top-rated", userId)
    }

    fun getV1TopRatedWithoutUserId(): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/top-rated")
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

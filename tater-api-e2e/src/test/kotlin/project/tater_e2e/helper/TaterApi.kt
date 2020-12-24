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
        val request = Request.Builder()
                .url("${Configurations.taterApiEndpoint}/v1/watched")
                .build()

        return client.newCall(request).execute()!!
    }

    fun getV1Recommended(userId: String): Response {
        return getRequestWith("${Configurations.taterApiEndpoint}/v1/recommended", userId)
    }

    private fun getRequestWith(path: String, userId: String): Response {
        return Request.Builder()
                .url(path)
                .header("tater-user-id", userId)
                .build()
                .let {
                    client.newCall(it).execute()!!
                }
    }
}

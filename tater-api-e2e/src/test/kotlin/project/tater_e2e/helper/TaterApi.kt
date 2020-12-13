package project.tater_e2e.helper

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class TaterApi {
    companion object {
        private val client = OkHttpClient()
    }

    fun getV1Watched(userId: String): Response {
        val request = Request.Builder()
            .url("${Configurations.taterApiEndpoint}/v1/watched")
            .header("tater-user-id", userId)
            .header("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute()!!
    }

    fun getV1WatchedWithoutUserId(): Response {
        val request = Request.Builder()
                .url("${Configurations.taterApiEndpoint}/v1/watched")
                .header("Content-Type", "application/json")
                .build()

        return client.newCall(request).execute()!!
    }
}
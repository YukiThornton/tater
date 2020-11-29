package project.tater_e2e

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import project.tater_e2e.helper.Configurations
import project.tater_e2e.helper.JsonReader
import project.tater_e2e.helper.MovieApiMock
import project.tater_e2e.helper.TaterDb

@DisplayName("GET /v1/watched")
class WatchedTest {

    companion object {
        private val client = OkHttpClient()
        private val movieApiMock = MovieApiMock()
        private val taterDb = TaterDb()

        @BeforeAll
        @JvmStatic
        fun setup() {
            movieApiMock.reset()
            taterDb.cleanInsert("default")
        }
    }

    @BeforeEach
    fun resetMockRequests() {
        movieApiMock.resetAllRequests()
    }

    @Test
    fun `001_視聴履歴のある映画のタイトルを全て返す`() {
        val expectedJsonPath = "/project/tater_e2e/tater-api/responses/get-v1-watched/001.json"
        val expectedJson = JsonReader.fromFilePath(expectedJsonPath)

        movieApiMock.stubGetMovie("1001")
        movieApiMock.stubGetMovie("1002")
        movieApiMock.stubGetMovie("1003")

        val request = Request.Builder()
            .url("${Configurations.taterApiEndpoint}/v1/watched")
            .header("tater-user-id", "userId1")
            .header("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()

        response.code() shouldBeEqualTo 200
        JsonReader.fromRawString(response.body().string()) shouldBeEqualTo  expectedJson

        movieApiMock.verifyGetMovie("1001")
        movieApiMock.verifyGetMovie("1002")
        movieApiMock.verifyGetMovie("1003")
    }
}
package project.tater_e2e

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import project.tater_e2e.helper.*

@DisplayName("GET /v1/watched")
class WatchedTest {

    companion object {
        private val taterApi = TaterApi()
        private val movieApi = MovieApiMock()
        private val taterDb = TaterDb()

        @BeforeAll
        @JvmStatic
        fun setup() {
            movieApi.clearAll()
            taterDb.loadData("default")
        }
    }

    @BeforeEach
    fun resetMockRequests() {
        movieApi.clearAllRequests()
    }

    @Test
    fun `001_視聴履歴のある映画のタイトルを全て返す`() {
        val expectedJsonPath = "/project/tater_e2e/tater-api/responses/get-v1-watched/001.json"
        val expectedJson = JsonReader.fromFilePath(expectedJsonPath)

        movieApi.returnsMovieDetailsWhenMovieIdIs("1001")
        movieApi.returnsMovieDetailsWhenMovieIdIs("1002")
        movieApi.returnsMovieDetailsWhenMovieIdIs("1003")

        val response = taterApi.getV1Watched("1")

        response.code() shouldBeEqualTo 200
        JsonReader.fromRawString(response.body().string()) shouldBeEqualTo  expectedJson

        movieApi.receivedARequestForMovieDetailsOf("1001")
        movieApi.receivedARequestForMovieDetailsOf("1002")
        movieApi.receivedARequestForMovieDetailsOf("1003")
    }

    // add test: no header
}
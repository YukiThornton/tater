package project.tater_e2e

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import project.tater_e2e.helper.*

@DisplayName("GET /v1/watched 指定したユーザに紐づく視聴履歴のある映画のタイトルを全て返す")
class WatchedTest {

    companion object {
        private val taterApi = TaterApi()
        private val movieApi = MovieApiMock()
        private val taterDb = TaterDb()
        private const val responseJsonRoot = "/project/tater_e2e/tater-api/responses/get-v1-watched"

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
    fun `001_指定したユーザに紐づく視聴履歴のある映画のタイトルを全て返す`() {
        val expectedResponse = JsonReader.fromFilePath("$responseJsonRoot/001.json")

        movieApi.returnsMovieDetailsWhenMovieIdIs("497")
        movieApi.returnsMovieDetailsWhenMovieIdIs("680")
        movieApi.returnsMovieDetailsWhenMovieIdIs("13")

        val response = taterApi.getV1Watched("1")

        response.code() shouldBeEqualTo 200
        JsonReader.fromRawString(response.body().string()) shouldBeEqualTo expectedResponse

        movieApi.receivedARequestForMovieDetailsOf("497")
        movieApi.receivedARequestForMovieDetailsOf("680")
        movieApi.receivedARequestForMovieDetailsOf("13")
    }

    @Test
    fun `002_指定したユーザに紐づく視聴履歴がないときは空の結果を返す`() {
        val expectedResponse = JsonReader.fromFilePath("$responseJsonRoot/002.json")

        val response = taterApi.getV1Watched("9999")

        response.code() shouldBeEqualTo 200
        JsonReader.fromRawString(response.body().string()) shouldBeEqualTo expectedResponse
    }

    @Test
    fun `003_一部の映画情報が存在せず取得に失敗した場合は取得に成功したものだけを返す`() {
        val expectedResponse = JsonReader.fromFilePath("$responseJsonRoot/003.json")

        movieApi.returnsMovieDetailsWhenMovieIdIs("497")
        movieApi.failsWithNotFoundForMovieDetailsWhenMovieIdIs("680")
        movieApi.returnsMovieDetailsWhenMovieIdIs("13")

        val response = taterApi.getV1Watched("1")

        response.code() shouldBeEqualTo 200
        JsonReader.fromRawString(response.body().string()) shouldBeEqualTo expectedResponse

        movieApi.receivedARequestForMovieDetailsOf("497")
        movieApi.receivedARequestForMovieDetailsOf("680")
        movieApi.receivedARequestForMovieDetailsOf("13")
    }

    @Test
    fun `501_ユーザを指定しないと400エラーを返す`() {
        val response = taterApi.getV1WatchedWithoutUserId()

        response.code() shouldBeEqualTo 400
    }

    @Test
    fun `502_映画情報の取得に失敗した場合は500エラーを返す`() {
        movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("497")
        movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("680")
        movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("13")

        val response = taterApi.getV1Watched("1")

        response.code() shouldBeEqualTo 500
    }
}
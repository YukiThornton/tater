package project.tater_e2e

import com.squareup.okhttp.Response
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.*
import project.tater_e2e.helper.*

@DisplayName("GET /v1/watched 指定したユーザに紐づく視聴履歴のある映画のタイトルを全て返す")
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

    @Nested
    @DisplayName("001_指定したユーザに紐づく視聴履歴が複数あるとき")
    inner class WhenViewingHistoriesExistForUser {

        @BeforeEach
        fun setup() {
            movieApi.returnsMovieDetailsWhenMovieIdIs("497")
            movieApi.returnsMovieDetailsWhenMovieIdIs("680")
            movieApi.returnsMovieDetailsWhenMovieIdIs("13")
        }

        @Test
        fun `MovieAPIに対して視聴履歴のある映画の情報を問い合わせていること`() {
            taterApi.getV1WatchedForUser("1")

            movieApi.receivedARequestForMovieDetailsOf("497")
            movieApi.receivedARequestForMovieDetailsOf("680")
            movieApi.receivedARequestForMovieDetailsOf("13")
        }

        @Test
        fun `ステータスコード200 & JSON形式のレスポンスが返ること`() {
            val response = taterApi.getV1WatchedForUser("1")

            response.code() shouldBeEqualTo 200
            response.header("Content-Type") shouldContain "application/json"
            JsonReader.isJson(response) shouldBeEqualTo true
        }

        @Test
        fun `視聴履歴のある映画全てを返すこと`() {
            val response = taterApi.getV1WatchedForUser("1")

            val responseJson = JsonReader.parseResponseBody(response)
            responseJson.shouldHaveExpectedAmountOf("movies", 3)
        }

        @Nested
        @DisplayName("それぞれの映画の情報として次の項目を返すこと")
        inner class MovieItems {
            @Test
            fun `ID`() {
                val response = taterApi.getV1WatchedForUser("1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.id", "497")
                responseJson.shouldHaveValueOf("movies.1.id", "680")
                responseJson.shouldHaveValueOf("movies.2.id", "13")
            }

            @Test
            fun `タイトル`() {
                val response = taterApi.getV1WatchedForUser("1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.title", "The Green Mile")
                responseJson.shouldHaveValueOf("movies.1.title", "Pulp Fiction")
                responseJson.shouldHaveValueOf("movies.2.title", "Forrest Gump")
            }
        }
    }

    @Nested
    @DisplayName("002_指定したユーザに紐づく視聴履歴がないとき")
    inner class WhenViewingHistoriesDoNotExistForUser {

        @Test
        fun `MovieAPIに対していかなる問い合わせもしていないこと`() {
            taterApi.getV1WatchedForUser("9999")

            movieApi.didNotReceiveAnyRequests()
        }

        @Test
        fun `ステータスコード200 & JSON形式のレスポンスが返ること`() {
            val response = taterApi.getV1WatchedForUser("9999")

            response.code() shouldBeEqualTo 200
            response.header("Content-Type") shouldContain "application/json"
            JsonReader.isJson(response) shouldBeEqualTo true
        }

        @Test
        fun `一つも映画を返さないこと`() {
            val response = taterApi.getV1WatchedForUser("9999")

            val responseJson = JsonReader.parseResponseBody(response)
            responseJson.shouldHaveExpectedAmountOf("movies", 0)
        }
    }

    @Nested
    @DisplayName("003_一部の映画情報が存在せず取得に失敗した場合は")
    inner class WhenFailsToFetchNonExistentMovies {
        @BeforeEach
        fun setup() {
            movieApi.returnsMovieDetailsWhenMovieIdIs("497")
            movieApi.failsWithNotFoundForMovieDetailsWhenMovieIdIs("680")
            movieApi.returnsMovieDetailsWhenMovieIdIs("13")
        }

        @Test
        fun `処理を中断することなくMovieAPIに対して視聴履歴のある全ての映画の情報を問い合わせていること`() {
            taterApi.getV1WatchedForUser("1")

            movieApi.receivedARequestForMovieDetailsOf("497")
            movieApi.receivedARequestForMovieDetailsOf("680")
            movieApi.receivedARequestForMovieDetailsOf("13")
        }

        @Test
        fun `ステータスコード200 & JSON形式のレスポンスが返ること`() {
            val response = taterApi.getV1WatchedForUser("1")

            response.code() shouldBeEqualTo 200
            response.header("Content-Type") shouldContain "application/json"
            JsonReader.isJson(response) shouldBeEqualTo true
        }

        @Test
        fun `取得に成功したものだけを返すこと`() {
            val response = taterApi.getV1WatchedForUser("1")

            val responseJson = JsonReader.parseResponseBody(response)
            responseJson.shouldHaveExpectedAmountOf("movies", 2)
            responseJson.shouldHaveValueOf("movies.0.id", "497")
            responseJson.shouldHaveValueOf("movies.1.id", "13")
        }
    }

    @Nested
    @DisplayName("501_ユーザを指定しないと")
    inner class WhenUserIsNotSpecified {

        @Test
        fun `ステータスコード400を返すこと`() {
            val response = taterApi.getV1WatchedWithoutUserId()
            response.code() shouldBeEqualTo 400
        }

        @Test
        fun `MovieAPIに対していかなる問い合わせもしていないこと`() {
            taterApi.getV1WatchedWithoutUserId()

            movieApi.didNotReceiveAnyRequests()
        }
    }

    @Nested
    @DisplayName("502_映画情報の取得に失敗した場合は")
    inner class WhenFailsToFetchMoviesWithError {
        @BeforeEach
        fun setup() {
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("497")
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("680")
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("13")
        }

        @Test
        fun `ステータスコード500を返すこと`() {
            val response = taterApi.getV1WatchedForUser("1")

            response.code() shouldBeEqualTo 500
        }
    }
}
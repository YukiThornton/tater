package project.tater_e2e

import com.squareup.okhttp.Response
import org.amshove.kluent.shouldBeEqualTo
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

        private lateinit var response: Response

        @BeforeEach
        fun setupAndExec() {
            movieApi.returnsMovieDetailsWhenMovieIdIs("497")
            movieApi.returnsMovieDetailsWhenMovieIdIs("680")
            movieApi.returnsMovieDetailsWhenMovieIdIs("13")

            response = taterApi.getV1Watched("1")
        }

        @Test
        fun `MovieAPIに対して視聴履歴のある映画の情報を問い合わせていること`() {
            movieApi.receivedARequestForMovieDetailsOf("497")
            movieApi.receivedARequestForMovieDetailsOf("680")
            movieApi.receivedARequestForMovieDetailsOf("13")
        }

        @Test
        fun `ステータスコードが200になること`() {
            response.code() shouldBeEqualTo 200
        }

        @Test
        fun `視聴履歴のある映画全てを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldNotBeEmpty()
            responseJson.shouldHaveExpectedAmountOf("movies", 3)
        }

        @Test
        fun `それぞれの映画のIDを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.id", "497")
            responseJson.shouldHaveValueOf("movies.1.id", "680")
            responseJson.shouldHaveValueOf("movies.2.id", "13")
        }

        @Test
        fun `それぞれの映画のタイトルを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.title", "The Green Mile")
            responseJson.shouldHaveValueOf("movies.1.title", "Pulp Fiction")
            responseJson.shouldHaveValueOf("movies.2.title", "Forrest Gump")
        }
    }

    @Nested
    @DisplayName("002_指定したユーザに紐づく視聴履歴がないとき")
    inner class WhenViewingHistoriesDoNotExistForUser {
        private lateinit var response: Response

        @BeforeEach
        fun exec() {
            response = taterApi.getV1Watched("9999")
        }

        @Test
        fun `MovieAPIに対していかなる問い合わせもしていないこと`() {
            movieApi.didNotReceiveAnyRequests()
        }

        @Test
        fun `ステータスコードが200になること`() {
            response.code() shouldBeEqualTo 200
        }

        @Test
        fun `一つも映画を返さない`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldNotBeEmpty()
            responseJson.shouldHaveExpectedAmountOf("movies", 0)
        }
    }

    @Nested
    @DisplayName("003_一部の映画情報が存在せず取得に失敗した場合は")
    inner class WhenFailsToFetchNonExistentMovies {
        private lateinit var response: Response

        @BeforeEach
        fun setupAndExec() {
            movieApi.returnsMovieDetailsWhenMovieIdIs("497")
            movieApi.failsWithNotFoundForMovieDetailsWhenMovieIdIs("680")
            movieApi.returnsMovieDetailsWhenMovieIdIs("13")

            response = taterApi.getV1Watched("1")
        }

        @Test
        fun `処理を中断することなくMovieAPIに対して視聴履歴のある全ての映画の情報を問い合わせていること`() {
            movieApi.receivedARequestForMovieDetailsOf("497")
            movieApi.receivedARequestForMovieDetailsOf("680")
            movieApi.receivedARequestForMovieDetailsOf("13")
        }

        @Test
        fun `ステータスコードが200になること`() {
            response.code() shouldBeEqualTo 200
        }

        @Test
        fun `取得に成功したものだけを返す`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldNotBeEmpty()
            responseJson.shouldHaveExpectedAmountOf("movies", 2)
            responseJson.shouldHaveValueOf("movies.0.id", "497")
            responseJson.shouldHaveValueOf("movies.1.id", "13")
        }
    }

    @Nested
    @DisplayName("501_ユーザを指定しないと")
    inner class WhenUserIsNotSpecified {
        private lateinit var response: Response

        @BeforeEach
        fun exec() {
            response = taterApi.getV1WatchedWithoutUserId()
        }

        @Test
        fun `ステータスコードが400になること`() {
            response.code() shouldBeEqualTo 400
        }
    }

    @Nested
    @DisplayName("502_映画情報の取得に失敗した場合は")
    inner class WhenFailsToFetchMoviesWithError {
        private lateinit var response: Response

        @BeforeEach
        fun setupAndExec() {
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("497")
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("680")
            movieApi.failsWithServerErrorForMovieDetailsWhenMovieIdIs("13")

            response = taterApi.getV1Watched("1")
        }

        @Test
        fun `ステータスコードが500になること`() {
            response.code() shouldBeEqualTo 500
        }
    }
}
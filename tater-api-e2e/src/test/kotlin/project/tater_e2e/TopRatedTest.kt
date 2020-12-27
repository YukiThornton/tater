package project.tater_e2e

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.*
import project.tater_e2e.helper.*

@DisplayName("GET /v1/top-rated 高評価の映画の情報を全て返す")
class TopRatedTest {

    companion object {
        private val taterApi = TaterApi()
        private val taterDb = TaterDb()
        private val movieApi = MovieApiMock()

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
    @DisplayName("001_高評価の映画の取得に成功したとき")
    inner class WhenSucceedsToFetchTopRatedMovies {

        @BeforeEach
        fun setup() {
            movieApi.returnsDiscoveredMoviesOfPage(1)
        }

        @Test
        fun `MovieAPIに対して一定の条件の映画を問い合わせていること`() {
            taterApi.getV1TopRatedForUser("2")

            movieApi.receivedARequestForMovieDiscoveryOfPage(1)
        }

        @Test
        fun `ステータスコード200 & JSON形式のレスポンスが返ること`() {
            val response = taterApi.getV1TopRatedForUser("2")

            response.code() shouldBeEqualTo 200
            response.header("Content-Type") shouldContain "application/json"
            JsonReader.isJson(response) shouldBeEqualTo true
        }

        @Test
        fun `高評価の映画20本を返すこと`() {
            val response = taterApi.getV1TopRatedForUser("2")

            val responseJson = JsonReader.parseResponseBody(response)
            responseJson.shouldHaveExpectedAmountOf("movies", 20)
        }

        @Nested
        @DisplayName("それぞれの映画の情報として次の項目を返すこと")
        inner class MovieItems {

            @Test
            fun `ID`() {
                val response = taterApi.getV1TopRatedForUser("2")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.id", "724089")
                responseJson.shouldHaveValueOf("movies.1.id", "696374")
                responseJson.shouldHaveValueOf("movies.2.id", "278")
            }

            @Test
            fun `タイトル`() {
                val response = taterApi.getV1TopRatedForUser("2")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.title", "Gabriel's Inferno Part II")
                responseJson.shouldHaveValueOf("movies.1.title", "Gabriel's Inferno")
                responseJson.shouldHaveValueOf("movies.2.title", "The Shawshank Redemption")
            }

            @Test
            fun `レビュー平均値`() {
                val response = taterApi.getV1TopRatedForUser("2")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.review.average", 8.9)
                responseJson.shouldHaveValueOf("movies.1.review.average", 8.8)
                responseJson.shouldHaveValueOf("movies.2.review.average", 8.7)
            }

            @Test
            fun `レビュー数`() {
                val response = taterApi.getV1TopRatedForUser("2")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.review.count", 1048)
                responseJson.shouldHaveValueOf("movies.1.review.count", 1724)
                responseJson.shouldHaveValueOf("movies.2.review.count", 17767)
            }

            @Test
            fun `指定したユーザの視聴有無`() {
                val response = taterApi.getV1TopRatedForUser("2")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("movies.0.watched", false)
                responseJson.shouldHaveValueOf("movies.1.watched", false)
                responseJson.shouldHaveValueOf("movies.2.watched", true)
            }
        }
    }

    @Nested
    @DisplayName("501_ユーザを指定しないと")
    inner class WhenUserIsNotSpecified {
        @Test
        fun `ステータスコード400が返ること`() {
            val response = taterApi.getV1TopRatedWithoutUserId()

            response.code() shouldBeEqualTo 400
        }

        @Test
        fun `MovieAPIに対していかなる問い合わせもしていないこと`() {
            taterApi.getV1TopRatedWithoutUserId()

            movieApi.didNotReceiveAnyRequests()
        }
    }

    @Nested
    @DisplayName("502_高評価の映画の取得に失敗した場合は")
    inner class WhenFailsToFetchTopRatedMoviesWithError {
        @BeforeEach
        fun setup() {
            movieApi.failsWithServerErrorForMovieDiscoveryOfPage(1)
        }

        @Test
        fun `ステータスコード500を返すこと`() {
            val response = taterApi.getV1TopRatedForUser("2")

            response.code() shouldBeEqualTo 500
        }
    }
}
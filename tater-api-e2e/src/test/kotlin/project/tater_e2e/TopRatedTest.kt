package project.tater_e2e

import com.squareup.okhttp.Response
import org.amshove.kluent.shouldBeEqualTo
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

        private lateinit var response: Response

        @BeforeEach
        fun setupAndExec() {
            movieApi.returnsDiscoveredMoviesOfPage(1)

            response = taterApi.getV1TopRated("2")
        }

        @Test
        fun `MovieAPIに対して一定の条件の映画を問い合わせていること`() {
            movieApi.receivedARequestForMovieDiscoveryOfPage(1)
        }

        @Test
        fun `ステータスコードが200になること`() {
            response.code() shouldBeEqualTo 200
        }

        @Test
        fun `高評価の映画20本を返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldNotBeEmpty()
            responseJson.shouldHaveExpectedAmountOf("movies", 20)
        }

        @Test
        fun `それぞれの映画のIDを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.id", "724089")
            responseJson.shouldHaveValueOf("movies.1.id", "696374")
            responseJson.shouldHaveValueOf("movies.2.id", "278")
        }

        @Test
        fun `それぞれの映画のタイトルを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.title", "Gabriel's Inferno Part II")
            responseJson.shouldHaveValueOf("movies.1.title", "Gabriel's Inferno")
            responseJson.shouldHaveValueOf("movies.2.title", "The Shawshank Redemption")
        }

        @Test
        fun `それぞれの映画のレビュー平均値を返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.review.average", 8.9)
            responseJson.shouldHaveValueOf("movies.1.review.average", 8.8)
            responseJson.shouldHaveValueOf("movies.2.review.average", 8.7)
        }

        @Test
        fun `それぞれの映画のレビュー数を返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.review.count", 1048)
            responseJson.shouldHaveValueOf("movies.1.review.count", 1724)
            responseJson.shouldHaveValueOf("movies.2.review.count", 17767)
        }

        @Test
        fun `それぞれの映画にユーザの視聴記録があるかどうかを返すこと`() {
            val responseJson = JsonReader.fromResponseBody(response)
            responseJson.shouldHaveValueOf("movies.0.watched", false)
            responseJson.shouldHaveValueOf("movies.1.watched", false)
            responseJson.shouldHaveValueOf("movies.2.watched", true)
        }
    }

    @Nested
    @DisplayName("501_ユーザを指定しないと")
    inner class WhenUserIsNotSpecified {
        private lateinit var response: Response

        @BeforeEach
        fun exec() {
            response = taterApi.getV1TopRatedWithoutUserId()
        }

        @Test
        fun `ステータスコードが400になること`() {
            response.code() shouldBeEqualTo 400
        }
    }

    @Nested
    @DisplayName("502_高評価の映画の取得に失敗した場合は")
    inner class WhenFailsToFetchTopRatedMoviesWithError {
        private lateinit var response: Response

        @BeforeEach
        fun setupAndExec() {
            movieApi.failsWithServerErrorForMovieDiscoveryOfPage(1)

            response = taterApi.getV1TopRated("2")
        }

        @Test
        fun `ステータスコードが500になること`() {
            response.code() shouldBeEqualTo 500
        }
    }
}
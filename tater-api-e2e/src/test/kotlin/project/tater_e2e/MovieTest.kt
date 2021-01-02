package project.tater_e2e

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.*
import project.tater_e2e.helper.JsonReader
import project.tater_e2e.helper.MovieApiMock
import project.tater_e2e.helper.TaterApi


@DisplayName("GET /v1/movies/:id 映画の情報を返す")
class MovieTest {

    companion object {
        private val taterApi = TaterApi()
        private val movieApi = MovieApiMock()

        @BeforeAll
        fun initialSetup() {
            movieApi.clearAll()
        }
    }

    @BeforeEach
    fun resetMockRequests() {
        movieApi.clearAllRequests()
    }

    @Nested
    @DisplayName("001_映画情報の取得に成功したとき")
    inner class WhenSucceedsToFetchMovie {

        @BeforeEach
        fun setup() {
            movieApi.returnsMovieDetailsWhenMovieIdIs("496243")
        }

        @Test
        fun `MovieAPIに対して指定した映画の情報を問い合わせていること`() {
            taterApi.getV1MoviesWithId("496243", userId = "1")

            movieApi.receivedARequestForMovieDetailsOf("496243")
        }

        @Test
        fun `ステータスコード200 & JSON形式のレスポンスが返ること`() {
            val response = taterApi.getV1MoviesWithId("496243", userId = "1")

            response.code() shouldBeEqualTo 200
            response.header("Content-Type") shouldContain "application/json"
            JsonReader.isJson(response) shouldBeEqualTo true
        }

        //TODO: タグライン、ジャンル、リリース日、上映時間も取得する
        @Nested
        @DisplayName("映画の情報として次の項目を返すこと")
        inner class MovieItems {
            @Test
            fun `ID`() {
                val response = taterApi.getV1MoviesWithId("496243", userId = "1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("id", "496243")
            }

            @Test
            fun `タイトル`() {
                val response = taterApi.getV1MoviesWithId("496243", userId = "1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("title", "Parasite")
            }

            @Test
            fun `レビュー平均値`() {
                val response = taterApi.getV1MoviesWithId("496243", userId = "1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("review.average", 8.5)
            }

            @Test
            fun `レビュー数`() {
                val response = taterApi.getV1MoviesWithId("496243", userId = "1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("review.count", 10154)
            }

            @Test
            fun `概要`() {
                val response = taterApi.getV1MoviesWithId("496243", userId = "1")

                val responseJson = JsonReader.parseResponseBody(response)
                responseJson.shouldHaveValueOf("overview", "All unemployed, Ki-taek's family takes peculiar interest in the wealthy and glamorous Parks for their livelihood until they get entangled in an unexpected incident.")
            }
        }
    }
}
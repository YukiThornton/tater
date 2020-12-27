package com.tater.rest

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.usecase.*
import io.ktor.http.*
import io.ktor.request.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.*

@DisplayName("HttpRequestExecutor")
class HttpRequestExecutorTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: HttpRequestExecutor

    @MockK
    private lateinit var viewingHistoryUsecase: ViewingHistoryUsecase

    @MockK
    private lateinit var movieSearchUsecase: MovieSearchUsecase

    @Nested
    @DisplayName("getV1Watched")
    inner class GetV1WatchedTest {

        @Nested
        @DisplayName("When usecase returns summaries")
        inner class WhenUsecaseReturnsSummaries {

            private lateinit var actual: HttpRequestExecutor.Result<MovieSummariesJson>
            private val request = mockk<ApplicationRequest>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } returns MovieSummaries(listOf(
                    MovieSummary(MovieId("id1"), MovieTitle("title1")),
                    MovieSummary(MovieId("id2"), MovieTitle("title2"))
                ))

                actual = sut.getV1Watched(request)
            }

            @Test
            fun `Retrieves User ID from a header`() {
                verify { request.header("tater-user-id") }
            }

            @Test
            fun `Calls usecase with User ID from the header`() {
                verify { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) }
            }

            @Test
            fun `Returns a result with status OK and MovieSummariesJson`() {
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                        HttpStatusCode.OK,
                        MovieSummariesJson(listOf(
                                MovieSummaryJson("id1", "title1"),
                                MovieSummaryJson("id2", "title2")
                        )))
            }
        }

        @Nested
        @DisplayName("When userId is missing in header")
        inner class WhenUserIdIsMissingInHeader {

            private val request = mockk<ApplicationRequest>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns null
                every { viewingHistoryUsecase.allMoviesWatchedBy(null) } returns MovieSummaries(emptyList())

                sut.getV1Watched(request)
            }

            @Test
            fun `Gives usecase null for UserId`() {
                verify { viewingHistoryUsecase.allMoviesWatchedBy(null) }
            }
        }

        @Nested
        @DisplayName("When usecase throws a UserNotSpecifiedException")
        inner class WhenUsecaseThrowsAUserNotSpecifiedException {

            private lateinit var actual: HttpRequestExecutor.Result<MovieSummariesJson>
            private val request = mockk<ApplicationRequest>()
            private val userNotSpecifiedException = mockk<UserNotSpecifiedException>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws userNotSpecifiedException

                actual = sut.getV1Watched(request)
            }

            @Test
            fun `Returns a result with status BadRequest and the exception`() {
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                    HttpStatusCode.BadRequest,
                    null,
                    userNotSpecifiedException
                )
            }
        }

        @Nested
        @DisplayName("When usecase throws a WatchedMoviesUnavailableException")
        inner class WhenUsecaseThrowsAWatchedMoviesUnavailableException {

            private lateinit var actual: HttpRequestExecutor.Result<MovieSummariesJson>
            private val request = mockk<ApplicationRequest>()
            private val watchedMoviesUnavailableException = mockk<WatchedMoviesUnavailableException>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws watchedMoviesUnavailableException

                actual = sut.getV1Watched(request)
            }

            @Test
            fun `Returns a result with status InternalServerError and the exception`() {
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                    HttpStatusCode.InternalServerError,
                    null,
                    watchedMoviesUnavailableException
                )
            }
        }
    }

    @Nested
    @DisplayName("getV1TopRated")
    inner class GetV1TopRatedTest {

        private lateinit var actual: HttpRequestExecutor.Result<MovieListJson>
        private val request = mockk<ApplicationRequest>()

        @Nested
        @DisplayName("When usecase returns movies")
        inner class WhenUsecaseReturnsMovies {
            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(UserId("userId1")) } returns PersonalizedMovies(listOf(
                        PersonalizedMovie(UserId("userId1"), false, Movie(MovieId("id1"), MovieTitle("title1"), MovieReview(AverageScore(5.6), ReviewCount(1000)))),
                        PersonalizedMovie(UserId("userId1"), true, Movie(MovieId("id2"), MovieTitle("title2"), MovieReview(AverageScore(5.5), ReviewCount(1200)))),
                        PersonalizedMovie(UserId("userId1"), true, Movie(MovieId("id3"), MovieTitle("title3"), MovieReview(AverageScore(5.4), ReviewCount(900)))),
                ))

                actual = sut.getV1TopRated(request)
            }

            @Test
            fun `Retrieves User ID from a header`() {
                verify { request.header("tater-user-id") }
            }

            @Test
            fun `Calls usecase with User ID from the header`() {
                verify { movieSearchUsecase.topRatedMovies(UserId("userId1")) }
            }

            @Test
            fun `Returns a result with status OK and json`() {
                actual.responseStatus shouldBeEqualTo HttpStatusCode.OK
                actual.responseBody shouldBeEqualTo MovieListJson(listOf(
                        MovieJson("id1", "title1", false, ReviewJson(5.6, 1000)),
                        MovieJson("id2", "title2", true, ReviewJson(5.5, 1200)),
                        MovieJson("id3", "title3", true, ReviewJson(5.4, 900)),
                ))
            }
        }

        @Nested
        @DisplayName("When usecase throws a UserNotSpecifiedException")
        inner class WhenUsecaseThrowsAUserNotSpecifiedException {

            private lateinit var actual: HttpRequestExecutor.Result<MovieListJson>
            private val request = mockk<ApplicationRequest>()
            private val userId = UserId("userId1")
            private val userNotSpecifiedException = mockk<UserNotSpecifiedException>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(userId) } throws userNotSpecifiedException

                actual = sut.getV1TopRated(request)
            }

            @Test
            fun `Calls usecase function`() {
                verify { movieSearchUsecase.topRatedMovies(userId) }
            }

            @Test
            fun `Returns a result with status BadRequest and the exception`() {
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                        HttpStatusCode.BadRequest,
                        null,
                        userNotSpecifiedException
                )
            }
        }

        @Nested
        @DisplayName("When usecase throws a TopRatedMoviesUnavailableException")
        inner class WhenUsecaseThrowsATopRatedMoviesUnavailableException {

            private lateinit var actual: HttpRequestExecutor.Result<MovieListJson>
            private val request = mockk<ApplicationRequest>()
            private val userId = UserId("userId1")
            private val unavailableException = mockk<TopRatedMoviesUnavailableException>()

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(userId) } throws unavailableException

                actual = sut.getV1TopRated(request)
            }

            @Test
            fun `Calls usecase function`() {
                verify { movieSearchUsecase.topRatedMovies(userId) }
            }

            @Test
            fun `Returns a result with status InternalServerError and the exception`() {
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                        HttpStatusCode.InternalServerError,
                        null,
                        unavailableException
                )
            }
        }
    }
}
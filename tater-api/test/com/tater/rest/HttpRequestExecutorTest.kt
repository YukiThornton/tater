package com.tater.rest

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.domain.attribute.*
import com.tater.usecase.*
import io.ktor.application.*
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
    private lateinit var jsonConverter: JsonConverter

    @MockK
    private lateinit var viewingHistoryUsecase: ViewingHistoryUsecase

    @MockK
    private lateinit var movieSearchUsecase: MovieSearchUsecase

    @MockK
    private lateinit var movieAcquisitionUsecase: MovieAcquisitionUsecase

    @Nested
    @DisplayName("getV1Watched")
    inner class GetV1WatchedTest {

        @Nested
        @DisplayName("When usecase returns summaries")
        inner class WhenUsecaseReturnsSummaries {

            private val theCall = mockk<ApplicationCall>()
            private val summaries = mockk<MovieSummaries>()
            private val movieSummariesJson = mockk<MovieSummariesJson>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } returns summaries
                every { jsonConverter.toMovieSummariesJson(summaries) } returns movieSummariesJson
            }

            @Test
            fun `Retrieves User ID from a header to call usecase with it`() {
                sut.getV1Watched(theCall)

                verify { theCall.request.header("tater-user-id") }
                verify { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) }
            }

            @Test
            fun `Returns a result with status OK and MovieSummariesJson created by JsonConverter`() {
                val actual = sut.getV1Watched(theCall)

                verify { jsonConverter.toMovieSummariesJson(summaries) }
                actual shouldBeEqualTo HttpRequestExecutor.Result(
                        HttpStatusCode.OK,
                        movieSummariesJson
                )
            }
        }

        @Nested
        @DisplayName("When userId is missing in header")
        inner class WhenUserIdIsMissingInHeader {

            private val theCall = mockk<ApplicationCall>()

            @BeforeEach
            fun setup() {
                val summaries = mockk<MovieSummaries>()
                every { theCall.request.header("tater-user-id") } returns null
                every { viewingHistoryUsecase.allMoviesWatchedBy(null) } returns summaries
                every { jsonConverter.toMovieSummariesJson(summaries) } returns mockk()
            }

            @Test
            fun `Gives usecase null for UserId`() {
                sut.getV1Watched(theCall)

                verify { viewingHistoryUsecase.allMoviesWatchedBy(null) }
            }
        }

        @Nested
        @DisplayName("When usecase throws a UserNotSpecifiedException")
        inner class WhenUsecaseThrowsAUserNotSpecifiedException {

            private val theCall = mockk<ApplicationCall>()

            private val userNotSpecifiedException = mockk<UserNotSpecifiedException>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws userNotSpecifiedException
            }

            @Test
            fun `Returns a result with status BadRequest and the exception`() {
                val actual = sut.getV1Watched(theCall)

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

            private val theCall = mockk<ApplicationCall>()

            private val watchedMoviesUnavailableException = mockk<WatchedMoviesUnavailableException>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws watchedMoviesUnavailableException
            }

            @Test
            fun `Returns a result with status InternalServerError and the exception`() {
                val actual = sut.getV1Watched(theCall)

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

        @Nested
        @DisplayName("When usecase returns movies")
        inner class WhenUsecaseReturnsMovies {

            private val theCall = mockk<ApplicationCall>()
            private val personalizedMovies = mockk<PersonalizedMovies>()
            private val reviewedMovieListJson = mockk<ReviewedMovieListJson>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(UserId("userId1")) } returns personalizedMovies
                every { jsonConverter.toReviewedMovieListJson(personalizedMovies) } returns reviewedMovieListJson
            }

            @Test
            fun `Retrieves User ID from a header to call usecase with it`() {
                sut.getV1TopRated(theCall)

                verify { theCall.request.header("tater-user-id") }
                verify { movieSearchUsecase.topRatedMovies(UserId("userId1")) }
            }

            @Test
            fun `Returns a result with status OK and json created by JsonConverter`() {
                val actual = sut.getV1TopRated(theCall)

                every { jsonConverter.toReviewedMovieListJson(personalizedMovies) }
                actual.responseStatus shouldBeEqualTo HttpStatusCode.OK
                actual.responseBody shouldBeEqualTo reviewedMovieListJson
            }
        }

        @Nested
        @DisplayName("When usecase throws a UserNotSpecifiedException")
        inner class WhenUsecaseThrowsAUserNotSpecifiedException {

            private val theCall = mockk<ApplicationCall>()

            private val userId = UserId("userId1")
            private val userNotSpecifiedException = mockk<UserNotSpecifiedException>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(userId) } throws userNotSpecifiedException
            }

            @Test
            fun `Calls usecase function`() {
                sut.getV1TopRated(theCall)

                verify { movieSearchUsecase.topRatedMovies(userId) }
            }

            @Test
            fun `Returns a result with status BadRequest and the exception`() {
                val actual = sut.getV1TopRated(theCall)

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

            private val theCall = mockk<ApplicationCall>()

            private val userId = UserId("userId1")
            private val unavailableException = mockk<TopRatedMoviesUnavailableException>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { movieSearchUsecase.topRatedMovies(userId) } throws unavailableException
            }

            @Test
            fun `Calls usecase function`() {
                sut.getV1TopRated(theCall)

                verify { movieSearchUsecase.topRatedMovies(userId) }
            }

            @Test
            fun `Returns a result with status InternalServerError and the exception`() {
                val actual = sut.getV1TopRated(theCall)

                actual shouldBeEqualTo HttpRequestExecutor.Result(
                        HttpStatusCode.InternalServerError,
                        null,
                        unavailableException
                )
            }
        }
    }

    @Nested
    @DisplayName("getV1MovieWithId")
    inner class GetV1MoviesWithIdTest {

        @Nested
        @DisplayName("When usecase returns movie")
        inner class WhenUsecaseReturnsMovie {

            private val theCall = mockk<ApplicationCall>()
            private val localizedMovie = mockk<LocalizedMovie>()
            private val movieJson = mockk<MovieJson>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { theCall.parameters["id"] } returns "movieId1"
                every { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) } returns localizedMovie
                every { jsonConverter.toMovieJson(localizedMovie) } returns movieJson
            }

            @Test
            fun `Retrieves Movie ID and User Id from the parameter to call usecase with them`() {
                sut.getV1MovieWithId(theCall)

                verify { theCall.request.header("tater-user-id") }
                verify { theCall.parameters["id"] }
                verify { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) }
            }

            @Test
            fun `Returns a result with status OK and json created by JsonConverter`() {
                val actual = sut.getV1MovieWithId(theCall)

                verify { jsonConverter.toMovieJson(localizedMovie) }
                actual.responseStatus shouldBeEqualTo HttpStatusCode.OK
                actual.responseBody shouldBeEqualTo movieJson
            }
        }

        @Nested
        @DisplayName("When usecase returns null")
        inner class WhenUsecaseThrowsAMovieNotFoundException {

            private val theCall = mockk<ApplicationCall>()

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { theCall.parameters["id"] } returns "movieId1"
                every { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) } returns null
            }

            @Test
            fun `Returns a result with status BadRequest`() {
                val actual = sut.getV1MovieWithId(theCall)

                verify { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) }

                actual.responseStatus shouldBeEqualTo HttpStatusCode.NotFound
                actual.responseBody shouldBeEqualTo null
                actual.error shouldBeEqualTo null
            }
        }

        @Nested
        @DisplayName("When usecase throws a UserNotSpecifiedException")
        inner class WhenUsecaseThrowsAUserNotSpecifiedException {

            private val theCall = mockk<ApplicationCall>()

            private val userNotSpecifiedException = UserNotSpecifiedException("")

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { theCall.parameters["id"] } returns "movieId1"
                every { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) } throws userNotSpecifiedException
            }

            @Test
            fun `Returns a result with status BadRequest and error`() {
                val actual = sut.getV1MovieWithId(theCall)

                verify { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) }

                actual.responseStatus shouldBeEqualTo HttpStatusCode.BadRequest
                actual.responseBody shouldBeEqualTo null
                actual.error shouldBeEqualTo userNotSpecifiedException
            }
        }

        @Nested
        @DisplayName("When usecase throws a MovieUnavailableException")
        inner class WhenUsecaseThrowsAMovieUnavailableException {

            private val theCall = mockk<ApplicationCall>()

            private val movieUnavailableException = MovieUnavailableException(RuntimeException(""), "")

            @BeforeEach
            fun setup() {
                every { theCall.request.header("tater-user-id") } returns "userId1"
                every { theCall.parameters["id"] } returns "movieId1"
                every { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) } throws movieUnavailableException
            }

            @Test
            fun `Returns a result with status InternalServerError and error`() {
                val actual = sut.getV1MovieWithId(theCall)

                verify { movieAcquisitionUsecase.getMovieOf(MovieId("movieId1"), UserId("userId1")) }

                actual.responseStatus shouldBeEqualTo HttpStatusCode.InternalServerError
                actual.responseBody shouldBeEqualTo null
                actual.error shouldBeEqualTo movieUnavailableException
            }
        }
    }
}
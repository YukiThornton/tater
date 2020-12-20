package com.tater.rest

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.usecase.UserNotSpecifiedException
import com.tater.usecase.ViewingHistoryUsecase
import com.tater.usecase.WatchedMoviesUnavailableException
import io.ktor.http.*
import io.ktor.request.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.*

@DisplayName("RequestHandler")
class RequestHandlerTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: RequestHandler

    @MockK
    private lateinit var viewingHistoryUsecase: ViewingHistoryUsecase

    @Nested
    @DisplayName("getV1Watched")
    inner class GetV1WatchedTest {

        @Nested
        @DisplayName("When usecase returns summaries")
        inner class WhenUsecaseReturnsSummaries {

            private lateinit var actual: RequestHandler.Result<MovieSummariesJson>
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
                actual shouldBeEqualTo RequestHandler.Result(
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

            private lateinit var actual: RequestHandler.Result<MovieSummariesJson>
            private val request = mockk<ApplicationRequest>()
            private val userNotSpecifiedException = UserNotSpecifiedException("error")

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws userNotSpecifiedException

                actual = sut.getV1Watched(request)
            }

            @Test
            fun `Returns a result with status BadRequest and the exception`() {
                actual shouldBeEqualTo RequestHandler.Result(
                    HttpStatusCode.BadRequest,
                    null,
                    userNotSpecifiedException
                )
            }
        }

        @Nested
        @DisplayName("When usecase throws a WatchedMoviesUnavailableException")
        inner class WhenUsecaseThrowsAWatchedMoviesUnavailableException {

            private lateinit var actual: RequestHandler.Result<MovieSummariesJson>
            private val request = mockk<ApplicationRequest>()
            private val watchedMoviesUnavailableException = WatchedMoviesUnavailableException("error", Exception(""))

            @BeforeEach
            fun setupAndExec() {
                every { request.header("tater-user-id") } returns "userId1"
                every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws watchedMoviesUnavailableException

                actual = sut.getV1Watched(request)
            }

            @Test
            fun `Returns a result with status InternalServerError and the exception`() {
                actual shouldBeEqualTo RequestHandler.Result(
                    HttpStatusCode.InternalServerError,
                    null,
                    watchedMoviesUnavailableException
                )
            }
        }
    }
}
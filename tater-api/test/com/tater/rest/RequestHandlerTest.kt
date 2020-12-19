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

        @Test
        fun `Returns status OK and MovieSummariesJson when usecase returns summaries`() {
            val request = mockk<ApplicationRequest>()
            val summaries = MovieSummaries(listOf(
                    MovieSummary(MovieId("id1"), MovieTitle("title1")),
                    MovieSummary(MovieId("id2"), MovieTitle("title2"))
            ))
            val expected = RequestHandler.Result(
                    HttpStatusCode.OK,
                    MovieSummariesJson(listOf(
                            MovieSummaryJson("id1", "title1"),
                            MovieSummaryJson("id2", "title2")
                    ))
            )

            every { request.header("tater-user-id") } returns "userId1"
            every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } returns summaries

            sut.getV1Watched(request) shouldBeEqualTo expected

            verify { request.header("tater-user-id") }
            verify { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) }
        }

        @Test
        fun `Gives usecase null for UserId when userId is missing in header`() {
            val request = mockk<ApplicationRequest>()
            val summaries = MovieSummaries(emptyList())

            every { request.header("tater-user-id") } returns null
            every { viewingHistoryUsecase.allMoviesWatchedBy(null) } returns summaries

            sut.getV1Watched(request)

            verify { request.header("tater-user-id") }
            verify { viewingHistoryUsecase.allMoviesWatchedBy(null) }
        }

        @Test
        fun `Returns Bad request response when usecase throws a UserNotSpecifiedException`() {
            val request = mockk<ApplicationRequest>()
            val exception = UserNotSpecifiedException("error")
            val expected = RequestHandler.Result(
                HttpStatusCode.BadRequest,
                null,
                exception
            )

            every { request.header("tater-user-id") } returns "userId1"
            every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws exception

            sut.getV1Watched(request) shouldBeEqualTo expected

            verify { request.header("tater-user-id") }
            verify { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) }
        }

        @Test
        fun `Returns Internal System Error response when usecase throws a WatchedMoviesUnavailableException`() {
            val request = mockk<ApplicationRequest>()
            val exception = WatchedMoviesUnavailableException("error", Exception(""))
            val expected = RequestHandler.Result(
                HttpStatusCode.InternalServerError,
                null,
                exception
            )

            every { request.header("tater-user-id") } returns "userId1"
            every { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) } throws exception

            sut.getV1Watched(request) shouldBeEqualTo expected

            verify { request.header("tater-user-id") }
            verify { viewingHistoryUsecase.allMoviesWatchedBy(UserId("userId1")) }
        }
    }
}
package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.MovieDetails
import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.port.MovieDetailPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withCause
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieDetailUsecase")
class MovieDetailUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieDetailUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var movieDetailPort: MovieDetailPort

    @Nested
    @DisplayName("detailsOf")
    inner class DetailsOf {

        @Nested
        @DisplayName("When movie details is available")
        inner class WhenMovieDetailsIsAvailable {

            private val expected = mockk<MovieDetails>()

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { movieDetailPort.getDetailsOf(MovieId("movieId1")) } returns expected
            }

            @Test
            fun `Makes sure that userId exists with UserIdChecker`() {
                sut.detailsOf(MovieId("movieId1"), UserId("userId1"))

                verify { userIdChecker.makeSureUserIdExists(UserId("userId1")) }
            }

            @Test
            fun `Gets movie details from port and returns as it is`() {
                val actual = sut.detailsOf(MovieId("movieId1"), UserId("userId1"))

                verify(exactly = 1) { movieDetailPort.getDetailsOf(MovieId("movieId1")) }
                actual shouldBeEqualTo expected
            }
        }

        @Nested
        @DisplayName("When movie details is not found")
        inner class WhenMovieDetailsIsNotFound {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { movieDetailPort.getDetailsOf(MovieId("movieId1")) } returns null
            }

            @Test
            fun `Returns null`() {
                val actual = sut.detailsOf(MovieId("movieId1"), UserId("userId1"))

                verify(exactly = 1) { movieDetailPort.getDetailsOf(MovieId("movieId1")) }
                actual shouldBeEqualTo null
            }
        }

        @Nested
        @DisplayName("When movie details is unavailable")
        inner class WhenMovieDetailsIsUnavailable {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { movieDetailPort.getDetailsOf(MovieId("movieId1")) } throws MovieDetailPort.UnavailableException(RuntimeException(""), "")
            }

            @Test
            fun `Throws a MovieDetailsUnavailableException`() {
                val expectedException = MovieDetailsUnavailableException::class
                val exceptionCause = MovieDetailPort.UnavailableException::class
                val exceptionMessage = "Movie(id=movieId1) requested by user(id=userId1) is unavailable"

                { sut.detailsOf(MovieId("movieId1"), UserId("userId1")) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage

                verify(exactly = 1) { movieDetailPort.getDetailsOf(MovieId("movieId1")) }
            }
        }
    }
}
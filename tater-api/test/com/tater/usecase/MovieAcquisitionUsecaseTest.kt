package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.Movie
import com.tater.domain.attribute.MovieId
import com.tater.domain.UserId
import com.tater.port.MoviePort
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

@DisplayName("MovieAcquisitionUsecase")
class MovieAcquisitionUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieAcquisitionUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var moviePort: MoviePort

    @Nested
    @DisplayName("getMovieOf")
    inner class GetMovieOfTest {

        @Nested
        @DisplayName("When movie data is available")
        inner class WhenMovieIsAvailable {

            private val expected = mockk<Movie>()

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { moviePort.getMovieOf(MovieId("movieId1")) } returns expected
            }

            @Test
            fun `Makes sure that userId exists with UserIdChecker`() {
                sut.getMovieOf(MovieId("movieId1"), UserId("userId1"))

                verify { userIdChecker.makeSureUserIdExists(UserId("userId1")) }
            }

            @Test
            fun `Gets the movie from port and returns as it is`() {
                val actual = sut.getMovieOf(MovieId("movieId1"), UserId("userId1"))

                verify(exactly = 1) { moviePort.getMovieOf(MovieId("movieId1")) }
                actual shouldBeEqualTo expected
            }
        }

        @Nested
        @DisplayName("When movie data is not found")
        inner class WhenMovieIsNotFound {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { moviePort.getMovieOf(MovieId("movieId1")) } returns null
            }

            @Test
            fun `Returns null`() {
                val actual = sut.getMovieOf(MovieId("movieId1"), UserId("userId1"))

                verify(exactly = 1) { moviePort.getMovieOf(MovieId("movieId1")) }
                actual shouldBeEqualTo null
            }
        }

        @Nested
        @DisplayName("When movie data is unavailable")
        inner class WhenMovieIsUnavailable {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { moviePort.getMovieOf(MovieId("movieId1")) } throws MoviePort.UnavailableException(RuntimeException(""), "")
            }

            @Test
            fun `Throws a MovieUnavailableException`() {
                val expectedException = MovieUnavailableException::class
                val exceptionCause = MoviePort.UnavailableException::class
                val exceptionMessage = "Movie(id=movieId1) requested by user(id=userId1) is unavailable"

                { sut.getMovieOf(MovieId("movieId1"), UserId("userId1")) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage

                verify(exactly = 1) { moviePort.getMovieOf(MovieId("movieId1")) }
            }
        }
    }
}
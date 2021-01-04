package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.LocalizedMovie
import com.tater.domain.LocalizedMovieAttributes
import com.tater.domain.Movie
import com.tater.domain.attribute.MovieId
import com.tater.domain.UserId
import com.tater.port.LocalizedAttributesPort
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

    @MockK
    private lateinit var localizedAttributesPort: LocalizedAttributesPort

    @Nested
    @DisplayName("getMovieOf")
    inner class GetMovieOfTest {

        @Nested
        @DisplayName("When movie data is available")
        inner class WhenMovieIsAvailable {

            private val movie = mockk<Movie>()
            private val japaneseAttributes = mockk<LocalizedMovieAttributes>()

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(UserId("userId1")) } returns UserId("userId1")
                every { moviePort.getMovieOf(MovieId("movieId1")) } returns movie
                every { localizedAttributesPort.getJapaneseAttributesOf(MovieId("movieId1")) } returns japaneseAttributes
            }

            @Test
            fun `Makes sure that userId exists with UserIdChecker`() {
                sut.getMovieOf(MovieId("movieId1"), UserId("userId1"))

                verify { userIdChecker.makeSureUserIdExists(UserId("userId1")) }
            }

            @Test
            fun `Gets the movie and japanese attributes from port and returns LocalizedMovie`() {
                val actual = sut.getMovieOf(MovieId("movieId1"), UserId("userId1"))

                verify(exactly = 1) { moviePort.getMovieOf(MovieId("movieId1")) }
                verify(exactly = 1) { localizedAttributesPort.getJapaneseAttributesOf(MovieId("movieId1")) }
                actual shouldBeEqualTo LocalizedMovie(movie, japaneseAttributes)
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
                verify(exactly = 0) { localizedAttributesPort.getJapaneseAttributesOf(any()) }
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
                verify(exactly = 0) { localizedAttributesPort.getJapaneseAttributesOf(any()) }
            }
        }
    }
}
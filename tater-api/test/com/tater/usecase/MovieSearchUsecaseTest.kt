package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.port.ReviewedMoviePort
import com.tater.port.ViewingHistoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieSearchUsecase")
class MovieSearchUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieSearchUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var reviewedMoviePort: ReviewedMoviePort

    @MockK
    private lateinit var viewingHistoryPort: ViewingHistoryPort

    @Nested
    @DisplayName("topRatedMovies")
    inner class TopRatedMovies {

        @Nested
        @DisplayName("When enough movies are available")
        inner class WhenEnoughMoviesAreAvailable {

            private val theUserId = UserId("userId1")

            private val theOnlyAcceptableSearchFilter = MovieSearchFilter.withMinimumReviewCount(ReviewCount(1000))
            private val theOnlyAcceptableSortKey = SortedBy.ReviewAverageDesc

            private val searchedMovies = mockk<ReviewedMovies>()
            private val viewingHistoriesForTheUser = mockk<ViewingHistories>()
            private val expected = mockk<PersonalizedMovies>()

            @BeforeEach
            fun setup() {
                mockkObject(PersonalizedMovies.Companion)
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { reviewedMoviePort.searchMovies(theOnlyAcceptableSearchFilter, theOnlyAcceptableSortKey) } returns searchedMovies
                every { searchedMovies.isEmpty() } returns false
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns viewingHistoriesForTheUser
                every { PersonalizedMovies.from(searchedMovies, viewingHistoriesForTheUser) } returns expected
            }

            @Test
            fun `Makes sure that userId exists with UserIdChecker`() {
                sut.topRatedMovies(theUserId)

                verify { userIdChecker.makeSureUserIdExists(theUserId) }
            }

            @Test
            fun `Gets necessary data from each port and creates personalized movies out of them`() {
                sut.topRatedMovies(theUserId)

                verify(exactly = 1) { reviewedMoviePort.searchMovies(theOnlyAcceptableSearchFilter, theOnlyAcceptableSortKey) }
                verify(exactly = 1) { viewingHistoryPort.getViewingHistoriesFor(theUserId) }
                verify { PersonalizedMovies.from(searchedMovies, viewingHistoriesForTheUser) }
            }

            @Test
            fun `Returns created data`() {
                val actual = sut.topRatedMovies(theUserId)

                actual shouldBeEqualTo expected
            }
        }

        @Nested
        @DisplayName("When no movies are available")
        inner class WhenNoMoviesAreAvailable {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val emptyMovies = mockk<ReviewedMovies>()
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { reviewedMoviePort.searchMovies(any(), any()) } returns emptyMovies
                every { emptyMovies.isEmpty() } returns true
            }

            @Test
            fun `Gets movies but does not get viewing histories`() {
                sut.topRatedMovies(theUserId)

                verify(exactly = 1) { reviewedMoviePort.searchMovies(any(), any()) }
                verify(exactly = 0) { viewingHistoryPort.getViewingHistoriesFor(any()) }
            }

            @Test
            fun `Returns empty PersonalizedMovies`() {
                val actual = sut.topRatedMovies(theUserId)

                actual shouldBeEqualTo PersonalizedMovies(emptyList())
            }
        }

        @Nested
        @DisplayName("When UserIdChecker throws an exception")
        inner class WhenUserIdCheckerThrowsAnException {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(null) } throws UserNotSpecifiedException("")
            }

            @Test
            fun `Throws the same UserNotSpecifiedException`() {
                { sut.topRatedMovies(null) } shouldThrow UserNotSpecifiedException::class
            }
        }

        @Nested
        @DisplayName("When movie port throws an exception")
        inner class WhenReviewedMoviePortThrowsAnException {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { reviewedMoviePort.searchMovies(any(), any()) } throws ReviewedMoviePort.SearchUnavailableException("", null)
            }

            @Test
            fun `Throws a TopRatedMoviesUnavailableException`() {
                val expectedException = TopRatedMoviesUnavailableException::class
                val exceptionCause = ReviewedMoviePort.SearchUnavailableException::class
                val exceptionMessage = "Top rated movies for user(id=userId1) are unavailable"

                { sut.topRatedMovies(theUserId) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }

        @Nested
        @DisplayName("When viewing history port throws an exception")
        inner class WhenViewingHistoryPortThrowsAnException {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val nonEmptyMovies = mockk<ReviewedMovies>()
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { reviewedMoviePort.searchMovies(any(), any()) } returns nonEmptyMovies
                every { nonEmptyMovies.isEmpty() } returns false
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } throws ViewingHistoryPort.UnavailableException("", null)
            }

            @Test
            fun `Throws a TopRatedMoviesUnavailableException`() {
                val expectedException = TopRatedMoviesUnavailableException::class
                val exceptionCause = ViewingHistoryPort.UnavailableException::class
                val exceptionMessage = "Top rated movies for user(id=userId1) are unavailable"

                { sut.topRatedMovies(theUserId) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }
    }
}
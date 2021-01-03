package com.tater.domain

import com.tater.AutoResetMock
import com.tater.domain.attribute.*
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("PersonalizedMovies")
class PersonalizedMoviesTest: AutoResetMock {

    @Nested
    @DisplayName("from")
    inner class From {

        @Test
        fun `Creates PersonalizedMovies from Movies and ViewingHistories`() {
            val movie1 = ReviewedMovie(MovieId("id1"), MovieTitle("title1"), MovieReview(AverageScore(5.6), ReviewCount(1000)))
            val movie2 = ReviewedMovie(MovieId("id2"), MovieTitle("title2"), MovieReview(AverageScore(5.5), ReviewCount(1200)))
            val movie3 = ReviewedMovie(MovieId("id3"), MovieTitle("title3"), MovieReview(AverageScore(5.4), ReviewCount(900)))
            val movies = ReviewedMovies(listOf(movie1, movie2, movie3))
            val viewingHistories = mockk<ViewingHistories>()

            every { viewingHistories.userId } returns UserId("userId1")
            every { viewingHistories.watched(movie1.id) } returns false
            every { viewingHistories.watched(movie2.id) } returns true
            every { viewingHistories.watched(movie3.id) } returns true

            PersonalizedMovies.from(movies, viewingHistories) shouldBeEqualTo PersonalizedMovies(listOf(
                    PersonalizedMovie(UserId("userId1"), false, movie1),
                    PersonalizedMovie(UserId("userId1"), true, movie2),
                    PersonalizedMovie(UserId("userId1"), true, movie3),
            ))
        }
    }
}
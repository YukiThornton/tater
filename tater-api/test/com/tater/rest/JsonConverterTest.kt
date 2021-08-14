package com.tater.rest

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.domain.attribute.*
import io.mockk.impl.annotations.InjectMockKs
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("JsonConverter")
class JsonConverterTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: JsonConverter

    @Nested
    @DisplayName("toMovieJson")
    inner class ToMovieJsonTest {

        @Test
        fun `Returns movie json when Japanese attribute exists`() {
            val localizedMovie = LocalizedMovie(
                    Movie(MovieId("movieId1"), MovieTitle("englishTitle1"),
                            MovieOverview("overview1"),
                            Runtime(123),
                            MovieReview(AverageScore(5.6), ReviewCount(1000))
                    ),
                    LocalizedMovieAttributes(MovieTitle("japaneseTitle1")))
            val expected = MovieJson(
                    "movieId1",
                    LocalizedTextJson("englishTitle1", "japaneseTitle1"),
                    "overview1",
                    123,
                    ReviewJson(5.6, 1000)
            )

            sut.toMovieJson(localizedMovie) shouldBeEqualTo expected
        }

        @Test
        fun `Returns movie json without Japanese title when Japanese attribute does not exist`() {
            val localizedMovie = LocalizedMovie(
                    Movie(MovieId("movieId1"), MovieTitle("englishTitle1"),
                            MovieOverview("overview1"),
                            Runtime(123),
                            MovieReview(AverageScore(5.6), ReviewCount(1000))
                    ),
                    null)
            val expected = MovieJson(
                    "movieId1",
                    LocalizedTextJson("englishTitle1", null),
                    "overview1",
                    123,
                    ReviewJson(5.6, 1000)
            )

            sut.toMovieJson(localizedMovie) shouldBeEqualTo expected
        }
    }

    @Nested
    @DisplayName("toMovieSummariesJson")
    inner class ToMovieSummariesJsonTest {

        @Test
        fun `Returns a MovieSummariesJson`() {
            val summaries = MovieSummaries(listOf(
                    MovieSummary(MovieId("id1"), MovieTitle("title1")),
                    MovieSummary(MovieId("id2"), MovieTitle("title2"))
            ))
            val expected = MovieSummariesJson(listOf(
                    MovieSummaryJson("id1", "title1"),
                    MovieSummaryJson("id2", "title2")
            ))

            sut.toMovieSummariesJson(summaries) shouldBeEqualTo expected
        }
    }

    @Nested
    @DisplayName("toReviewedMovieListJson")
    inner class ToReviewedMovieListJsonTest {

        @Test
        fun `Returns reviewed movie list json`() {
            val movies = PersonalizedMovies(listOf(
                    PersonalizedMovie(UserId("userId1"), false, ReviewedMovie(MovieId("id1"), MovieTitle("title1"), MovieReview(AverageScore(5.6), ReviewCount(1000)))),
                    PersonalizedMovie(UserId("userId1"), true, ReviewedMovie(MovieId("id2"), MovieTitle("title2"), MovieReview(AverageScore(5.5), ReviewCount(1200)))),
                    PersonalizedMovie(UserId("userId1"), true, ReviewedMovie(MovieId("id3"), MovieTitle("title3"), MovieReview(AverageScore(5.4), ReviewCount(900)))),
            ))
            val expected = ReviewedMovieListJson(listOf(
                    ReviewedMovieJson("id1", "title1", false, ReviewJson(5.6, 1000)),
                    ReviewedMovieJson("id2", "title2", true, ReviewJson(5.5, 1200)),
                    ReviewedMovieJson("id3", "title3", true, ReviewJson(5.4, 900)),
            ))

            sut.toReviewedMovieListJson(movies) shouldBeEqualTo expected
        }
    }
}
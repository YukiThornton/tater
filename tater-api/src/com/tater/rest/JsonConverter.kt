package com.tater.rest

import com.tater.domain.*
import com.tater.domain.attribute.MovieReview

class JsonConverter {
    fun toMovieJson(movie: LocalizedMovie) =
            MovieJson(movie.id().value, toLocalizedTitleJson(movie), movie.overview().value, movie.runtime().minuteValue, toReviewJson(movie.review()))

    fun toReviewedMovieListJson(movies: PersonalizedMovies) =
            movies.map { toReviewedMovieJson(it) }.let(::ReviewedMovieListJson)

    fun toMovieSummariesJson(summaries: MovieSummaries) =
            summaries.map { summary -> toMovieSummaryJson(summary) }.let(::MovieSummariesJson)

    private fun toMovieSummaryJson(summary: MovieSummary) =
            MovieSummaryJson(summary.id.value, summary.title.value)

    private fun toLocalizedTitleJson(movie: LocalizedMovie) =
            LocalizedTextJson(movie.englishTitle().value, movie.japaneseTitle()?.value)

    private fun toReviewedMovieJson(movie: PersonalizedMovie) =
            ReviewedMovieJson(movie.movieId.value, movie.movieTitle.value, movie.watched, toReviewJson(movie.movieReview))

    private fun toReviewJson(review: MovieReview) = ReviewJson(review.averageScore.value, review.count.value)
}
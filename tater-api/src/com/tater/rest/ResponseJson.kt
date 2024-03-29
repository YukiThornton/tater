package com.tater.rest

data class MovieSummaryJson(val id: String, val title: String)
data class MovieSummariesJson(val movies: List<MovieSummaryJson>)

data class ReviewJson(val average: Double, val count: Int)
data class ReviewedMovieJson(val id: String, val title: String, val watched: Boolean, val review: ReviewJson)
data class ReviewedMovieListJson(val movies: List<ReviewedMovieJson>)

data class LocalizedTextJson(val en: String, val ja: String?)
data class MovieJson(val id: String, val title: LocalizedTextJson, val overview: String, val runtime: Int, val review: ReviewJson)
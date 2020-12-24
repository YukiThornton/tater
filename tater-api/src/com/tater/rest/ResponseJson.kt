package com.tater.rest

data class MovieSummaryJson(val id: String, val title: String)
data class MovieSummariesJson(val movies: List<MovieSummaryJson>)

data class ReviewJson(val average: Double, val count: Int)
data class MovieJson(val id: String, val title: String, val review: ReviewJson)
data class MovieListJson(val movies: List<MovieJson>)

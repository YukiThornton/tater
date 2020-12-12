package com.tater.rest

data class MovieSummaryJson(val id: String, val title: String)
data class MovieSummariesJson(val movies: List<MovieSummaryJson>)
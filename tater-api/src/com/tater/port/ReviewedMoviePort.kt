package com.tater.port

import com.tater.domain.MovieSearchFilter
import com.tater.domain.ReviewedMovies
import com.tater.domain.SortedBy

interface ReviewedMoviePort {
    fun searchMovies(searchFilter: MovieSearchFilter, sort: SortedBy): ReviewedMovies

    class SearchUnavailableException(override val message: String?, override val cause: Throwable?): RuntimeException()
}
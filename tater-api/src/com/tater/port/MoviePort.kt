package com.tater.port

import com.tater.domain.MovieSearchFilter
import com.tater.domain.Movies
import com.tater.domain.SortedBy

interface MoviePort {
    fun searchMovies(searchFilter: MovieSearchFilter, sort: SortedBy): Movies
}
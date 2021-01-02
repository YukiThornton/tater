package com.tater.port

import com.tater.domain.MovieDetails
import com.tater.domain.MovieId

interface MovieDetailPort {
    fun getDetailsOf(movieId: MovieId): MovieDetails
}
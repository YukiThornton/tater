package com.tater.port

import com.tater.domain.LocalizedMovieAttributes
import com.tater.domain.attribute.MovieId

interface LocalizedAttributesPort {
    fun getJapaneseAttributesOf(movieId: MovieId): LocalizedMovieAttributes?
}
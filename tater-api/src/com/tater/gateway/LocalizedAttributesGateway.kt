package com.tater.gateway

import com.tater.domain.LocalizedMovieAttributes
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle
import com.tater.driver.MovieApi
import com.tater.port.LocalizedAttributesPort

class LocalizedAttributesGateway(
        private val movieApi: MovieApi
) : LocalizedAttributesPort {
    override fun getJapaneseAttributesOf(movieId: MovieId): LocalizedMovieAttributes? {
        return movieApi.getMovieTranslations(movieId.value).translations
                .find { it.iso_639_1 == "ja" }
                ?.let { LocalizedMovieAttributes(MovieTitle(it.data.title)) }
    }
}
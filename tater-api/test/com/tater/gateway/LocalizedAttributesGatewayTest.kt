package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.LocalizedMovieAttributes
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle
import com.tater.driver.MovieApi
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LocalizedAttributesGateway")
class LocalizedAttributesGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: LocalizedAttributesGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("getJapaneseAttributesOf")
    inner class GetJapaneseAttributesOfTest {

        @Nested
        @DisplayName("When Japanese attributes exist")
        inner class WhenJapaneseAttributesExist {

            @BeforeEach
            fun setup() {
                every { movieApi.getMovieTranslations("movieId1") } returns MovieApi.TranslationsJson(listOf(
                        MovieApi.TranslationJson("it", MovieApi.TranslatedDataJson("italianTitle1")),
                        MovieApi.TranslationJson("ja", MovieApi.TranslatedDataJson("japaneseTitle1")),
                        MovieApi.TranslationJson("ko", MovieApi.TranslatedDataJson("koreanTitle1"))
                ))
            }

            @Test
            fun `Calls movie api once and returns a Japanese LocalizedMovieAttributes`() {
                val actual = sut.getJapaneseAttributesOf(MovieId("movieId1"))

                verify(exactly = 1) { movieApi.getMovieTranslations("movieId1") }
                actual shouldBeEqualTo LocalizedMovieAttributes(MovieTitle("japaneseTitle1"))
            }

        }

        @Nested
        @DisplayName("When Japanese attributes do not exist")
        inner class WhenJapaneseAttributesDoNotExist {

            @BeforeEach
            fun setup() {
                every { movieApi.getMovieTranslations("movieId1") } returns MovieApi.TranslationsJson(listOf(
                        MovieApi.TranslationJson("it", MovieApi.TranslatedDataJson("italianTitle1")),
                        MovieApi.TranslationJson("ko", MovieApi.TranslatedDataJson("koreanTitle1"))
                ))
            }

            @Test
            fun `Returns null`() {
                val actual = sut.getJapaneseAttributesOf(MovieId("movieId1"))

                actual shouldBeEqualTo null
            }

        }
    }
}
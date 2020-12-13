package project.tater_e2e.helper

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*

class MovieApiMock {

    private val wireMock = WireMock(Configurations.wiremockHost, Configurations.wiremockPort)

    fun clearAll() {
        wireMock.resetMappings()
    }

    fun clearAllRequests() {
        wireMock.resetRequests()
    }

    fun returnsMovieDetailsWhenMovieIdIs(movieId: String) {
        wireMock.register(get(urlPathEqualTo("/3/movie/$movieId"))
                .withQueryParam("api_key", equalTo(Configurations.movieApiToken))
                .willReturn(okJson(JsonReader.read("/project/tater_e2e/movie-api/responses/get-movie/$movieId.json"))
        ))
    }

    fun receivedARequestForMovieDetailsOf(movieId: String) {
        wireMock.verifyThat(getRequestedFor(urlPathEqualTo("/3/movie/$movieId"))
            .withQueryParam("api_key", equalTo(Configurations.movieApiToken))
            .withHeader("Content-Type", equalTo("application/json")))
    }
}
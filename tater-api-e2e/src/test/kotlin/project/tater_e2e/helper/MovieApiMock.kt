package project.tater_e2e.helper

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import project.tater_e2e.helper.Configurations
import project.tater_e2e.helper.JsonReader

class MovieApiMock {

    private val wireMock = WireMock(Configurations.wiremockHost, Configurations.wiremockPort)

    fun clearAll() {
        wireMock.resetMappings()
    }

    fun clearAllRequests() {
        wireMock.resetRequests()
    }

    fun returnsMovieDetailsWhenMovieIdIs(movieId: String) {
        wireMock.register(get("/3/movie/$movieId").willReturn(
            okJson(JsonReader.read("/project/tater_e2e/movie-api/responses/get-movie/$movieId.json"))
        ))
    }

    fun receivedARequestForMovieDetailsOf(movieId: String) {
        wireMock.verifyThat(getRequestedFor(urlEqualTo("/3/movie/$movieId"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer ${Configurations.movieApiToken}")))
    }
}
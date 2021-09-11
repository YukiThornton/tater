package project.tater_web_e2e

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.thoughtworks.gauge.Step

class MovieApi {

    private val mock = WireMock(Configurations.movieApiHost, Configurations.movieApiPort)

    @Step("MovieApiが<listName>の映画一覧を返す状態になっている")
    fun createStubForMovieList(listName: String) {
        mock.register(WireMock.get(WireMock.urlPathEqualTo("/3/discover/movie"))
                .withQueryParam("api_key", WireMock.equalTo(Configurations.movieApiToken))
                .withQueryParam("page", WireMock.equalTo("1"))
                .willReturn(aResponse()
                        .withBody(JsonReader.read("/project/tater_web_e2e/movie-api/$listName.json"))
                        .withHeader("Content-Type", "application/json")))
    }

    fun clearAllMocks() {
        mock.resetMappings()
        mock.resetRequests()
    }
}
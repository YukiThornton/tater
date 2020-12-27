package project.tater_e2e.helper

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*

class MovieApiMock {

    companion object {
        private const val responseBasePath = "/project/tater_e2e/movie-api/responses"
    }

    private val wireMock = WireMock(Configurations.wiremockHost, Configurations.wiremockPort)

    fun clearAll() {
        wireMock.resetMappings()
    }

    fun clearAllRequests() {
        wireMock.resetRequests()
    }

    fun returnsMovieDetailsWhenMovieIdIs(movieId: String) {
        wireMock.register(requestForMovieDetailsFor(movieId)
                .willReturn(okJson(JsonReader.readFile("$responseBasePath/get-movie/$movieId.json"))
        ))
    }

    fun failsWithNotFoundForMovieDetailsWhenMovieIdIs(movieId: String) {
        wireMock.register(requestForMovieDetailsFor(movieId)
                .willReturn(notFound()))
    }

    fun failsWithServerErrorForMovieDetailsWhenMovieIdIs(movieId: String) {
        wireMock.register(requestForMovieDetailsFor(movieId)
                .willReturn(serverError()))
    }

    private fun requestForMovieDetailsFor(movieId: String): MappingBuilder {
        return get(urlPathEqualTo("/3/movie/$movieId"))
                .withQueryParam("api_key", equalTo(Configurations.movieApiToken))
    }

    fun receivedARequestForMovieDetailsOf(movieId: String) {
        wireMock.verifyThat(getRequestedFor(urlPathEqualTo("/3/movie/$movieId"))
            .withQueryParam("api_key", equalTo(Configurations.movieApiToken)))
    }

    fun didNotReceiveAnyRequests() {
        wireMock.verifyThat(0, anyRequestedFor(anyUrl()))
    }

    fun returnsDiscoveredMoviesOfPage(page: Int) {
        wireMock.register(requestForMovieDiscovery(page)
                .willReturn(okJson(JsonReader.readFile("$responseBasePath/movie-discovery/$page.json"))
                ))
    }

    fun failsWithServerErrorForMovieDiscoveryOfPage(page: Int) {
        wireMock.register(requestForMovieDiscovery(page)
                .willReturn(serverError()))
    }

    private fun requestForMovieDiscovery(page: Int): MappingBuilder {
        return get(urlPathEqualTo("/3/discover/movie"))
                .withQueryParam("api_key", equalTo(Configurations.movieApiToken))
                .withQueryParam("page", equalTo(page.toString()))
    }

    fun receivedARequestForMovieDiscoveryOfPage(page: Int) {
        wireMock.verifyThat(getRequestedFor(urlPathEqualTo("/3/discover/movie"))
                .withQueryParam("api_key", equalTo(Configurations.movieApiToken))
                .withQueryParam("page", equalTo(page.toString()))
                .withQueryParam("sort_by", equalTo("vote_average.desc"))
                .withQueryParam("include_adult", equalTo("false"))
                .withQueryParam("include_video", equalTo("false"))
                .withQueryParam("vote_count.gte", matching("^\\d+\$")))
    }

}
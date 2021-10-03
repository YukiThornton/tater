import { Movie } from "@domains/Movie";
import { Review } from "@domains/Review";
import type { UserId } from "@domains/User";
import type { MovieJson, ReviewJson } from "@drivers/ApiClient";
import type ApiClient from "@drivers/ApiClient";

export default class MovieRepository {

    constructor(
        private apiClient: ApiClient
    ) {}

    async getTopRated(userId: UserId): Promise<Movie[]> {
        const json = await this.apiClient.getV1TopRated(userId.id);
        return json.movies.map(movie => this.createMovie(movie))
    }

    private createMovie(json: MovieJson) {
        return new Movie(json.id, json.title, this.createAverage(json.review))
    }

    private createAverage(json: ReviewJson) {
        return new Review(json.average)
    }
}
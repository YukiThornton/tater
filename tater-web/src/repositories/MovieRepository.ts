import { Movie } from "@domains/Movie";
import type { UserId } from "@domains/User";
import type ApiClient from "@drivers/ApiClient";

export default class MovieRepository {

    constructor(
        private apiClient: ApiClient
    ) {}

    async getTopRated(userId: UserId): Promise<Movie[]> {
        const json = await this.apiClient.getV1TopRated(userId.id);
        return json.movies.map(movie => new Movie(movie.id, movie.title))
    }
}
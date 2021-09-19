import { Movie } from "src/domains/Movie";
import type { UserId } from "src/domains/User";
import type ApiClient from "src/drivers/ApiClient";

export default class MovieRepository {

    constructor(
        private apiClient: ApiClient
    ) {}

    async getTopRated(userId: UserId): Promise<Movie[]> {
        const json = await this.apiClient.getV1TopRated(userId.id);
        return json.movies.map(movie => new Movie(movie.id, movie.title))
    }
}
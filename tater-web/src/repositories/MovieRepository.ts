import { Movie } from "src/domains/Movie";
import type { UserId } from "src/domains/User";
import ApiClient from "src/drivers/ApiClient";

export default class MovieRepository {
    async getTopRated(userId: UserId): Promise<Movie[]> {
        const client = new ApiClient();
        
        const json = await client.getV1TopRated(userId.id);
        return json.movies.map(movie => new Movie(movie.id, movie.title))
    }
}
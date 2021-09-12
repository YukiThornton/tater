import { Movie } from "src/domains/Movie";
import type { UserId } from "src/domains/User";

export default class MovieRepository {
    async getTopRated(userId: UserId): Promise<Movie[]> {
        const response = await fetch(process.env.apiUrl + '/v1/top-rated', {
            headers: { 'tater-user-id': userId.id }
        })
        const json = await response.json()
        return json.movies.map(movie => new Movie(movie.id, movie.title))
    }
}
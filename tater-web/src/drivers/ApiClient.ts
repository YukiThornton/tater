export default class ApiClient {
    async getV1TopRated(userId: string): Promise<MoviesJson> {
        const response = await fetch(process.env.apiUrl + '/v1/top-rated', {
            headers: { 'tater-user-id': userId }
        })
        return response.json()
    }
}

export interface MovieJson {
    id: string,
    title: string,
}

export interface MoviesJson {
    movies: MovieJson[]
}
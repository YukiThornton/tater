import type { Writable } from 'svelte/store';
import type { Movie } from '@domains/Movie';
import type { MovieViewModel } from '@stores/movie';

export default class MoviePresenter {

    constructor(private movieStore: Writable<MovieViewModel[]>) {}

    setMovies(movies: Movie[]) {
        const models: MovieViewModel[] = movies.map(this.createMovieViewModel)
        this.movieStore.set(models)
    }

    private createMovieViewModel(movie: Movie): MovieViewModel {
        return {
            title: movie.title,
            reviewAverage: movie.reviewAverageWithFullScore()
        }
    }
}
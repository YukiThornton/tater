import type { Movie } from 'src/domains/Movie';
import type { MovieViewModel } from '@stores/movie';
import type { Writable } from 'svelte/store';

export default class MoviePresenter {
    
    constructor(private movieStore: Writable<MovieViewModel[]>) {}
    
    setMovies(movies: Movie[]) {
        const models = movies.map(movie => ({title: movie.title} as MovieViewModel))
        this.movieStore.set(models)
    }
}
import type { Movie } from 'src/domains/Movie';
import { movies as movieStore, MovieViewModel } from '@stores/movie';

export default class MoviePresenter {
    setMovies(movies: Movie[]) {
        const models = movies.map(movie => ({title: movie.title} as MovieViewModel))
        movieStore.set(models)
    }
}
import type MovieListUseCase from '@usecases/MovieListUseCase';

export default class MovieListController {

    constructor(private movieListUseCase: MovieListUseCase){}

    async loadMovies() {
        this.movieListUseCase.loadMovies()
    }
}
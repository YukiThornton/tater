import type MovieListUseCase from 'src/usecases/MovieListUseCase';

export default class MovieListController {

    constructor(private movieListUseCase: MovieListUseCase){}

    async loadMovies() {
        this.movieListUseCase.loadMovies()
    }
}
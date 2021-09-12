import MovieListUseCase from 'src/usecases/MovieListUseCase';

export default class MovieListController {
    async loadMovies() {
        new MovieListUseCase().loadMovies()
    }
}
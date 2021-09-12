import MoviePresenter from 'src/presenters/MoviePresenter';
import UserPresenter from 'src/presenters/UserPresenter';
import MovieRepository from 'src/repositories/MovieRepository';

export default class MovieListUseCase {
    async loadMovies() {
        const userRepo = new UserPresenter()
        const movieRepo = new MovieRepository()
        const moviePresenter = new MoviePresenter()

        const m = await movieRepo.getTopRated(userRepo.getUserId())
        moviePresenter.setMovies(m)
    }
}
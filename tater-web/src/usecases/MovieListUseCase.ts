import type MoviePresenter from 'src/presenters/MoviePresenter';
import type UserPresenter from 'src/presenters/UserPresenter';
import type MovieRepository from 'src/repositories/MovieRepository';

export default class MovieListUseCase {
    
    constructor(
        private movieRepo: MovieRepository,
        private moviePresenter: MoviePresenter,
        private userPresenter: UserPresenter,
    ) {}

    async loadMovies() {
        const m = await this.movieRepo.getTopRated(this.userPresenter.getUserId())
        this.moviePresenter.setMovies(m)
    }
}
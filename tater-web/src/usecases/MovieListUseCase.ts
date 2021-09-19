import type MoviePresenter from '@presenters/MoviePresenter';
import type UserPresenter from '@presenters/UserPresenter';
import type MovieRepository from '@repositories/MovieRepository';

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
import type PagePresenter from "src/presenters/PagePresenter";

export default class AppInitUseCase {

    constructor(private pagePresenter: PagePresenter) {}

    initializePages() {
        this.pagePresenter.initializePages();
    }
}
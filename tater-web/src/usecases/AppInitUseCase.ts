import PagePresenter from "src/presenters/PagePresenter";

export default class AppInitUseCase {
    private pagePresenter = new PagePresenter();
    initializePages() {
        this.pagePresenter.initializePages();
    }
}
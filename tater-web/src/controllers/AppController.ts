import AppInitUseCase from "src/usecases/AppInitUseCase";

export default class AppController {
    
    private appInitUseCase = new AppInitUseCase()
    initializePages() {
        this.appInitUseCase.initializePages()
    }
}
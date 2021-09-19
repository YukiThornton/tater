import type AppInitUseCase from "src/usecases/AppInitUseCase";

export default class AppController {
    
    constructor(private  appInitUseCase: AppInitUseCase){}

    initializePages() {
        this.appInitUseCase.initializePages()
    }
}
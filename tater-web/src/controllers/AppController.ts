import type AppInitUseCase from "@usecases/AppInitUseCase";

export default class AppController {
    
    constructor(private  appInitUseCase: AppInitUseCase){}

    initializePages() {
        this.appInitUseCase.initializePages()
    }
}
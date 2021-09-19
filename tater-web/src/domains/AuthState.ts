export default class AuthState {
    constructor(readonly authenticated: boolean) {}
    
    allPagesAreAuthorized(): boolean {
        return this.authenticated;
    }
}
import AuthState from 'src/domains/AuthState';
import { get, Writable } from 'svelte/store'

export default class AuthPresenter {

    constructor(private authenticatedStore: Writable<boolean>) {}

    setAuthState(state: AuthState) {
        this.authenticatedStore.set(state.authenticated);
    }
    
    getAuthState(): AuthState {
        return new AuthState(get(this.authenticatedStore));
    }
}
import { get, Writable } from 'svelte/store';
import AuthState from '@domains/AuthState';

export default class AuthPresenter {

    constructor(private authenticatedStore: Writable<boolean>) {}

    setAuthState(state: AuthState) {
        this.authenticatedStore.set(state.authenticated);
    }
    
    getAuthState(): AuthState {
        return new AuthState(get(this.authenticatedStore));
    }
}
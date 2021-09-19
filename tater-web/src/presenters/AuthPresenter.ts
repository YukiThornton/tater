import { authenticated as authenticatedStore } from '@stores/auth'
import AuthState from 'src/domains/AuthState';
import { get } from 'svelte/store'

export default class AuthPresenter {
    setAuthState(state: AuthState) {
        authenticatedStore.set(state.authenticated);
    }
    
    getAuthState(): AuthState {
        return new AuthState(get(authenticatedStore));
    }
}
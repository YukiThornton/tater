import { writable } from 'svelte/store'
import type { RouteModel } from './RouteModel';

export const login = writable<RouteModel>();
export const top = writable<RouteModel>();
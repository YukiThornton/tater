import { writable } from "svelte/store";

export interface MovieViewModel {
    title: string
}

export const movies = writable<MovieViewModel[]>();
import { writable } from "svelte/store";

export interface MovieViewModel {
    readonly title: string,
    readonly reviewAverage: string,
}

export const movies = writable<MovieViewModel[]>();
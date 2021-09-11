import { writable } from "svelte/store";

export interface StoredMovie {
    title: string
}

export const movies = writable<StoredMovie[]>();
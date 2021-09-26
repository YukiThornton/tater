<script lang="ts">
    import type { MovieViewModel } from "@stores/movie";
    import MovieSummary from '@molecules/MovieSummary.svelte'

    export let movies: MovieViewModel[]
</script>

{#each movies as movie}
    <MovieSummary {movie} />
{/each}

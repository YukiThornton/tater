<script lang="ts">
    import type { MovieViewModel } from "@stores/movie";
    import MovieSummary from '@molecules/MovieSummary.svelte'

    export let movies: MovieViewModel[]
</script>

<div>
    {#each movies as movie}
        <MovieSummary {movie} />
    {/each}
</div>

<style lang="scss">
    div {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 20px;
        place-items: center;
        padding: 20px;
    }
</style>
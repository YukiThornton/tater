<script lang="ts">
    import { onMount } from 'svelte';
    import { userId } from '@stores/store';
    import { movies } from '@stores/movie';
    
    onMount(async () => {
        const response = await fetch(process.env.apiUrl + '/v1/top-rated', {
            headers: { 'tater-user-id': $userId.toString() }
        })
        const json = await response.json()
        movies.set(json.movies.map(movie => ({title: movie.title})))
    })
</script>

<header>
    <div data-tater-app-name>Tater</div>
    <div data-tater-user-id>{$userId}</div>
</header>
<main>
    {#if $movies}
        {#each $movies as movie}
            <div data-tater-movie>
                <h2 data-tater-title>{movie.title}</h2>
            </div>
        {/each}
    {/if}
</main>

<style>
</style>
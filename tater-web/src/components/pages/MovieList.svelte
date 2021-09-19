<script lang="ts">
    import { onMount } from 'svelte';
    import { userId } from '@stores/user';
    import { movies } from '@stores/movie';
    import { movieListController } from '@container';
    
    onMount(async () => {
        movieListController.loadMovies()
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
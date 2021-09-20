<script lang="ts">
    import { onMount } from 'svelte';
    import { userId } from '@stores/user';
    import { movies } from '@stores/movie';
    import { movieListController } from '@container';
    import Header from '@organisms/Header.svelte'
    
    onMount(async () => {
        movieListController.loadMovies()
    })
</script>

<div class="movie-list">
    <Header userId={$userId}/>
    <main>
        {#if $movies}
            {#each $movies as movie}
                <div data-tater-movie>
                    <h2 data-tater-title>{movie.title}</h2>
                </div>
            {/each}
        {/if}
    </main>
</div>

<style lang="scss">
    .movie-list {
        background-color: #252525;
    }
</style>
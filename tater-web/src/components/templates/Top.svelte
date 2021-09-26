<script lang="ts">
    import type { MovieViewModel } from '@stores/movie';
    import Header from '@organisms/Header.svelte';
    import MovieList from '@organisms/MovieList.svelte';
    
    export let userId: string
    export let movies: MovieViewModel[]
</script>

<div class="movie-list">
    <Header userId={userId}/>
    <main>
        {#if movies}
            <MovieList movies={movies}/>
        {/if}
    </main>
</div>

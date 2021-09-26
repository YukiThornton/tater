<script lang="ts">
    import { onMount } from 'svelte';
    import { userId } from '@stores/user';
    import { movies } from '@stores/movie';
    import { movieListController } from '@container';
    import TopTemplate from '@templates/Top.svelte';
    
    onMount(async () => {
        movieListController.loadMovies()
    })
</script>

<TopTemplate userId={$userId} movies={$movies} />

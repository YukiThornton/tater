<script lang="ts">
    import type { MovieViewModel } from "@stores/movie";

    export let movie: MovieViewModel
</script>

<div data-tater-movie>
    <h2 data-tater-title>{movie.title}</h2>
    <span data-tater-rating>{movie.reviewAverage}</span>
</div>
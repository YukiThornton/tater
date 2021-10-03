<script lang="ts">
    import type { MovieViewModel } from "@stores/movie";
    import Text from '@atoms/Text.svelte';

    export let movie: MovieViewModel
</script>

<div data-tater-movie>
    <span>
        <Text text={movie.title} light="true" attr={{'data-tater-title': true}}></Text>
        <Text text={movie.reviewAverage} light="true" attr={{'data-tater-rating': true}}></Text>
    </span>
</div>

<style lang="scss">
    div {
        width: 240px;
        height: 300px;
        background-color: #464646;
        display: flex;
        align-items: end;
        justify-content: end;
        padding: 10px;

        span {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            text-align: end;
        }
    }
</style>
<script lang="ts">
    export let attr: {[key: string]: string|number|boolean} = {};
</script>

<button on:click {...attr} ><slot /></button>

<style lang="scss">
    button {
        background-color: $g-bg-color;
        font-family: $g-primary-font-family;
        border: 2px solid $g-primary-text-color;
        cursor: pointer;
        
        &:hover {
            border: 2px solid;
            border-image-slice: 1;
            border-image-source: linear-gradient(to right, #ea00d9,#9550e0, #0abdc6, #3479dc);
        }
    }
</style>
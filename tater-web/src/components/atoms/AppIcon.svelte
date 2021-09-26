<script lang="ts">
    export let large: boolean = false;
</script>

<span class="logo" class:large data-tater-app-name>Tater</span>

<style lang="scss">
    .logo {
        font-family: 'Sigmar One', cursive;
        font-size: 3em;
        background: linear-gradient(to right, #ea00d9,#9550e0, #0abdc6, #3479dc);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        text-shadow: 3px 3px 1px;
    }
    
    .large {
        font-size: 6em;
    }
</style>
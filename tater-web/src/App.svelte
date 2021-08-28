<script lang="ts">
    import { Router, Route } from 'svelte-navigator';
    import Login from "./components/pages/Login.svelte";
    import MovieList from "./components/pages/MovieList.svelte";
</script>

<Router>
    <Route path="/" primary="{false}"><MovieList /></Route>
    <Route path="/login" primary="{false}"><Login /></Route>
</Router>

<style>
</style>
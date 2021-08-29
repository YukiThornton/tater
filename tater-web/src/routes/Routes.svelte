<script lang="ts">
    import { Router, Route } from 'svelte-navigator';
    import AuthRoute from './AuthRoute.svelte';
    import Login from "../components/pages/Login.svelte";
    import MovieList from "../components/pages/MovieList.svelte";
</script>

<Router primary={false}>
    <AuthRoute path="/"><MovieList /></AuthRoute>
    <Route path="/login"><Login /></Route>
</Router>

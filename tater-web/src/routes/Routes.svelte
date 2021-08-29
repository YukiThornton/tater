<script lang="ts">
    import { Router, Route } from 'svelte-navigator';
    import AuthRoute from '@routes/AuthRoute.svelte';
    import Login from '@pages/Login.svelte';
    import MovieList from '@pages/MovieList.svelte';
</script>

<Router primary={false}>
    <AuthRoute path="/"><MovieList /></AuthRoute>
    <Route path="/login"><Login /></Route>
</Router>

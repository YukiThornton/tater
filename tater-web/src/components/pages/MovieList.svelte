<script lang="ts">
    import { userId } from '../../store';
    
    let userIdValue;
    userId.subscribe(value => userIdValue = value);
</script>

<header>
    <div data-tater-app-name>Tater</div>
    <div data-tater-user-id>{userIdValue}</div>
</header>
<main>
    list
</main>

<style>
</style>
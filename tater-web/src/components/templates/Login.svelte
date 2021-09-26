<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import Button from '@atoms/Button.svelte';
    import Text from '@atoms/Text.svelte';
    import AppIcon from '@atoms/AppIcon.svelte';
    import LabeledInput from '@molecules/LabeledInput.svelte';

    const dispatch = createEventDispatcher()
    
    export let userId: string;
    export let focusRequired: string;
    export let errorMessage: string;

    function login() {
        dispatch('login')
    }
</script>

<main>
    <div class="icon">
        <AppIcon large={true}/>
    </div>
    <div class="form">
        <LabeledInput
            bind:value={userId}
            id="userId"
            bind:focus={focusRequired}
            bind:errorMessage={errorMessage}
            on:input
        />
        <Button on:click={login} attr={{'data-tater-login-button': ''}}>
            <Text text="Login" light={true}/>
        </Button>
    </div>
</main>

<style lang="scss">
    main {
        display: grid;
        grid-template-rows: 30vh 30vh 40vh;
        justify-items: center;
        align-items: center;
        
        .icon {
            align-self: end;
            border-bottom: 2px dashed $g-primary-text-color;
        }
        
        .form {
            display: grid;
            gap: 4vh;
        }
    }
</style>
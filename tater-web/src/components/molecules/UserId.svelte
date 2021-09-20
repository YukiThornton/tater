<script lang="ts">
    import Text from '@atoms/Text.svelte';
    export let userId: string;
    const attr = {"data-tater-user-id": ""}
</script>

<Text text="User ID: {userId}" {attr} />

<template>
    <div class="alert-container">
        <v-alert
                closable
                :icon="icon"
                variant="tonal"
                :color="color"
        >
            {{ message }}
        </v-alert>
    </div>
</template>

<script setup>
const { icon, color, message } = defineProps(['icon', 'color', 'message']);
</script>

<style scoped lang="scss">

</style>
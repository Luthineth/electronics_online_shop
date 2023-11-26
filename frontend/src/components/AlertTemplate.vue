<template>
    <v-alert
        closable
        :icon="`${icon}`"
        :color="`${color}-lighten-2`"
    >
        {{ message }}
    </v-alert>
</template>

<script setup>
const { icon, color, message } = defineProps(['icon', 'color', 'message']);
</script>
<template>
    <v-btn
            variant="plain"
            class="back-to-top"
            icon="mdi-arrow-up"
            @click="scrollToTop"
    ></v-btn>
</template>

<script setup>
import {scrollToTop} from "../utils/utils";
</script>

<style scoped lang="scss">
.back-to-top {
    position: fixed;
    bottom: 20px;
    right: 20px;
    cursor: pointer;
    z-index: 3;
}
</style>
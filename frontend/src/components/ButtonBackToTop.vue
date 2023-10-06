<template>
    <v-btn
            variant="plain"
            class="back-to-top"
            icon="mdi-arrow-up"
            @click="scrollToTop"
    ></v-btn>
</template>

<script setup>
const scrollToTop = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
};
</script>

<style scoped lang="scss">
.back-to-top {
  position: fixed;
  bottom: 20px;
  right: 20px;
  cursor: pointer;
}
</style>
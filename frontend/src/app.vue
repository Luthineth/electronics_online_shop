<template>
    <div style="min-height: 100vh">
        <NavBar />
        <router-view />
        <ButtonBackToTop />
    </div>
    <MainFooter />
</template>

<script setup>
import NavBar from "./components/NavBar.vue";
import ButtonBackToTop from "./components/ButtonBackToTop.vue";
import MainFooter from "./components/MainFooter.vue";
</script>

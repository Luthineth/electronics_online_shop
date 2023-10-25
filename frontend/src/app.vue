<template>
    <NavBar/>
    <router-view/>
    <ButtonBackToTop/>
</template>

<script setup>
import NavBar from "./components/NavBar.vue";
import ButtonBackToTop from "./components/ButtonBackToTop.vue";
</script>

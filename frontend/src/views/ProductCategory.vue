<template>
    <ProductList
            v-if="products"
            :products="products"
    />
</template>

<script setup>
import {onMounted, ref} from "vue"
import ProductList from "../components/ProductList.vue";
import router from "../router/router";

const categoryId = router.currentRoute.value.params.id
const isFetchError = ref(false);
const products = ref([])

onMounted(async () => {
    products.value = await fetch(`http://localhost:8080/products_category/${categoryId}`)
        .then(res => res.json())
});

onMounted(async () => {
    try {
        const response = await fetch(`http://localhost:8080/products_category/${categoryId}`);

        if (!response.ok) {
            isFetchError.value = true;
        } else {
            products.value = await response.json();
        }
    } catch (error) {
        isFetchError.value = true;
    }

    if (isFetchError.value) {
        await router.push('/404');
    }
});
</script>

<style scoped lang="scss">

</style>
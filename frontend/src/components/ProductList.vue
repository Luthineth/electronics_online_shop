<template>
    <div class="product-list">
        <ProductCard
                v-for="product in products"
                :key="product.id"
                :product="product"
        />
    </div>
</template>

<script setup>
import ProductCard from "./ProductCard.vue";

const { products } = defineProps({
    products: Array
})
</script>

<style scoped lang="scss">
.product-list {
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 2rem;
}

</style>

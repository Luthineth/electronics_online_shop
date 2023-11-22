<template>
    <div class="product-list">
        <OrderItem
                v-for="orderItem in orderItems"
                :key="orderItem.productId"
                :product="orderItem.product"
                :quantity="orderItem.quantity"
                :productId="orderItem.productId"
        />
    </div>
</template>

<script setup>
import OrderItem from "./OrderItem.vue";

const { orderItems } = defineProps({
    orderItems: Array
})
</script>

<style scoped lang="scss">
.product-list {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 2rem;
}
</style>

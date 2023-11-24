<template>
    <div class="product">
        <v-card
                class="product__info"
                variant="outlined"
                width="100%"
        >
            <v-img
                    class="product__picture"
                    :src="orderItemImage"
                    v-on:error="orderItemImage = '../../../public/no_img.png'"
            />

            <div class="product__description">
                <h3>{{ productName }}</h3>
            </div>

            <div class="product__availability">
                <span v-if="addToCartError">
                    Нельзя добавить
                </span>
                <v-card-actions class="d-flex align-content-center">
                    <v-btn
                        class="font-weight-bold"
                        @click="decreaseOrderItemQuantity()"
                    >
                        -
                    </v-btn>
                    {{quantityRef}}
                    <v-btn
                        class="font-weight-bold"
                        @click="increaseOrderItemQuantity()"
                    >
                        +
                    </v-btn>
                </v-card-actions>

                <v-btn
                    variant="text"
                    color="red"
                    @click="removeOrderItem()"
                >
                    Удалить
                </v-btn>
            </div>
            <div class="product__controls">
                <div class="product__controls__price">
                    <h4 v-if="price === priceWithDiscount">
                        {{ price }}₽
                    </h4>
                    <div
                            v-else
                            class="price-updated"
                    >
                        <b>{{ priceWithDiscount }}₽</b>
                    </div>
                </div>
            </div>
        </v-card>
    </div>
</template>

<script setup>
import {ref} from "vue";
import {getImage} from "../../utils/utils";
import {cartItemCount} from "../../utils/variables";
import store from "../../stores/store";
import {productsBackendUrl} from "../../utils/urls";

const { product, quantity, productId } = defineProps(['product', 'quantity', 'productId']);
const {
    productName,
    description,
    price,
    priceWithDiscount,
    imageUrl,
} = product

const orderItemImage = ref(getImage('products', imageUrl))
let quantityRef = ref(quantity)
let addToCartError = ref(false)

const increaseOrderItemQuantity = async () => {
    let currentStockQuantity = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.stockQuantity)
    if (quantityRef.value < currentStockQuantity) {
        const orderItem = {
            product: product,
            quantity: 1,
            productId: productId,
        };
        cartItemCount.value += 1
        quantityRef.value += 1
        store.commit('addToCart', orderItem);
    } else {
        addToCartError.value = true
        setTimeout(() => {
            addToCartError.value = false;
        }, 1500);
    }
};

const decreaseOrderItemQuantity = async () => {
    if (quantityRef.value === 1) {
        await removeOrderItem()
    } else {
        quantityRef.value -= 1
        const orderItem = {
            product: product,
            quantity: parseInt(quantityRef.value),
            productId: productId,
        };
        cartItemCount.value -= 1
        store.commit('editCart', orderItem);
    }
};

const removeOrderItem = async () => {
    const orderItem = {
        product: product,
        quantity: parseInt(quantityRef.value),
        productId: productId,
    };
    cartItemCount.value -= parseInt(quantityRef.value)
    store.commit('removeFromCart', orderItem);
}
</script>

<style scoped lang="scss">
.product{
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}
.product__info{
  padding: 10px 20px;
  width: 100%;
  max-width: 1500px;
  display: flex;
  align-items: center;
  gap: 1rem;
}
.product__picture{
    width: 20%;
    max-height: 150px;
}
.product__description{
    width: 40%;
}
.product__availability{
    width: 30%;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.quantity{
    width: 30px;
}
.product__controls {
  width: 10%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.old-price{
  color: dimgray;
  margin-left: 5px;
}
</style>
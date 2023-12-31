<template>
    <div class="alert-container">
        <AlertTemplate
            v-if="addToCartError"
            :color="'red'"
            :icon="'mdi-alert-circle-outline'"
            :message="'Товар уже раскупили:('"
        />
    </div>

    <div
        class="product"
        v-if="product.length !== 0"
    >
        <div class="product__info">
            <div class="product__image">
                <v-img
                    :src="productImage"
                    v-on:error="productImage = '../../public/no_img.png'"
                />
            </div>

            <div class="product__details">
                <div>
                    <h1>{{ product.productName }}</h1>
                    <p>{{product.description}}</p>
                </div>

                <div class="description-price mt-2">
                    <div v-if="userAuthorized">
                        <h1 v-if="product.price === product.priceWithDiscount">
                            {{ product.price }} RUB
                        </h1>

                        <div v-else>
                            <h2>
                                <b>{{ product.priceWithDiscount }} RUB</b>
                            </h2>

                            <h3>
                                <s class="old-price">{{ product.price }} RUB</s>
                            </h3>
                        </div>
                    </div>

                    <h1 v-else>
                        {{ product.price }} RUB
                    </h1>
                </div>

                <div class="mt-2 d-flex align-center">
                    <v-btn
                        color="green"
                        variant="tonal"
                        @click="addToCart()"
                        :disabled="product.stockQuantity === 0"
                    >
                        Добавить в корзину
                    </v-btn>

                    <span class="ml-1">В наличии: {{ product.stockQuantity }}</span>
                </div>
            </div>
        </div>

        <div class="product__comments">
            <Comments
                v-if="product.comments"
                :comments="product.comments"
                :productId="productId"
            />
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref} from "vue"
import router from "../router/router";
import Comments from "../components/Comments.vue";
import {getImage} from "../utils/utils";
import {cartItemCount, userAuthorized} from "../utils/variables.js";
import store from "../stores/store";
import {productsBackendUrl} from "../utils/urls";
import AlertTemplate from "../components/AlertTemplate.vue";

const productId = router.currentRoute.value.params.id
const productImage = ref(null)
const isFetchError = ref(false);
let addToCartError = ref(false)
let product = ref([])

onMounted(async () => {
    try {
        const response = await fetch(productsBackendUrl + `/${productId}`);

        if (!response.ok) {
            isFetchError.value = true;
        } else {
            product.value = await response.json();
            productImage.value = getImage('products', product.value.imageUrl)
        }
    } catch (error) {
        isFetchError.value = true;
    }

    if (isFetchError.value) {
        await router.push('/404');
    }
});

const addToCart = async () => {
    let currentStockQuantity = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.stockQuantity)
    if (currentStockQuantity !== 0) {
        const orderItem = {
            productId: parseInt(productId),
            product: product,
            quantity: 1,
        };
        cartItemCount.value += 1
        store.commit('addToCart', orderItem);
    } else {
        addToCartError.value = true
        setTimeout(() => {
            addToCartError.value = false;
        }, 1500);
    }
};
</script>

<style scoped lang="scss">
.product {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 50px;
}
.product__info {
    display: flex;
    align-content: center;
    width: 100%;
    max-width: 1390px;
    gap: 20px;
}
.product__image{
    width: 30%;
}
.product__details{
    width: 70%;
}
.old-price{
    color: gray;
}
.product__comments{
    width: 50%;
    max-width: 600px;
}
</style>
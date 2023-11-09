<template>
    <div class="alert-container">
        <v-alert
            closable
            icon="mdi-alert-circle-outline"
            variant="tonal"
            color="error"
            v-if="addToCartError"
        >
            Товар уже раскупили:(
        </v-alert>
    </div>
    <div
        class="product"
        v-if="product.length !== 0"
    >
        <div class="product__info">
            <div class="product__image">
                <v-img
                    :src="getImage(product.imageUrl)"
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
import {cartItemCount, getImage, userAuthorized} from "../utils/utils";
import store from "../stores/store";

const productId = router.currentRoute.value.params.id
const isFetchError = ref(false);
let addToCartError = ref(false)
let product = ref([])

onMounted(async () => {
    try {
        const response = await fetch(`http://localhost:8080/products/${productId}`);

        if (!response.ok) {
            isFetchError.value = true;
        } else {
            product.value = await response.json();
        }
    } catch (error) {
        isFetchError.value = true;
    }

    if (isFetchError.value) {
        await router.push('/404');
    }
});

const addToCart = async () => {
    let currentStockQuantity = await fetch(`http://localhost:8080/products/${productId}`)
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
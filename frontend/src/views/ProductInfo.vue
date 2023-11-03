<template>
    <div class="product" v-if="product.length !== 0">
        <div class="product__info">
            <div class="product__image">
                <v-img src="https://cdn.vuetifyjs.com/images/cards/halcyon.png"></v-img>
            </div>
            <div class="product__details">
                <div class="description-info">
                    <h1>{{ product.productName }}</h1>
                    <p>{{product.description}}</p>
                </div>
                <div class="description-price">
                    <h1 v-if="product.price === product.priceWithDiscount">
                        {{ product.price }} RUB
                    </h1>
                    <div
                        v-else
                        class="price-updated"
                    >
                        <b>{{ product.priceWithDiscount }} RUB</b>
                        <s class="old-price">{{ product.price }} RUB</s>
                    </div>
                </div>


                <div class="description-action-wrapper">
                    <div class="description-button" style="">
                        <p class="header">В наличии: {{ product.stockQuantity }}</p>
                        <v-btn
                            color="green"
                            variant="tonal"
                            @click="addProductToCart()"
                            :disabled="product.stockQuantity === 0"
                        >
                            Добавить в корзину
                        </v-btn>
                    </div>
                </div>

            </div>
        </div>
        <div class="product__comments">
            <Comments
                v-if="product.comments.length !== 0"
                :comments="product.comments"
                :productId="productId"
            />
            <h4 v-else>Никто еще не оставил комментарий к этому товару, будьте первым!</h4>
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref} from "vue"
import router from "../router/router";
import Comments from "../components/Comments.vue";

const productId = router.currentRoute.value.params.id
const isFetchError = ref(false);
const product = ref([])

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
    display: grid;
    width: 100%;
    max-width: 1000px;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
}
.product__image {
    padding: 10px;
}
.product__details {
    padding: 10px;
}
</style>
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
                        <p class="header">В наличии {{ product.stockQuantity }}</p>
                        <v-btn color="red-darken-2" @click="addProductToCart(product)"><v-icon class="me-1" icon="mdi-image-off-outline"></v-icon> Add to cart</v-btn>
                    </div>
                </div>

            </div>
        </div>
        <div class="product__comments">
            <div>
                <v-card
                    class="pa-2 mb-9"
                    variant="outlined"
                >
                    <v-textarea label="Напишите, что вам понравилось в товаре (или не понравилось)"></v-textarea>
                    <v-file-input
                        :rules="rules"
                        accept="image/png, image/jpeg, image/bmp"
                        placeholder="Выберите изображение"
                        prepend-icon="mdi-camera"
                        label="Добавьте фото к отзыву"
                    ></v-file-input>
                    <v-rating
                        :model-value="6"
                        color="yellow-darken-3"
                        size="small"
                    />
                    <v-card-actions class="d-flex justify-center">
                        <v-btn
                            class="me-4"
                            color="black"
                            variant="tonal"
                            type="submit"
                        >
                            <v-icon icon="mdi-check"></v-icon>
                            Сохранить
                        </v-btn>

                        <v-btn @click="handleReset">
                            <v-icon icon="mdi-close"></v-icon>
                            Очистить
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </div>
            <Comments v-if="product.comments.length !== 0" :comments="product.comments"/>
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
let rules = [
    value => {
        return !value || !value.length || value[0].size < 2000000 || 'Avatar size should be less than 2 MB!'
    },
]

onMounted(async () => {
    product.value = await fetch(`http://localhost:8080/products/${productId}`)
        .then(res => res.json())
    console.log(product.value)
});

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
    grid-template-columns: 1fr 1fr; /* Split into two columns */
    gap: 10px; /* Add some space between grid items */
}

/* Additional CSS for styling, you can customize as needed */
.product__image {
    padding: 10px;
}

.product__details {
    padding: 10px;
}
</style>
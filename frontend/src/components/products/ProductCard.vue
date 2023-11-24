<template>
    <div class="alert-container">
        <AlertTemplate
            v-if="addToCartError"
            :color="'red'"
            :icon="'mdi-alert-circle-outline'"
            :message="'Товар уже раскупили:('"
        />
    </div>

    <div class="product">
        <v-card
            class="product__info"
            variant="outlined"
        >
            <v-img
                class="product__picture"
                :src="productImage"
                v-on:error="productImage = '../../../public/no_img.png'"
            />

            <div class="product__description">
                <h3>
                    <router-link
                        :to="'/products/' + productId"
                        @click="scrollToTop"
                    >
                        {{ productName }}
                    </router-link>
                </h3>

                <v-card-text class="description__text">
                    {{ isDescriptionShown? description : descriptionSmall }}
                </v-card-text>

                <v-btn
                    v-if="description.length > 300"
                    class="description__more"
                    :append-icon="isDescriptionShown? 'mdi-chevron-up' : 'mdi-chevron-down'"
                    variant="plain"
                    density="compact"
                    size="small"
                    @click="isDescriptionShown = !isDescriptionShown"
                >
                    {{ isDescriptionShown? 'Скрыть описание' : 'Показать больше' }}
                </v-btn>
            </div>

            <div class="product__availability">
                <h4>{{ getProductStockStatus(stockQuantity) }}</h4>
            </div>

            <div class="product__controls">
                <div class="product__controls__price">
                    <h4 v-if="price === priceWithDiscount || !userAuthorized">
                        {{ price }}₽
                    </h4>

                    <div
                        v-else
                        class="price-updated"
                    >
                        <b>{{ priceWithDiscount }}₽</b>
                        <s class="old-price">{{ price }}₽</s>
                    </div>
                </div>
                <v-btn
                    variant="tonal"
                    color="green"
                    :disabled="stockQuantity === 0"
                    @click="addToCart()"
                >
                    в корзину
                </v-btn>
            </div>
        </v-card>
        <div
            class="product__admin-actions"
            v-if="admin"
        >
            <v-btn variant="text">
                Редактировать
                <ProductEdit :old-product-info="product" />
            </v-btn>

            <v-btn
                variant="outlined"
                color="red"
                @click="deleteProduct()"
            >
                Удалить
            </v-btn>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
import store from "../../stores/store";
import {getImage, scrollToTop} from "../../utils/utils";
import {cartItemCount, userAuthorized, userRole} from "../../utils/variables";
import ProductEdit from "./ProductEdit.vue";
import axios from "axios";
import {productsBackendUrl} from "../../utils/urls";
import AlertTemplate from "../AlertTemplate.vue";

const { product } = defineProps(['product']);
const {
    productName,
    productId,
    description,
    stockQuantity,
    price,
    priceWithDiscount,
    imageUrl,
} = product

const productImage = ref(getImage('products', imageUrl))
let admin = userRole.value === 'ADMIN';
const addToCartError = ref(false)
const isDescriptionShown = ref(false)
const descriptionSmall = description.length <= 300 ? description : description.slice(0, 300) + "..."

function getProductStockStatus(stockQuantity) {
    if (typeof stockQuantity === 'number') {
        if (stockQuantity === 0) return "Нет в наличии"
        if (stockQuantity > 5) return "В наличии"
        if (stockQuantity >= 1 && stockQuantity <= 5) return "Мало"
        return stockQuantity
    }
    return "Нет данных";
}

const addToCart = async () => {
    addToCartError.value = false

    let currentStockQuantity = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.stockQuantity)
    if (currentStockQuantity !== 0) {
        const orderItem = {
            product: product,
            quantity: 1,
            productId: productId,
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

const deleteProduct = async () => {
    const token = localStorage.getItem('token')

    await axios
        .delete(productsBackendUrl + `/${productId}`,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})

    location.reload()
};
</script>

<style scoped lang="scss">
.product{
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.product__admin-actions{
    margin-top: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
}
.product__info{
    padding: 10px 20px;
    width: 70%;
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
    width: 45%;
}
.product__availability{
    width: 15%;
    display: flex;
    justify-content: center;
    text-align: center;
}
.product__controls {
    width: 20%;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.old-price{
    color: dimgray;
    margin-left: 5px;
}
.description__text{
    padding: 0;
}
.description__more{
    padding: 0;
}
</style>
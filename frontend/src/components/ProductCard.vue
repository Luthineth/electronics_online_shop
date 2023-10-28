<template>
    <div class="product">
        <v-card
            class="product__info"
            variant="outlined"
        >
            <v-img
                class="product__picture"
                src="imageUrl"
            />
            <div class="product__description">
                <h3><router-link :to="'/products/' + productId">{{ productName }}</router-link></h3>
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
                    <h4 v-if="price === priceWithDiscount">
                        {{ price }}
                    </h4>
                    <div
                        v-else
                        class="price-updated"
                    >
                        <b>{{ priceWithDiscount }}</b>
                        <s class="old-price">{{ price }}</s>
                    </div>
                </div>
                <v-btn
                    variant="tonal"
                    color="green"
                    :disabled="stockQuantity === 0"
                >
                    в корзину
                </v-btn>
            </div>
        </v-card>
        <div
            class="product__admin-actions"
            v-if="admin === true"
        >
            <v-btn variant="text">edit</v-btn>
            <v-btn variant="outlined" color="red">delete</v-btn>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
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

const admin = false
let isDescriptionShown = ref(false)
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
</script>

<style scoped lang="scss">
.product{
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
}
.product__admin-actions{
    display: flex;
    flex-direction: column;
    align-items: center;
}
.product__info{
    padding: 10px 20px;
    width: 90%;
    max-width: 1500px;
    display: flex;
    align-items: center;
    gap: 1rem;
}
.product__picture{
    width: 20%;
}
.product__description{
    width: 55%;
}
.product__availability{
    width: 15%;
    display: flex;
    justify-content: center;
    text-align: center;
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
.description__text{
    padding: 0;
}
.description__more{
    padding: 0;
}
</style>
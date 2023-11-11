<template>
    <AlertContainer
        v-if="placeOrderSuccess"
        :color="'success'"
        :icon="'mdi-check-circle-outline'"
        :message="'Поздравляем! Заказ подтвержден, отправили письмо на вашу почту'"
    />

    <AlertContainer
        v-if="!userAuthorized"
        :color="'info'"
        :icon="'mdi-information-outline'"
        :message="'Чтобы оформить заказ, нужно авторизоваться'"
    />

    <AlertContainer
        v-if="confirmationError || sendOrderError"
        :color="confirmationError ? 'warning' : 'error'"
        :icon="'mdi-alert-circle-outline'"
        :message="confirmationError ? `Уменьшите количество товаров ` + problematicProductsNames.join(', ') : 'Заказ не удалось подтвердить'"
    />

    <div class="order-info">
        <div class="order__items d-flex justify-center">
            <OrderList
                v-if="orderItems.length !== 0"
                :orderItems="orderItems"
            />
            <h4 v-else>
                Корзина пуста, добавьте что-нибудь!
            </h4>
        </div>
        <div class="order__details">
            <v-card
                    class="pa-3 position-fixed"
                    variant="outlined"
                    width="fit-content"
            >
                <v-progress-linear
                    :active="processingOrder"
                    :indeterminate="processingOrder"
                    absolute
                    height="10"
                    color="light-green-lighten-3"
                />

                <v-card-title class="pa-0">
                    Ваш заказ
                </v-card-title>

                <v-table class="order__summary">
                    <tr>
                        <td class="pr-3">Кол-во товаров:</td>
                        <td>{{ cartItemCount }}</td>
                    </tr>
                    <tr>
                        <td class="pr-3">Итого:</td>
                        <td>{{ orderPrice }}₽</td>
                    </tr>
                </v-table>

                <v-card-actions class="d-flex justify-center">
                    <v-btn
                        color="green"
                        variant="tonal"
                        :disabled="!userAuthorized || processingOrder || orderItems.length === 0"
                        @click="processingOrder = true; confirmOrder()"
                    >
                        Оформить заказ
                    </v-btn>
                </v-card-actions>
            </v-card>
        </div>
    </div>
</template>

<script setup>
import {cartItemCount, userAuthorized} from "../utils/utils";
import {computed, onMounted, ref} from "vue";
import store from "../stores/store";
import OrderList from "../components/OrderList.vue";
import axios from "axios";
import AlertContainer from "../components/AlertContainer.vue";

let placeOrderSuccess = ref(false)
let processingOrder = ref(false)
let confirmationError = ref(false)
let sendOrderError = ref(false)
let problematicProductsNames = ref([])

const confirmOrder = async () => {
    confirmationError.value = false

    await store.dispatch("load");
    const orderItemsArray = [];
    problematicProductsNames.value = [];

    for (const cartItem of store.state.cart) {
        let currentStockQuantity = await fetch(`http://localhost:8080/products/${cartItem.productId}`)
            .then(res => res.json())
            .then(res => res.stockQuantity)
        if (cartItem.quantity <= currentStockQuantity) {
            const {productId, quantity} = cartItem;
            orderItemsArray.push({productId, quantity});
        } else {
            problematicProductsNames.value.push(cartItem.product.productName)
            confirmationError.value = true
            processingOrder.value = false
        }
    }

    if(confirmationError.value === false) {
        await sendOrderToServer(orderItemsArray);
    } else {
        setTimeout(() => {
            confirmationError.value = false;
        }, 1000);
    }
};

const sendOrderToServer = async (orderItemsArray) => {
    sendOrderError.value = false

    const token = localStorage.getItem('token')

    await axios
        .post(`http://localhost:8080/orders`,
            orderItemsArray,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})
        .catch(() => {
            sendOrderError.value = true
            processingOrder.value = false
        })
        .then(() => {
            if (sendOrderError.value === false) {
                placeOrderSuccess.value = true;
                setTimeout(() => {
                    placeOrderSuccess.value = false;
                }, 2000);
                cartItemCount.value = 0
                store.dispatch("clearCart");
                processingOrder.value = false
            }
        })
};

const orderItems = computed(() => store.state.cart);
const orderPrice = computed(() => store.state.totalPrice);

onMounted(() => {
    store.dispatch("load");
});
</script>

<style scoped lang="scss">
.order-info{
  margin: auto;
  width: 100%;
  max-width: 1390px;
  display: flex;
}
.order__items{
  width: 70%;
}
.order__details{
  width: 30%;
  display: flex;
  justify-content: center;
}
.order__summary{
  background-color: transparent;
}
</style>
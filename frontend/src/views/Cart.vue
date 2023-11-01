<template>
    <div class="order-info">
        <div class="order__items d-flex justify-center">
            items
        </div>
        <div class="order__details">
            <v-card
                    class="pa-3 position-fixed"
                    variant="outlined"
                    width="fit-content"
            >
                <v-card-title class="pa-0">
                    Ваш заказ
                </v-card-title>
                <v-table class="order__summary">
                    <tr>
                        <td class="pr-3">Количество товаров:</td>
                        <td>{{ cartItemCount }}</td>
                    </tr>
                    <tr>
                        <td class="pr-3">Итого:</td>
                        <td>{{ totalOrderCost }} ₽</td>
                    </tr>
                </v-table>
                <v-card-actions class="d-flex justify-center">
                    <v-btn color="green" variant="tonal" @click="confirmOrder()">
                        Оформить заказ
                    </v-btn>
                </v-card-actions>
            </v-card>
        </div>
    </div>
</template>

<script setup>
import {cartItemCount} from "../utils/utils";
import {ref} from "vue";

let totalOrderCost = ref(0)
let confirmationError = ref(false)

const confirmOrder = () => {

};

const sendOrder = () => {

};
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
<template>
    <v-dialog activator="parent" width="auto">
        <v-card class="pa-2" width="500px">
            <v-text-field
                v-model="productName"
                label="Название"
                variant="underlined"
            />
            <v-textarea
                v-model="description"
                label="Описание"
                variant="underlined"
            />
            <v-text-field
                v-model="price"
                label="Цена"
                variant="underlined"
            />
            <v-text-field
                v-model="stockQuantity"
                label="Количество"
                variant="underlined"
            />
            <v-text-field
                v-model="priceWithDiscount"
                label="Цена со скидкой"
                variant="underlined"
            />
            <v-text-field
                v-model="ImageUrl"
                label="Ссылка на изображение"
                variant="underlined"
            />
            <v-card-actions class="d-flex justify-center">
                <v-btn>Сохранить</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script setup>
</script>

<style scoped>
.product-input{
    height: 200px;
}
</style>
<template>
    <v-dialog
        activator="parent"
        width="auto"
    >
        <v-card
            class="pa-2"
            width="80vw"
            max-width="800px"
        >
            <form @submit.prevent="submitChanges()">
                <v-card-title
                    class="pa-0"
                    v-if="productId"
                >
                    Id продукта: {{productId}}
                </v-card-title>

                <v-text-field
                    v-model="productName.value.value"
                    :error-messages="productName.errorMessage.value"
                    label="Название"
                    variant="underlined"
                />
                <v-textarea
                    v-model="description.value.value"
                    :error-messages="description.errorMessage.value"
                    label="Описание"
                    variant="underlined"
                />
                <v-text-field
                    v-model="price.value.value"
                    :error-messages="price.errorMessage.value"
                    type="number"
                    min="0"
                    label="Цена"
                    variant="underlined"
                />
                <v-text-field
                    v-model="stockQuantity.value.value"
                    :error-messages="stockQuantity.errorMessage.value"
                    type="number"
                    min="0"
                    label="Количество"
                    variant="underlined"
                />
                <v-text-field
                    v-if="productId"
                    v-model="discountPercentage.value.value"
                    :error-messages="discountPercentage.errorMessage.value"
                    type="number"
                    min="0"
                    max="100"
                    label="Процент скидки: 0, 10, 15, 20"
                    variant="underlined"
                />
                <v-file-input
                    v-model="newProductImage"
                    variant="underlined"
                    accept=".png"
                    placeholder="Выберите изображение"
                    prepend-icon="mdi-camera-plus-outline"
                    label="Прикрепите фото товара"
                >
                </v-file-input>
                <div v-if="imageUrl">
                    <i style="color: gray">
                        !Если фото не видно, битая ссылка: не найдено фото!
                    </i>

                    <span>
                        <v-img
                            :src="getImage('products', imageUrl)"
                            width="40px"
                        />
                        Текущее фото: {{ imageUrl }}
                    </span>

                    <v-switch
                        v-model="isOldPhotoSaved"
                        color="green-lighten-2"
                        label="Оставить текущее фото"
                    />
                </div>
                <v-autocomplete
                    clearable
                    multiple
                    v-model="selectedCategories"
                    label="Выберите категории"
                    :items="categoriesList"
                    item-title="categoryName"
                    item-value="categoryId"
                    variant="underlined"
                />
                <v-card-actions class="d-flex justify-center">
                    <v-btn
                        @click="submitChanges()"
                    >
                        Сохранить
                    </v-btn>
                </v-card-actions>
            </form>
            <v-snackbar
                v-model="formNotCompleted"
                :timeout="3000"
            >
                {{ formNotCompletedMessage }}
            </v-snackbar>
        </v-card>
    </v-dialog>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import router from "../../router/router";
import {getImage} from "../../utils/utils";
import {useField, useForm} from "vee-validate";
import {mainBackendUrl, productsBackendUrl} from "../../utils/urls";

const { oldProductInfo } = defineProps(['oldProductInfo']);

//errors
const formNotCompleted = ref(false)
const formNotCompletedMessage = ref('')

//basic info
const categoryId = router.currentRoute.value.params.id
const productId = ref(oldProductInfo?.productId)
const categoriesList = ref([])

//validation for text inputs
const { handleSubmit } = useForm({
    validationSchema: {
        productName (value) {
            if (value) return true

            return 'Введите название'
        },
        description (value) {
            if (value) return true

            return 'Введите описание'
        },
        price (value) {
            if (/^(?:\d*\.)?\d+$/.test(value)) return true

            return 'Введите цену'
        },
        stockQuantity (value) {
            if (/^[0-9]\d*$/.test(value)) return true

            return 'Количество должно быть целым неотрицательным числом'
        },
        discountPercentage (value) {
            if (!productId.value || /^(0|10|15|20)$/.test(value)) return true

            return 'Введите процент правильно'
        },
    },
    initialValues: {
        productName: oldProductInfo?.productName,
        description: oldProductInfo?.description,
        price: oldProductInfo?.price,
        stockQuantity: oldProductInfo?.stockQuantity,
        discountPercentage: oldProductInfo?.discountId.discountPercentage,
    },
})
//form text inputs
const productName = useField('productName')
const description = useField('description')
const price = useField('price')
const stockQuantity = useField('stockQuantity')
const discountPercentage = useField('discountPercentage')

//other form inputs
const selectedCategories = ref([])
const imageUrl = ref(oldProductInfo?.imageUrl)
const newProductImage = ref(null)
const isOldPhotoSaved = ref(false)

const submitChanges = handleSubmit(async () => {
    if (!isOldPhotoSaved.value && !newProductImage.value || selectedCategories.value.length === 0) {
        formNotCompleted.value = true
        formNotCompletedMessage.value =
            selectedCategories.value.length === 0
                ? 'Не выбрано ни одной категории'
                : 'Не выбрано фото: оставьте прежнее или добавьте новое'
    } else {
        productId.value ? await editProduct() : await addProduct()
    }
})

const addProduct = async () => {
    const token = localStorage.getItem('token')

    const formData = new FormData();
    formData.append('productName', productName.value.value);
    formData.append('description', description.value.value);
    formData.append('price', price.value.value);
    formData.append('stockQuantity', stockQuantity.value.value);
    formData.append('categoryId', selectedCategories.value);
    formData.append('file', newProductImage.value?.[0]);

    await axios
        .post(productsBackendUrl,
            formData,
            {headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                }})

    location.reload()
};

const editProduct = async () => {
    const token = localStorage.getItem('token')

    const formData = new FormData();
    formData.append('productName', productName.value.value);
    formData.append('description', description.value.value);
    formData.append('price', price.value.value);
    formData.append('stockQuantity', stockQuantity.value.value);
    formData.append('discountPercentage', discountPercentage.value.value);
    formData.append('categoryId', selectedCategories.value);
    isOldPhotoSaved.value
        ? formData.append('file', null)
        : formData.append('file', newProductImage.value?.[0]);

    await axios
        .put(productsBackendUrl + `/${productId.value}`,
            formData,
            {headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                }})

    location.reload()
};

onMounted(async () => {
    categoriesList.value = await fetch(mainBackendUrl)
        .then(res => res.json())
    selectedCategories.value.push(parseInt(categoryId));
});
</script>

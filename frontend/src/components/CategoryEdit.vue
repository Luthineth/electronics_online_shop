<template>
    <v-dialog activator="parent" width="auto">
        <v-card class="pa-2" width="500px">
            <v-card-title
                class="pa-0"
                v-if="categoryId"
            >
                Id категории: {{categoryId}}
            </v-card-title>

            <v-text-field
                clearable
                v-model="categoryName"
                label="Название"
                variant="underlined"
            />

            <v-select
                clearable
                v-model="selectedParentCategory"
                label="Выберите родительскую категорию"
                :items="categoriesList"
                item-title="categoryName"
                item-value="categoryId"
                variant="underlined"
            />

            <v-card-actions class="d-flex justify-center">
                <v-btn
                    @click="categoryId ? editCategory() : addCategory()"
                    :disabled="!categoryName"
                >
                    Сохранить
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";

const { categoryId, oldCategoryName, oldParentCategoryId } =
    defineProps(['categoryId', 'oldCategoryName', 'oldParentCategoryId']);

const categoryName = ref(oldCategoryName)
const selectedParentCategory = ref(oldParentCategoryId)
const categoriesList = ref([])

const addCategory = async () => {
    const token = localStorage.getItem('token')
    const categoryData = {
        parentCategoryId: selectedParentCategory.value,
        categoryName: categoryName.value,
    }

    await axios
        .post(`http://localhost:8080/categories`,
            categoryData,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})

    location.reload()
};

const editCategory = async () => {
    const token = localStorage.getItem('token')
    const categoryData = {
        parentCategoryId: selectedParentCategory.value,
        categoryName: categoryName.value,
    }

    await axios
        .put(`http://localhost:8080/categories/${categoryId}`,
            categoryData,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})

    location.reload()
};

onMounted(async () => {
    categoriesList.value = await fetch(`http://localhost:8080/main`)
        .then(res => res.json())
    if (categoryId) {
        categoriesList.value = categoriesList.value.filter(item => item.categoryId !== categoryId);
        categoriesList.value = categoriesList.value.filter(item => item.categoryName !== oldCategoryName);
    }
});
</script>

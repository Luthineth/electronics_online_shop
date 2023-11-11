<template>
    <div
        class="page-info"
        v-if="categoryWithParents.length !== 0"
    >
        <v-breadcrumbs
            class="d-flex justify-center"
            :items="parentCategories"
        />
        <div class="mb-6 d-flex justify-center">
            <v-btn
                v-if="userRole === 'ADMIN'"
                variant="tonal"
                color="green"
                width="fit-content"
            >
                Добавить товар
                <ProductEdit/>
            </v-btn>
        </div>
        <ProductList
            v-if="products.length !== 0"
            :products="products"
        />
        <h4 v-else class="d-flex justify-center">
            Тут пока нет товаров:(
        </h4>
    </div>
</template>

<script setup>
import {onMounted, ref} from "vue"
import ProductList from "../components/ProductList.vue";
import router from "../router/router";
import {userRole} from "../utils/utils";
import ProductEdit from "../components/ProductEdit.vue";

const categoryId = router.currentRoute.value.params.id
const isFetchError = ref(false);
const products = ref([])
const categoryWithParents = ref([])
const parentCategories = ref([])

function produceBreadCrumbs(category) {
    let categoryInfo = [];

    while (category) {
        categoryInfo.push({
            href: `http://localhost:5173/product_category/${category.categoryId}`,
            title: category.categoryName
        });
        category = category.parentCategoryId;
    }

    categoryInfo.push({
        href: '/',
        title: 'Главная'
    });

    return categoryInfo.reverse();
}

onMounted(async () => {
    try {
        const response = await fetch(`http://localhost:8080/products_category/${categoryId}`);

        if (!response.ok) {
            isFetchError.value = true;
        } else {
            products.value = await response.json();
        }
    } catch (error) {
        isFetchError.value = true;
    }

    if (isFetchError.value) {
        await router.push('/404');
    } else {
        categoryWithParents.value = await fetch(`http://localhost:8080/main`)
            .then(res => res.json())
            .then(res => res.filter(each => each.categoryId === parseInt(categoryId)))
        parentCategories.value = produceBreadCrumbs(categoryWithParents.value[0])
    }
});
</script>

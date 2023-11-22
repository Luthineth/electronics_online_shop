<template>
    <div
        class="page-info"
        v-if="categoryWithParents.length !== 0"
    >
        <v-breadcrumbs
            class="d-flex justify-center"
            :items="parentCategories"
        >
            ({{ countProducts }})
        </v-breadcrumbs>

        <div class="d-flex justify-center mb-3">
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

        <div class="d-flex justify-center mb-3">
            <h4
                style="cursor: pointer"
                @click="sortByPriceDesc = !sortByPriceDesc; sortProductsByPrice()"
            >
                {{ sortByPriceDesc? 'Сначала дороже' : 'Сначала дешевле'}}
                <v-icon icon="mdi-swap-vertical"/>
            </h4>

            <h4
                style="cursor: pointer; margin-left: 20px"
                @click="areProductsFilterOptionsShown = !areProductsFilterOptionsShown"
            >
                Фильтры поиска
                <v-icon :icon="areProductsFilterOptionsShown ? 'mdi-chevron-up' : 'mdi-chevron-down'"/>
            </h4>
        </div>

        <v-card
            v-if="areProductsFilterOptionsShown"
            class="pa-2 ml-auto mr-auto mt-2 mb-5"
            width="70%"
            max-width="1500px"
            variant="outlined"
        >
            <div class="d-flex justify-center">
                <v-switch
                    v-model="isInStock"
                    color="green-lighten-2"
                    label="В наличии"
                />
                <v-select
                    v-model="selectedMinRating"
                    label="Минимальная оценка"
                    :items="[5, 4, 3, 2, 1]"
                    variant="underlined"
                />
            </div>

            <v-range-slider
                v-model="range"
                :max="maxPrice"
                :min="0"
                :step="1"
                hide-details
                class="align-center"
            >
                <template v-slot:prepend>
                    <v-text-field
                        v-model="range[0]"
                        :max="maxPrice"
                        :min="0"
                        hide-details
                        single-line
                        type="number"
                        variant="outlined"
                        density="compact"
                        style="width: fit-content"
                    />
                </template>
                <template v-slot:append>
                    <v-text-field
                        v-model="range[1]"
                        :max="maxPrice"
                        :min="0"
                        hide-details
                        single-line
                        type="number"
                        variant="outlined"
                        style="width: fit-content"
                        density="compact"
                    />
                </template>
            </v-range-slider>

            <v-card-actions class="d-flex justify-center">
                <v-btn
                    @click="applyFiltersToProducts()"
                >
                    Применить фильтры
                </v-btn>
            </v-card-actions>
        </v-card>

        <ProductList
            v-if="products.length !== 0"
            :products="products"
        />

        <h4 v-else class="d-flex justify-center">
            Пока нет товаров:(
        </h4>
    </div>
</template>

<script setup>
import {onMounted, ref} from "vue"
import ProductList from "../components/products/ProductList.vue";
import router from "../router/router";
import {baseBackendUrl, getFilterUrl, userRole} from "../utils/utils";
import ProductEdit from "../components/products/ProductEdit.vue";

const categoryId = router.currentRoute.value.params.id

//filter data
const areProductsFilterOptionsShown = ref(false)
const sortByPriceDesc = ref (false)
const isInStock = ref(false)
const selectedMinRating = ref(null)
const minPrice = ref(0)
const maxPrice = ref(0)
const range = ref([minPrice.value, maxPrice.value])

//category data
const products = ref([])
const countProducts = ref(0)
const categoryWithParents = ref([])
const parentCategories = ref([])

//errors
const isFetchError = ref(false)

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

const sortProductsByPrice = () => {
    sortByPriceDesc.value
        ? products.value.sort((a, b) => b.price - a.price)
        : products.value.sort((a, b) => a.price - b.price);
};

const applyFiltersToProducts = async () => {
    const filterUrl =
        getFilterUrl(categoryId, range.value, isInStock.value, selectedMinRating.value, sortByPriceDesc.value)

    products.value = await fetch(filterUrl)
        .then((res) => res.json())

    countProducts.value = products.value.length
}

onMounted(async () => {
    try {
        const response = await fetch(baseBackendUrl + `/products_category/${categoryId}`);

        if (!response.ok) {
            isFetchError.value = true;
        } else {
            products.value = await response.json()

            maxPrice.value = Math.ceil(Math.max(...products.value.map(item => item.price)))
            range.value[1] = maxPrice.value
            countProducts.value = products.value.length

            sortProductsByPrice()
        }
    } catch (error) {
        isFetchError.value = true;
    }

    if (isFetchError.value) {
        await router.push('/404');
    } else {
        categoryWithParents.value = await fetch(baseBackendUrl + '/main')
            .then(res => res.json())
            .then(res => res.filter(each => each.categoryId === parseInt(categoryId)))
        parentCategories.value = produceBreadCrumbs(categoryWithParents.value[0])
    }
});
</script>

<template>
    <div class="mainNav wrapper__links">
        <router-link to="/">Главная</router-link>
        <div class="dropdown" @mouseover="showDropdown" @mouseleave="hideDropdown">
            <router-link to="/frsghus">
                Каталог
            </router-link>
            <div class="dropdown-content" :class="{ open: isOpen }">
                <HierarchicalCategoriesList :hierarchyProductTree="hierarchy" v-if="hierarchy"/>
            </div>
        </div>
        <router-link to="/frsdejn">О нас</router-link>
    </div>
    <div class="actions">
        <div class="actions__container">
            <div class="wrapper__links">
                <router-link to="/" v-if="userAuthorized">
                    <v-icon icon="mdi-logout"></v-icon>
                    Doe J.
                </router-link>
                <router-link to="/login" v-else>
                    <v-icon icon="mdi-login"></v-icon>
                    Войти
                </router-link>
                <div @click="updateItemCount()">
                    <v-icon icon="mdi-cart-outline"></v-icon>
                    ({{ itemCount }})
                </div>
            </div>
            <div class="wrapper__links">
                <v-icon icon="mdi-magnify" @click="showSearchBar"></v-icon>
                <v-icon icon="mdi-account-outline"></v-icon>
            </div>
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {isSearchBarShown} from "../utils/utils.js";
import HierarchicalCategoriesList from "./HierarchicalCategoriesList.vue";

let itemCount = ref(3)
let userAuthorized = ref(false)
let categories = ref([])
let hierarchy = ref([])

const updateItemCount = () => {
    itemCount.value += 1;
    userAuthorized.value = !userAuthorized.value
};

const showSearchBar = () => {
    isSearchBarShown.value = true
};
const isOpen = ref(false);

const showDropdown = () => {
    isOpen.value = true;
};

const hideDropdown = () => {
    isOpen.value = false;
};

function buildHierarchyTree(categories){
    const categoryMap = new Map();
    categories.forEach((category) => {
        categoryMap.set(category.categoryId, { ...category, children: [] });
    });

    const hierarchyTree = [];
    categories.forEach((category) => {
        if (category.parentCategoryId === null) {
            hierarchyTree.push(categoryMap.get(category.categoryId));
        } else {
            const parentCategory = categoryMap.get(category.parentCategoryId.categoryId);
            if (parentCategory) {
                parentCategory.children.push(categoryMap.get(category.categoryId));
            }
        }
    });

    return hierarchyTree
}


onMounted(async () => {
    categories.value = await fetch(`http://localhost:8080/main`)
        .then(res => res.json())
    hierarchy = buildHierarchyTree(categories.value)
});
</script>

<style scoped lang="scss">
a:hover {
  color: red;
}
.mainNav{
  padding: 0 10px 10px 10px;
}
.wrapper__links{
  display: flex;
  align-items: flex-end;
  gap: 1.425rem;
  text-decoration: none;
  -webkit-transition: all 0.2s ease-in-out;
  transition: all 0.2s ease-in-out;
}
.actions{
  margin-left: auto;
  margin-bottom: .475rem;
  display: flex;
}
.actions__container{
  display: flex;
  flex-flow: column nowrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: .7125rem;
}
.dropdown {
    position: relative;
    display: inline-block;
}
.dropdown-content {
    display: none;
    width: 100vw;
    max-width: 1390px;
    left: 0;
    position: fixed;
    z-index: 1;
    box-shadow: rgba(33, 35, 38, 0.1) 0 10px 10px -10px;
}

.open {
    display: block;
    background-color: #faf8f5;
}
</style>
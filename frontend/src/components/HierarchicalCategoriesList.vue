<template>
    <ul>
        <li v-for="item in hierarchyProductTree" :key="item.categoryId">
            <div class="categoryName" @click="loadNewPage(item.categoryId, 'product_category')">
                {{ item.categoryName }}
            </div>
            <HierarchicalCategoriesList :hierarchyProductTree="item.children" v-if="item.children" />
        </li>
    </ul>
</template>

<script setup>
import {loadNewPage} from "../utils/utils";

const { hierarchyProductTree } = defineProps({
    hierarchyProductTree: Array
})
</script>

<style scoped lang="scss">

ul {
    list-style-type: none;
    padding: 0;
    margin-top: 10px;
    background-color: #e2e2e2;
}

li{
    padding: 10px;
    margin-left: 10px;
}
.categoryName{
    cursor: pointer;
}
.categoryName:hover {
    color: red;
}
</style>

<template>
    <ul>
        <li v-for="item in hierarchyProductTree" :key="item.categoryId">
            <div>
                <button
                    class="categoryName"
                    @click="loadNewPage(`product_category/${item.categoryId}`)"
                >
                    {{ item.categoryName }}
                </button>

                <button v-if="userRole === 'ADMIN'">
                    <v-icon
                        class="ml-2"
                        icon="mdi-pencil-outline"
                        color="grey"
                    />

                    <CategoryEdit
                        :category-id="item.categoryId"
                        :old-category-name="item.categoryName"
                        :old-parent-category-id="item.parentCategoryId?.categoryId"
                    />
                </button>

                <v-icon
                    v-if="userRole === 'ADMIN'"
                    icon="mdi-delete-outline"
                    @click="deleteCategory(item.categoryId)"
                />
            </div>

            <HierarchicalCategoriesList
                :hierarchyProductTree="item.children"
                v-if="item.children"
            />
        </li>
    </ul>
</template>

<script setup>
import {baseBackendUrl, loadNewPage, userRole} from "../utils/utils";
import CategoryEdit from "./CategoryEdit.vue";
import axios from "axios";

const { hierarchyProductTree } = defineProps({
    hierarchyProductTree: Array
})

const deleteCategory = async (categoryId) => {
    const token = localStorage.getItem('token')

    await axios
        .delete(baseBackendUrl + `/categories/${categoryId}`,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})
    location.reload()
};
</script>

<style scoped lang="scss">

ul {
    margin-top: 10px;
}

li{
    padding-left: 10px;
    margin-left: 10px;
}
.categoryName:hover {
    color: darkgreen;
}
</style>

<template>
    <div class="text-center">
        <v-menu
            open-on-click
        >
            <template v-slot:activator="{ props }">
                <button
                        v-bind="props"
                >
                    Каталог
                </button>
            </template>

            <div class="dropdown-container">
                <div v-if="userRole === 'ADMIN'">
                    <v-btn
                        class="mb-2 mt-2"
                        width="100%"
                        variant="tonal"
                    >
                        Добавить категорию
                        <CategoryEdit/>
                    </v-btn>

                    <v-divider/>
                </div>

                <HierarchicalCategoriesList :hierarchyProductTree="hierarchy" v-if="hierarchy"/>
            </div>
        </v-menu>
    </div>
</template>

<script setup>
import HierarchicalCategoriesList from "./HierarchicalCategoriesList.vue";
import {onMounted, ref} from "vue";
import CategoryEdit from "./CategoryEdit.vue";
import {userRole} from "../../utils/variables";
import {mainBackendUrl} from "../../utils/urls";

let categories = ref([])
let hierarchy = ref([])

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
    categories.value = await fetch(mainBackendUrl)
        .then(res => res.json())
    hierarchy = buildHierarchyTree(categories.value)
});
</script>

<style scoped lang="scss">
.dropdown-container{
    margin-top: 10px;
    background-color: #e2e2e2;
    padding: 0 20px 0;
    max-height: 90vh;
    max-width: 800px;
    overflow: auto;
}
::-webkit-scrollbar {
    width: 10px;
}
::-webkit-scrollbar-track {
    background: #e2e2e2;
}
::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 10px;
}
::-webkit-scrollbar-thumb:hover {
    background: #555;
}
</style>
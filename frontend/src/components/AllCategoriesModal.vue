<template>
    <v-dialog
        activator="parent"
        width="auto"
    >
        <v-card class="pa-4">
            <div v-if="userRole === 'ADMIN'">
                <v-btn
                    class="mb-2"
                    width="100%"
                    variant="tonal"
                >
                    Добавить категорию
                    <CategoryEdit/>
                </v-btn>

                <v-divider/>
            </div>

            <HierarchicalCategoriesList :hierarchyProductTree="hierarchy" v-if="hierarchy"/>
        </v-card>
    </v-dialog>
</template>

<script setup>
import HierarchicalCategoriesList from "./HierarchicalCategoriesList.vue";
import {onMounted, ref} from "vue";
import CategoryEdit from "./CategoryEdit.vue";
import {userRole} from "../utils/utils";

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
    categories.value = await fetch(`http://localhost:8080/main`)
        .then(res => res.json())
    hierarchy = buildHierarchyTree(categories.value)
});
</script>

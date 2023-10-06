<template>
    <v-card
        class="mx-auto"
        width="70vw"
        variant="text"
    >
        <v-card-text>
            <v-text-field
                v-model="query"
                density="compact"
                variant="outlined"
                clearable=""
                label="Найти товар..."
                append-inner-icon="mdi-magnify"
                append-icon="mdi-close"
                single-line
                hide-details
                @keyup.enter="search"
                @click:append-inner="search"
                @click:append="hideSearchBar"
            ></v-text-field>
        </v-card-text>
    </v-card>
</template>

<script setup>
import { ref } from "vue"
import {isSearchBarShown} from "../utils/utils.js";

let query = ref('');
const search = () => {
    searchResults()
}

const searchResults = async () => {
    console.log(query.value)
}

const hideSearchBar = () => {
    isSearchBarShown.value = false;
}
</script>

<style scoped lang="scss">
.v-card{
    background: #faf8f5;
    max-width: 1200px;
}
</style>
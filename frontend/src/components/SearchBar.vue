<template>
    <v-card
        class="mx-auto"
        width="80vw"
        variant="text"
    >
        <v-card-text>
            <v-text-field
                density="compact"
                variant="outlined"
                clearable=""
                label="Search templates"
                append-inner-icon="mdi-magnify"
                append-icon="mdi-close"
                single-line
                hide-details
                @click:append="console.log('one')"
            ></v-text-field>
        </v-card-text>
    </v-card>
</template>

<script setup>

</script>

<style scoped lang="scss">
.v-card{
    background: #faf8f5;
}
</style>
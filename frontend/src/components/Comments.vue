<template>
    <div class="commentThread">
        <v-card
            class="mx-auto commentCard"
            width="700px"
            variant="tonal"
            v-for="comment in comments"
        >
            <v-card-title class="authorNameAndRating">
                {{ comment.firstName }}
                <v-rating
                    :model-value="comment.rating"
                    color="yellow-darken-3"
                    size="small"
                    disabled
                />
                <span class="text-grey-darken-2 text-caption me-2">
                    ({{ comment.rating }}/5)
            </span>
            </v-card-title>
            <v-card-text class="text-h6 py-2">
                <v-avatar
                    size="125"
                    rounded="0"
                    class="mb-2"
                    v-if="comment.imageUrl"
                >
                    <v-img src="https://cdn.vuetifyjs.com/images/cards/halcyon.png"></v-img>
                </v-avatar>
                <div>{{ comment.text }}</div>
            </v-card-text>

            <v-card-actions v-if="admin">
                <v-list-item class="w-100">
                    <template v-slot:append>
                        <div class="justify-self-end">
                            <v-icon class="me-1" icon="mdi-image-off-outline"></v-icon>
                            <span class="subheading me-2">Удалить фото</span>
                            <span class="me-1">·</span>
                            <v-icon class="me-1" icon="mdi-trash-can-outline"></v-icon>
                            <span class="subheading">Удалить отзыв</span>
                        </div>
                    </template>
                </v-list-item>
            </v-card-actions>
        </v-card>
    </div>
</template>

<script setup>

const { comments } = defineProps({
    comments: Array
})

const admin = false
</script>

<style scoped>
.authorNameAndRating{
    display: flex;
    align-items: center;
}
.commentThread{
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 1rem;
}
</style>
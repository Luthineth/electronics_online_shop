<template>
    <v-card
        class="pa-2 mb-9"
        variant="outlined"
        max-width="1000px"
        v-if="userAuthorized"
    >
        <v-card-title class="rating-input">
            Ваша оценка:
            <v-rating
                v-model="commentRating"
                color="yellow-darken-3"
            />
            <span class="text-grey-darken-2 text-caption me-2">
                    ({{ commentRating }}/5)
            </span>
        </v-card-title>

        <v-textarea
            v-model="commentText"
            variant="underlined"
            label="Напишите ваше мнение о товаре"
        />

        <v-file-input
            v-model="commentImage"
            variant="underlined"
            accept=".png"
            placeholder="Выберите изображение"
            prepend-icon="mdi-camera-plus-outline"
            label="Добавьте фото к отзыву"
        />

        <v-card-actions class="d-flex justify-center">
            <v-btn
                class="me-4"
                color="black"
                variant="tonal"
                @click="addComment()"
                :disabled="commentRating === 0"
            >
                <v-icon icon="mdi-arrow-up"/>
                Добавить комментарий
            </v-btn>
        </v-card-actions>
    </v-card>

    <h4
        v-else
        class="d-flex justify-center mb-3 text-decoration-underline"
    >
        <router-link to="/login">
            Авторизуйтесь, чтобы оставить отзыв!
        </router-link>
    </h4>

    <div class="commentThread">
        <v-select
            v-model="selectedRatingFilter"
            :items="filterOptions"
            item-title="title"
            item-value="val"
            variant="underlined"
        />

        <h4
            v-if="filteredComments.length === 0"
            class="d-flex justify-center"
        >
            Не нашли комментарии :(
        </h4>

        <v-card
            v-else
            class="mx-auto commentCard"
            width="100%"
            variant="outlined"
            v-for="comment in filteredComments"
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
                    <v-img :src="getImage('comments', comment.imageUrl)"/>
                </v-avatar>

                <div>{{ comment.text }}</div>
            </v-card-text>

            <v-card-actions v-if="userRole === 'ADMIN'">
                <v-list-item class="w-100">
                    <template v-slot:append>
                        <div class="justify-self-end">
                            <v-btn
                                v-if="comment.imageUrl"
                                class="subheading me-2"
                                variant="text"
                                color="red"
                                @click="deleteCommentPhoto(comment.commentId)"
                            >
                                <v-icon icon="mdi-image-off-outline"/>
                                Удалить фото
                            </v-btn>

                            <span class="me-1" v-if="comment.imageUrl">·</span>

                            <v-btn
                                class="subheading"
                                variant="tonal"
                                color="red"
                                @click="deleteComment(comment.commentId)"
                            >
                                <v-icon icon="mdi-trash-can-outline"/>
                                Удалить комментарий
                            </v-btn>
                        </div>
                    </template>
                </v-list-item>
            </v-card-actions>
        </v-card>
    </div>
</template>

<script setup>
import {getImage} from "../utils/utils";
import {userAuthorized, userRole} from "../utils/variables";
import axios from "axios";
import {computed, ref} from "vue";
import {commentsBackendUrl, productsBackendUrl} from "../utils/urls";

let { productId, comments } = defineProps({
    productId: String,
    comments: Array
});

const commentsRef = ref(comments)
const selectedRatingFilter = ref(0);

const commentRating = ref(0)
const commentText = ref('')
const commentImage = ref(null)

const filterOptions = [
    { title: 'Все комментарии', val: 0 },
    { title: 'С оценкой 5', val: 5 },
    { title: 'С оценкой 4', val: 4 },
    { title: 'С оценкой 3', val: 3 },
    { title: 'С оценкой 2', val: 2 },
    { title: 'С оценкой 1', val: 1 },
]

const filteredComments = computed(() => {
    if (selectedRatingFilter.value === 0) {
        return commentsRef.value;
    } else {
        return commentsRef.value.filter(comment => comment.rating === selectedRatingFilter.value);
    }
});

const addComment = async () => {
    const token = localStorage.getItem('token')

    const formData = new FormData();
    formData.append('productId', productId);
    formData.append('text', commentText.value);
    formData.append('rating', commentRating.value);
    formData.append('file', commentImage.value?.[0]);

    await axios
        .post(commentsBackendUrl,
            formData,
            {headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                }})

    commentsRef.value = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.comments)

    commentText.value = ''
    commentRating.value = 0
    commentImage.value = null
};

const deleteCommentPhoto = async (commentId) => {
    const token = localStorage.getItem('token')

    await axios
        .put(commentsBackendUrl + `/${commentId}/image`,
            {},
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})

    commentsRef.value = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.comments)
};

const deleteComment = async (commentId) => {
    const token = localStorage.getItem('token')

    await axios
        .delete(commentsBackendUrl + `/${commentId}`,
            {headers: {
                    'Authorization': `Bearer ${token}`
                }})

    commentsRef.value = await fetch(productsBackendUrl + `/${productId}`)
        .then(res => res.json())
        .then(res => res.comments)
};
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
.rating-input{
    display: flex;
    align-items: center;
    padding: 0;
    font-size: large;
}
</style>
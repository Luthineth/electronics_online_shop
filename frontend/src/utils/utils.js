import {ref} from "vue";

export const isSearchBarShown = ref(false);

export let userAuthorized = ref(false);

export const loadNewPage = (id, pageType) => {
    const {protocol, hostname , port} = window.location
    const url = `${protocol}//${hostname}:${port}/${pageType}/${id}`;

    window.open(url, '_self');
};
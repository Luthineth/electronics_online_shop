import {ref} from "vue";

export const isSearchBarShown = ref(false);

export let userAuthorized = ref(false);

export let cartItemCount = ref(0);

export let userRole = ref('USER')

export const loadNewPage = (pageType) => {
    const {protocol, hostname , port} = window.location
    const url = `${protocol}//${hostname}:${port}/${pageType}`;

    window.open(url, '_self');
};

export const scrollToTop = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
};

export const checkAuthorisation = () => {
    if (localStorage.getItem('token') !== null){
        let token = localStorage.getItem('token')
        userRole.value = JSON.parse(atob(token.split('.')[1])).role
        return true
    } else {
        return false
    }
};

export function getImage(src) {
    //'../../public/no_img.png'
    return `http://localhost:8080/products/images/iphone14_image.png`
}
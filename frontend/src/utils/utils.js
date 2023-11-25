import {baseBackendUrl} from "./urls";
import {tokenExpirationDate, userRole} from "./variables";

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
        tokenExpirationDate.value = JSON.parse(atob(token.split('.')[1])).exp
        return true
    } else {
        return false
    }
};

export function getFilterUrl(categoryId, range, inStock, minRating, direction) {
    let filterUrl = baseBackendUrl + `/products_category/${categoryId}?`;

    filterUrl += `&minPrice=${range[0]}&maxPrice=${range[1]}`;

    filterUrl += direction ? `&price=DESC` : `&price=ASC`;

    if (minRating !== null) {
        filterUrl += `&minRating=${minRating}`;
    }

    if (inStock) {
        filterUrl += `&inStock=true`;
    }

    return filterUrl;
}

export function getImage(type, src) {
    return baseBackendUrl + `/${type}/images/${src}`
}
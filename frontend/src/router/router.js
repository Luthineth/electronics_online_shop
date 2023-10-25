import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import NotFound from '../views/NotFound.vue'
import Authorization from "../views/Authorization.vue";
import ProductCategory from "../views/ProductCategory.vue";
import ProductInfo from "../views/ProductInfo.vue";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/product_category/:id',
        name: 'product_category',
        component: ProductCategory
    },
    {
        path: '/product/:id',
        name: 'product',
        component: ProductInfo
    },
    {
        path: '/login',
        name: 'Authorization',
        component: Authorization
    },
    {
        path: '/:pathMatch(.*)',
        name: 'NotFound',
        component: NotFound
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;
import { createStore } from 'vuex'

export default createStore({
    state: {
        cart: [],
    },
    mutations: {
        addToCart(state, product){
            state.cart.push(product)

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        removeFromCart(state, product){
            state.cart = state.cart.filter(each => each.id !== product.id)

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        editCart(state, product){
            const index = state.cart.findIndex((obj) => obj.id === product.id);
            state.cart.splice(index, 1, product);

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
    },
    actions: {
        load(){
            const cart = JSON.parse(localStorage.getItem("cart"));
            this.state.cart =  cart ?? [];
        }
    }
})
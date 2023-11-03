import { createStore } from 'vuex'

export default createStore({
    state: {
        cart: [],
    },
    mutations: {
        addToCart(state, orderItem){
            const existingItem = state.cart.find(item => item.productId === orderItem.productId);

            if (existingItem) {
                existingItem.quantity += 1;
            } else {
                state.cart.push(orderItem);
            }

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        removeFromCart(state, orderItem){
            state.cart = state.cart.filter(each => each.productId !== orderItem.productId)

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        editCart(state, orderItem){
            const index = state.cart.findIndex((obj) => obj.productId === orderItem.productId);
            state.cart.splice(index, 1, orderItem);

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
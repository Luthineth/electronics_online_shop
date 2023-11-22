import { createStore } from 'vuex'

export default createStore({
    state: {
        cart: [],
        totalPrice: 0,
    },
    mutations: {
        addToCart(state, orderItem){
            const existingItem = state.cart.find(item => item.productId === orderItem.productId);

            if (existingItem) {
                existingItem.quantity += 1;
            } else {
                state.cart.push(orderItem);
            }
            state.totalPrice = state.cart.reduce((total, item) => {
                return total + (item.product.priceWithDiscount * item.quantity);
            }, 0);

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        removeFromCart(state, orderItem){
            state.cart = state.cart.filter(each => each.productId !== orderItem.productId)
            state.totalPrice = state.cart.reduce((total, item) => {
                return total + (item.product.priceWithDiscount * item.quantity);
            }, 0);

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        editCart(state, orderItem){
            const index = state.cart.findIndex((obj) => obj.productId === orderItem.productId);
            state.cart.splice(index, 1, orderItem);
            state.totalPrice = state.cart.reduce((total, item) => {
                return total + (item.product.priceWithDiscount * item.quantity);
            }, 0);

            localStorage.setItem("cart", JSON.stringify(state.cart));
        },
        clearCart(state) {
            state.cart = [];
            state.totalPrice = 0;
            localStorage.removeItem("cart");
        },
    },
    actions: {
        load(){
            const cart = JSON.parse(localStorage.getItem("cart"));
            this.state.cart =  cart ?? [];
            this.state.totalPrice = this.state.cart.reduce((total, item) => {
                return total + (item.product.priceWithDiscount * item.quantity);
            }, 0);
        },
        clearCart({ commit }) {
            commit('clearCart');
        },
    }
})
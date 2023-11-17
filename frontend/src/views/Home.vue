<template>
  <router-link to="/product_category/1">
      bubgasgs
  </router-link>
    <router-link to="/product_category/2">
        bubgasgs
    </router-link>
  <div class="products-filter">
      <v-card
              class="pa-2"
      >
          <v-card-title
                  class="pa-0"
          >
              Фильтры поиска
              <v-icon
                  :icon="areProductsFilterOptionsShown ? 'mdi-chevron-up' : 'mdi-chevron-down'"
              />
          </v-card-title>
          <v-switch
                  color="green-lighten-2"
                  label="В наличии"
          />
          <v-range-slider
                  v-model="range"
                  :max="42000"
                  :min="0"
                  :step="1"
                  hide-details
                  class="align-center"
          >
              <template v-slot:prepend>
                  <v-text-field
                          v-model="range[0]"
                          :max="42000"
                          :min="0"
                          hide-details
                          single-line
                          type="number"
                          variant="outlined"
                          density="compact"
                          style="width: fit-content"
                  ></v-text-field>
              </template>
              <template v-slot:append>
                  <v-text-field
                          v-model="range[1]"
                          :max="42000"
                          :min="0"
                          hide-details
                          single-line
                          type="number"
                          variant="outlined"
                          style="width: fit-content"
                          density="compact"
                  ></v-text-field>
              </template>
          </v-range-slider>
          <v-select
                  label="Минимальная оценка"
                  :items="['5', '4', '3', '2', '1']"
                  variant="underlined"
          ></v-select>
          <v-card-actions>
              <v-btn>Применить фильтры</v-btn>
          </v-card-actions>
      </v-card>
      <v-card-title>
          {{ sortByPrice? 'Сначала дороже' : 'Сначала дешевле'}}
          <v-icon
              icon="mdi-swap-vertical"
          />
      </v-card-title>
  </div>
</template>

<script setup>
import {ref} from "vue";

const sortByPrice = ref (false)
const areProductsFilterOptionsShown = ref(false)
const maxPrice = ref(42000)
const range = ref([0, maxPrice.value])
</script>

<style scoped lang="scss">
</style>
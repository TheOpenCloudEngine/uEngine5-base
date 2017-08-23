// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import VueMaterial from '../node_modules/vue-material'

import App from './App'
import router from './router'


Vue.use(VueMaterial)
Vue.use(Vuetify)

Vue.config.productionTip = false

window.Vue = Vue;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
})

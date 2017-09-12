// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import VueMaterial from '../node_modules/vue-material'
import BpmnVue from './components/bpmn'
import Opengraph from './components/opengraph'

import App from './App'
import router from './router'


Vue.use(Vuetify);
Vue.use(VueMaterial);
Vue.use(BpmnVue);
Vue.use(Opengraph);

Vue.config.productionTip = false

window.Vue = Vue;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
});

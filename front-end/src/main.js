// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import VueMaterial from '../node_modules/vue-material'
import BpmnVue from './components/bpmn'
import Opengraph from './components/opengraph'
import App from './App'
import router from './router'
import VueI18n from '../node_modules/vue-i18n'
import ClassModeling from './components/designer/class-modeling'
import Essence from './components/designer/essence'
import Modeling from './components/designer/modeling'

Vue.use(VueMaterial);
Vue.use(BpmnVue);
Vue.use(Opengraph);
Vue.use(ClassModeling);
Vue.use(Essence);
Vue.use(Modeling);



/**
 * i18n
 */
Vue.use(VueI18n)
var resources = {
  ko: require('./message/message-ko.json'),
  en: require('./message/message-en.json')
};
var i18n = new VueI18n({
  locale: 'ko',
  fallbackLocale: 'ko',
  messages: resources
});

router.beforeEach((to, from, next) => {
  // localStorage['locale']외 다른 값으로도 설정 가능함
  var lang = localStorage['locale'];
  if (lang == null || lang == '') {
    lang = "ko";
  }
  i18n.locale = lang;
  next();
});

/**
 * i18n
 */

Vue.config.productionTip = false

window.Vue = Vue;

import VueBreadcrumbs from 'vue-breadcrumbs'
Vue.use(VueBreadcrumbs, {
  template: '<nav class="breadcrumb" v-if="$breadcrumbs.length"> ' +
  '<router-link class="md-title-accent" v-for="(crumb, key) in $breadcrumbs" :to="linkProp(crumb)" :key="key">' +
  '<span class="md-title-accent" v-if="crumb.meta.preTitle">{{ crumb.meta.preTitle }} &nbsp;&nbsp;>&nbsp;&nbsp;</span>' +
  '<span class="md-title-accent" v-if="crumb.meta.breadcrumb.indexOf(' + "':'" + ') != -1">{{ $route.params[crumb.meta.breadcrumb.split(' + "':'" + ')[1]] }} &nbsp;&nbsp;>&nbsp;&nbsp;</span>' +
  '<span class="md-title-accent" v-else>{{ crumb.meta.breadcrumb }} &nbsp;&nbsp;>&nbsp;&nbsp;</span>' +
  '</router-link> ' +
  '</nav>'
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n: i18n,
  template: '<App/>',
  components: {App}
});

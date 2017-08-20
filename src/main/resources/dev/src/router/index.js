import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import ServiceLocator from '@/components/ServiceLocator'
import Designer from '@/components/Designer'
import Home from '@/components/Home'
import Sns from '@/components/Sns'

let iam = new IAM('http://localhost:8080/iam');
let RouterGuard = require("./RouterGuard.js")(iam);


// https://github.com/waynecz/vue-img-inputer --Document
import VueImgInputer from 'vue-img-inputer'
Vue.component('vue-img-inputer', VueImgInputer)

// Vue.component('service-locator', ServiceLocator);

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/sns',
      name: 'home',
      component: Home,
      props: { iam: iam },
      children: [
        {
          path: 'sns',
          name: 'sns',
          component: Sns,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'designer',
          name: 'designer',
          component: Designer,
          beforeEnter: RouterGuard.requireUser,
        },
      ]
    },
    {
      path: '/auth/:command',
      name: 'login',
      component: Login,
      props: { iam: iam },
      beforeEnter: RouterGuard.requireGuest
    }
  ]
})

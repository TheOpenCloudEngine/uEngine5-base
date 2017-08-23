import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import ServiceLocator from '@/components/ServiceLocator'
import Designer from '@/components/Designer'
import Home from '@/components/Home'
import Sns from '@/components/Sns'
import WorkItemHandler from '@/components/WorkItemHandler'

/**
 * Iam && Vue Router
 * @type {IAM}
 */
let iam = new IAM('http://localhost:8080/iam');
let RouterGuard = require("./RouterGuard.js")(iam);
Vue.use(Router);


/**
 * VueImgInputer
 */
// https://github.com/waynecz/vue-img-inputer --Document
import VueImgInputer from 'vue-img-inputer'
Vue.component('vue-img-inputer', VueImgInputer)


/**
 * Vue resource configuration
 */
let VueResource = require('vue-resource-2');
Vue.use(VueResource);


/**
 * ServiceLocator
 */
Vue.component('service-locator', ServiceLocator);
Vue.component('work-item', WorkItemHandler);
Vue.http.interceptors.push(function (request, next) {


  console.log(request);
  // modify headers
  request.headers['access_token'] = localStorage['access_token'];

  // continue to next interceptor
  next();
});

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/sns',
      name: 'home',
      component: Home,
      props: {iam: iam},
      children: [
        {
          path: 'sns',
          name: 'sns',
          component: Sns,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'definition',
          name: 'definition',
          component: Designer,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'definition/:id',
          name: 'graph',
          component: Graph,
          beforeEnter: RouterGuard.requireUser,
        }
      ]
    },
    {
      path: '/auth/:command',
      name: 'login',
      component: Login,
      props: {iam: iam},
      beforeEnter: RouterGuard.requireGuest
    }
  ]
})

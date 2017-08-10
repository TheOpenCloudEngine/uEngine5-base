import Vue from 'vue'
import Router from 'vue-router'
import IAMLogin from '@/components/IAMLogin'
import ServiceLocator from '@/components/ServiceLocator'
import Designer from '@/components/Designer'
import Home from '@/components/Home'
import Sns from '@/components/Sns'
import HeaderNav from '@/components/HeaderNav'

import RouterGuard from './RouterGuard'

Vue.component('service-locator', ServiceLocator);
Vue.component('iam-login', IAMLogin);
Vue.component('header-nav', HeaderNav);


Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/sns',
      name: 'home',
      component: Home,
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
      path: '/login',
      name: 'login',
      component: IAMLogin,
      beforeEnter: RouterGuard.requireGuest
    }
  ]
})

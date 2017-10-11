import Vue from 'vue'
import Router from 'vue-router'
// import Login from '@/components/Login'
import ServiceLocator from '@/components/ServiceLocator'
import InstanceList from '@/components/InstanceList'
import SvgGraph from '@/components/SvgGraph'
Vue.component('svg-graph', SvgGraph);


/**
 * Iam && Vue Router
 * @type {IAM}
 */
// let iam = new IAM(location.protocol + '//' + location.hostname + ':8080/iam');
//let RouterGuard = require("./RouterGuard.js")(iam);
Vue.use(Router);

/**
 * Vue resource configuration
 */
let VueResource = require('vue-resource-2');
Vue.use(VueResource);


/**
 * ServiceLocator
 */
Vue.component('service-locator', ServiceLocator);
Vue.http.interceptors.push(function (request, next) {
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
      redirect: '/instance',
      name: 'home'
    },
    {
      path: '/instance',
      name: 'instance',
      component: InstanceList,
      //beforeEnter: RouterGuard.requireUser,
    },
    {
      path: '/instance/:id',
      name: 'instanceMonitor',
      component: SvgGraph,
      //beforeEnter: RouterGuard.requireUser,
      props: {monitor: true}
    }
  ]
})

import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import ServiceLocator from '@/components/ServiceLocator'
import Designer from '@/components/Designer'
import Home from '@/components/Home'
import Sns from '@/components/Sns'
import WorkItemHandler from '@/components/WorkItemHandler'
import ObjectForm from '@/components/ObjectForm'
import ObjectGrid from '@/components/ObjectGrid'
import ClassEditor from '@/components/ClassEditor'
import ClassSelector from '@/components/ClassSelector'
import AvatarUploader from '@/components/AvatarUploader'
import InstanceList from '@/components/InstanceList'
import SvgGraph from '@/components/SvgGraph'


/**
 * Iam && Vue Router
 * @type {IAM}
 */
let iam = new IAM(location.protocol + '//' + location.hostname + ':8080/iam');
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
Vue.http.interceptors.push(function (request, next) {
  // modify headers
  request.headers['access_token'] = localStorage['access_token'];

  // continue to next interceptor
  next();
});


/**
 * Others
 */
Vue.component('work-item', WorkItemHandler);
Vue.component('object-grid', ObjectGrid);
Vue.component('object-form', ObjectForm);
Vue.component('class-editor', ClassEditor);
Vue.component('class-selector', ClassSelector);
Vue.component('avatar-uploader', AvatarUploader);
Vue.component('svg-graph', SvgGraph);


import CloudExample from '../components/example/CloudExample'
Vue.component('cloud-example', CloudExample);

import ElementListExample from '../components/example/ElementListExample'
Vue.component('element-list-example', ElementListExample);

import ChartExample from '../components/example/ChartExample'
Vue.component('chart-example', ChartExample);


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
          path: 'example/cloud',
          name: 'cloudexample',
          component: CloudExample,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'example/elements',
          name: 'elementlistexample',
          component: ElementListExample,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'example/chart',
          name: 'chartexample',
          component: ChartExample,
          beforeEnter: RouterGuard.requireUser,
        },
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
          component: SvgGraph,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'instance',
          name: 'instance',
          component: InstanceList,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'instance/:id',
          name: 'instanceMonitor',
          component: SvgGraph,
          beforeEnter: RouterGuard.requireUser,
          props: {monitor: true}
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

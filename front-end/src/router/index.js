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
import IAMAvatar from '@/components/IAMAvatar'
import InstanceList from '@/components/InstanceList'
import SvgGraph from '@/components/SvgGraph'
import UserPicker from '@/components/bpm-portal/UserPicker'
import UserAutocomplete from '@/components/bpm-portal/UserAutocomplete'
import NewPackage from '@/components/bpm-portal/NewPackage'
import RenamePackage from '@/components/bpm-portal/RenamePackage'
import DeletePackage from '@/components/bpm-portal/DeletePackage'
import ListPackage from '@/components/bpm-portal/ListPackage'
import MovePackage from '@/components/bpm-portal/MovePackage'


//import CustomizedSvgGraph from '@/components/CustomizedSvgGraph'

/**
 * Iam && Vue Router
 * @type {IAM}
 */
//let iam = new IAM(location.protocol + '//' + location.hostname + ':8080/iam');
var iam = new IAM('http://iam.uengine.io:8080');
//TODO Implict grant flow 로 전환하기. (클라이언트 시크릿키가 필요없는 flow, 로그인 창을 iam 이 제공.)
iam.setDefaultClient('e74a9505-a811-407f-b4f6-129b7af1c703','109cf590-ac67-4b8c-912a-913373ada046');

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
 * Hybind
 */
var access_token = localStorage["access_token"];

var backend = hybind("http://localhost:8080", {headers:{'access_token': access_token}});


/**
 * Others
 */
Vue.component('work-item', WorkItemHandler);
Vue.component('object-grid', ObjectGrid);
Vue.component('object-form', ObjectForm);
Vue.component('class-editor', ClassEditor);
Vue.component('class-selector', ClassSelector);
Vue.component('avatar-uploader', AvatarUploader);
Vue.component('iam-avatar', IAMAvatar);
Vue.component('svg-graph', SvgGraph);
Vue.component('user-picker', UserPicker);
Vue.component('new-package', NewPackage);
Vue.component('rename-package', RenamePackage);
Vue.component('delete-package', DeletePackage);
Vue.component('list-package', ListPackage);
Vue.component('move-package', MovePackage);
Vue.component('user-autocomplete', UserAutocomplete);

import CloudExample from '../components/example/CloudExample'
Vue.component('cloud-example', CloudExample);

import ElementListExample from '../components/example/ElementListExample'
Vue.component('element-list-example', ElementListExample);

import ChartExample from '../components/example/ChartExample'
Vue.component('chart-example', ChartExample);


//--------- customized components here -------


//Vue.component('svg-graph', CustomizedSVGGraph);


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
          name: 'Workspace',
          component: Sns,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend
          },
        },
        {
          path: 'definition',
          name: 'definition',
          component: Designer,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend
          },
        },
        {
          path: 'definition/:id',
          name: 'graph',
          component: SvgGraph,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend
          },
        },
        {
          path: 'definition/:path/:id',
          name: 'graph',
          component: SvgGraph,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend
          },
        },
        {
          path: 'instance',
          name: 'instance',
          component: InstanceList,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend
          },
        },
        {
          path: 'instance/:id',
          name: 'instanceMonitor',
          component: SvgGraph,
          beforeEnter: RouterGuard.requireUser,
          props: {
            monitor: true,
            iam: iam,
            backend: backend
          }
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

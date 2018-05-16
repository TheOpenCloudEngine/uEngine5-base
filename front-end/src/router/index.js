import Vue from 'vue'
import Router from 'vue-router'
import Login from '../../node_modules/metaworks4/src/components/Login.vue'
import ServiceLocator from '@/components/ServiceLocator'
import Home from '@/components/Home'


/**
 * Sns
 */
import Sns from '@/components/sns/Sns'
import WorkItemHandler from '@/components/sns/WorkItemHandler'
import InstanceHandler from '@/components/sns/InstanceHandler'

Vue.component('work-item-handler', WorkItemHandler);
Vue.component('instance-handler', InstanceHandler);


/**
 * Instance
 */
import InstanceList from '@/components/instance/InstanceList'

/**
 * Service
 */
import Service from '@/components/service/ServiceManagement'


/**
 * Designer / Definition
 */
import DefinitionList from '@/components/designer/DefinitionList'
import ModelerRouter from '@/components/designer/ModelerRouter'
import ProcessDesigner from '@/components/designer/process//ProcessDesigner'
import ClassModeler from '@/components/designer/class-modeling/ClassModeler'
import PracticeDesigner from '@/components/designer/essence/PracticeDesigner'

Vue.component('modeler-router', ModelerRouter);
Vue.component('process-designer', ProcessDesigner);


import ObjectForm from '@/components/ObjectForm'
import ObjectFormSelect from '@/components/ObjectFormSelect'
import ObjectFormBoolean from '@/components/ObjectFormBoolean'
import ObjectGrid from '@/components/ObjectGrid'
import ClassEditor from '@/components/ClassEditor'
import ClassSelector from '@/components/ClassSelector'
import UserSelector from '@/components/bpm-portal/UserSelector'
import AvatarUploader from '@/components/AvatarUploader'
import IAMAvatar from '@/components/IAMAvatar'
import UserPicker from '@/components/bpm-portal/UserPicker'
import UserAutocomplete from '@/components/bpm-portal/UserAutocomplete'
import NewPackage from '@/components/bpm-portal/NewPackage'
import RenamePackage from '@/components/bpm-portal/RenamePackage'
import DeletePackage from '@/components/bpm-portal/DeletePackage'
import ListPackage from '@/components/bpm-portal/ListPackage'
import MovePackage from '@/components/bpm-portal/MovePackage'
import VersionManager from '@/components/bpmn/VersionManager'


import Metaworks4 from '../../node_modules/metaworks4'

Vue.use(Metaworks4);

import AsyncComputed from 'vue-async-computed'

Vue.use(AsyncComputed);

import TreeView from "vue-json-tree-view"

Vue.use(TreeView)


/**
 * Iam && Vue Router
 * @type {IAM}
 */
var clientKey = "my-client-key";

//This required for managing user rest api (avatar upload, curl user data, etc..)
//If the client type is not public,(described in iam yaml setting) the rest api will rejected.
var clientSecret = "my-client-secret";

//window.profile will be autowired by uengine-cloud-server. It can be local,dev,stg,prod. default is 'local'.
var profile = window.profile;


//Change the url your IAM application's vcap service's profile url.
//For example, 'http://' + config.vcap.services['your-iam-server'][profile].external;
var iamUrl = 'http://iam.pas-mini.io';

//Define iam client
var iam = new IAM(iamUrl);

//Set clientKey, clientSecret(optional).
iam.setDefaultClient(clientKey, clientSecret);

//Mark in window
window.iam = iam;

let RouterGuard = require("./RouterGuard.js")(iam);
Vue.use(Router);


/**
 * VueImgInputer
 */
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

var backend;
if (profile == 'local') {
  backend = hybind("http://localhost:8080", {headers: {'access_token': access_token}});
} else {
  backend = hybind("http://" + config.vcap.services['uengine5-router'][profile].external, {headers: {'access_token': access_token}});
}

window.backend = backend;

/**
 * Others
 */
Vue.component('object-grid', ObjectGrid);
Vue.component('object-form', ObjectForm);
Vue.component('object-form-select', ObjectFormSelect);
Vue.component('object-form-boolean', ObjectFormBoolean);
Vue.component('class-editor', ClassEditor);
Vue.component('class-selector', ClassSelector);
Vue.component('object-form-org-uengine-kernel-role-mapping', UserSelector);

if (!Vue._components) Vue._components = {};
Vue._components['object-form-org-uengine-kernel-role-mapping'] = UserSelector;

Vue.component('avatar-uploader', AvatarUploader);
Vue.component('iam-avatar', IAMAvatar);
Vue.component('user-picker', UserPicker);
Vue.component('new-package', NewPackage);
Vue.component('rename-package', RenamePackage);
Vue.component('delete-package', DeletePackage);
Vue.component('list-package', ListPackage);
Vue.component('move-package', MovePackage);
Vue.component('user-autocomplete', UserAutocomplete);
Vue.component('version-manager', VersionManager);

import CloudExample from '../components/example/CloudExample'

Vue.component('cloud-example', CloudExample);

import ElementListExample from '../components/example/ElementListExample'

Vue.component('element-list-example', ElementListExample);

import ChartExample from '../components/example/ChartExample'

Vue.component('chart-example', ChartExample);

import ClassDiagram from '../components/example/ClassDiagram'


//--------- customized components here -------


export default new Router({
//  mode: 'history',
  base: '/',
  routes: [
    {
      path: '/',
      redirect: '/sns',
      name: 'home',
      component: Home,
      props: {iam: iam},
      meta: {
        breadcrumb: '홈'
      },
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
          path: 'example/class',
          name: 'classexample',
          component: ClassDiagram,
          beforeEnter: RouterGuard.requireUser,
        },
        {
          path: 'sns',
          name: 'Workspace',
          component: Sns,
          beforeEnter: RouterGuard.requireUser,
          meta: {
            breadcrumb: 'Workspace'
          },
          props: {
            backend: backend
          },
        },
        {
          path: 'services',
          name: 'Service',
          component: Service,
          beforeEnter: RouterGuard.requireUser,
          meta: {
            breadcrumb: 'Service'
          },
          props: {
            backend: backend
          },
        },
        {
          path: 'designer/:path*',
          name: 'designer',
          component: DefinitionList,
          beforeEnter: RouterGuard.requireUser,
          meta: {
            breadcrumb: 'Designer'
          },
          props: function (route) {
            return {
              backend: backend,
              path: route.params.path
            }
          }
        },
        {
          path: 'definition/:path*',
          name: 'definition',
          component: ModelerRouter,
          beforeEnter: RouterGuard.requireUser,
          props: function (route) {
            return {
              backend: backend,
              path: route.params.path
            }
          }
        },
        {
          path: 'class-definition',
          name: 'classdefinition',
          component: ClassModeler,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend,
          },
        },
        {
          path: 'process-definition',
          name: 'processdefinition',
          component: ProcessDesigner,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend,
          },
        },
        {
          path: 'practice',
          name: 'practice',
          component: PracticeDesigner,
          beforeEnter: RouterGuard.requireUser,
          props: {
            backend: backend,
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
          path: 'instance/:rootId/:id',
          name: 'instanceMonitor',
          component: ProcessDesigner,
          beforeEnter: RouterGuard.requireUser,
          props: function (route) {
            return {
              backend: backend,
              instanceId: route.params.id,
              rootInstanceId: route.params.rootId,
              monitor: true,
              iam: iam
            }
          }
        }
      ]
    },
    {
      path: '/auth/:command',
      name: 'login',
      component: Login,
      props: {
        iamServer: iamUrl,
        scopes: "cloud-server,bpm"
      },
      beforeEnter: RouterGuard.requireGuest
    }

  ]
})

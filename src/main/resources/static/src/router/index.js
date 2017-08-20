import Vue from 'vue'
import Router from 'vue-router'
import IAMLogin from '@/components/IAMLogin'
import ServiceLocator from '@/components/ServiceLocator'
import InstanceView from '@/components/InstanceView'
import ObjectForm from '@/components/ObjectForm'
import ObjectGrid from '@/components/ObjectGrid'
import ClassEditor from '@/components/ClassEditor'

Vue.component('service-locator', ServiceLocator);
Vue.component('iam-login', IAMLogin);
Vue.component('instance-view', InstanceView);
Vue.component('object-grid', ObjectGrid);
Vue.component('object-form', ObjectForm);
Vue.component('class-editor', ClassEditor);

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/class-editor',
      name: 'ClassEditor',
      component: ClassEditor
    },
  ]
})

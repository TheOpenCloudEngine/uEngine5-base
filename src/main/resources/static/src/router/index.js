import Vue from 'vue'
import Router from 'vue-router'
import IAMLogin from '@/components/IAMLogin'
import ServiceLocator from '@/components/ServiceLocator'
import InstanceView from '@/components/InstanceView'

Vue.component('service-locator', ServiceLocator);
Vue.component('iam-login', IAMLogin);
Vue.component('instance-view', InstanceView);

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/mashupproduct',
      name: 'MashupProduct',
      component: ServiceLocator
    },
  ]
})

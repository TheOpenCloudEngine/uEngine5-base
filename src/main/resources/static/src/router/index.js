import Vue from 'vue'
import Router from 'vue-router'
import IAMLogin from '@/components/IAMLogin'
import ServiceLocator from '@/components/ServiceLocator'
import InstanceView from '@/components/InstanceView'
import ObjectForm from '@/components/ObjectForm'
import ObjectGrid from '@/components/ObjectGrid'
import ClassEditor from '@/components/ClassEditor'
import ClassSelector from '@/components/ClassSelector'
import ProcessDesigner from '@/components/ProcessDesigner'
import PropertyPanelStartEvent from '@/components/PropertyPanelStartEvent'

Vue.component('service-locator', ServiceLocator);
Vue.component('iam-login', IAMLogin);
Vue.component('instance-view', InstanceView);
Vue.component('object-grid', ObjectGrid);
Vue.component('object-form', ObjectForm);
Vue.component('class-editor', ClassEditor);
Vue.component('class-selector', ClassSelector);
Vue.component('process-designer', ProcessDesigner);
Vue.component('org-uengine-kernel-bpmn-startevent', PropertyPanelStartEvent);
Vue.component('org-uengine-kernel-defaultactivity', PropertyPanelStartEvent);
Vue.component('org-uengine-kernel-humanactivity', PropertyPanelStartEvent);

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/class-editor',
      name: 'ClassEditor',
      component: ClassEditor
    },
    {
      path: '/process-designer',
      name: 'ProcessDesigner',
      component: ProcessDesigner,
      props: { serviceLocator: 'backend', definitionName: 'test2'}
    },
  ]
})

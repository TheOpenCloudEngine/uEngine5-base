import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import ServiceLocator from '@/components/ServiceLocator'
import Designer from '@/components/Designer'
import Graph from '@/components/Graph'
import Home from '@/components/Home'
import Sns from '@/components/Sns'
import WorkItemHandler from '@/components/WorkItemHandler'
import ObjectForm from '@/components/ObjectForm'
import ObjectGrid from '@/components/ObjectGrid'
import ClassEditor from '@/components/ClassEditor'
import ClassSelector from '@/components/ClassSelector'
import AvatarUploader from '@/components/AvatarUploader'
import InstanceList from '@/components/InstanceList'
import SvgTest from '@/components/SvgTest'


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
Vue.component('svg-test', SvgTest);


/**
 * Bpmn 컴포넌트
 */
import BpmnComponents from '@/components/bpmn'
for (var key in BpmnComponents) {
  Vue.component(BpmnComponents[key].name, BpmnComponents[key]);
}
window.bpmnComponents = BpmnComponents;
//console.log(BpmnComponents.BpmnVue.computed.className());


/**
 * 시작 이벤트
 */
// import StartEvent from '@/components/opengraph/bpmn/StartEvent'
// import MessageStartEvent from '@/components/opengraph/bpmn/MessageStartEvent'
// import TimerStartEvent from '@/components/opengraph/bpmn/TimerStartEvent'
// import ConditionalStartEvent from '@/components/opengraph/ConditionalStartEvent'
// import SignalStartEvent from '@/components/opengraph/bpmn/SignalStartEvent'
//
// Vue.component('org-uengine-kernel-bpmn-startevent', StartEvent);
// Vue.component('org-uengine-kernel-bpmn-messagestartevent', MessageStartEvent);
// Vue.component('org-uengine-kernel-bpmn-timerstartevent', TimerStartEvent);
// Vue.component('org-uengine-kernel-bpmn-conditionalstartevent', ConditionalStartEvent);
// Vue.component('org-uengine-kernel-bpmn-signalstartevent', SignalStartEvent);
//
// //컴포넌트 안에 프로퍼티 패널도 포함시키기.
//
// import IntermediateThrowEvent from '@/components/opengraph/bpmn/IntermediateThrowEvent'
// import MessageIntermediateCatchEvent from '@/components/opengraph/bpmn/MessageIntermediateCatchEvent'
// import MessageIntermediateThrowEvent from '@/components/opengraph/bpmn/MessageIntermediateThrowEvent'
// import TimerIntermediateCatchEvent from '@/components/opengraph/bpmn/TimerIntermediateCatchEvent'
// import EscalationIntermediateThrowEvent from '@/components/opengraph/bpmn/EscalationIntermediateThrowEvent'
// import ConditionalIntermediateThrowEvent from '@/components/opengraph/bpmn/ConditionalIntermediateThrowEvent'
// import LinkIntermediateCatchEvent from '@/components/opengraph/bpmn/LinkIntermediateCatchEvent'
// import LinkIntermediateThrowEvent from '@/components/opengraph/bpmn/LinkIntermediateThrowEvent'
// import CompensationIntermediateThrowEvent from '@/components/opengraph/bpmn/CompensationIntermediateThrowEvent'
// import SignalIntermediateCatchEvent from '@/components/opengraph/bpmn/SignalIntermediateCatchEvent'
// import SignalIntermediateThrowEvent from '@/components/opengraph/bpmn/SignalIntermediateThrowEvent'


// Vue.component('svg-org-uengine-kernel-humanactivity', SVGActivity);
// Vue.component('svg-org-uengine-kernel-defaultactivity', SVGActivity);
// Vue.component('svg-org-uengine-kernel-bpmn-startevent', SVGEvent);
// Vue.component('svg-org-uengine-kernel-bpmn-endevent', SVGEvent);
// Vue.component('svg-org-uengine-kernel-bpmn-gateway', SVGGateway);
// Vue.component('svg-org-uengine-kernel-bpmn-parallelgateway', SVGGateway);
// Vue.component('org-uengine-kernel-bpmn-startevent', PropertyPanelStartEvent);
// Vue.component('org-uengine-kernel-defaultactivity', PropertyPanelStartEvent);
// Vue.component('org-uengine-kernel-humanactivity', PropertyPanelStartEvent);
window.componentsList = [];

console.log(window.componentsList);

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
          component: SvgTest,
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
          component: Graph,
          beforeEnter: RouterGuard.requireUser,
          props: {monitor: true}
        },
        {
          path: 'svg',
          name: 'svg',
          component: SvgTest,
          beforeEnter: RouterGuard.requireUser
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

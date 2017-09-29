<template>
  <div>
    <group-element
      selectable
      movable
      resizable
      connectable
      deletable
      :enableFrom="false"
      :id.sync="activity.tracingTag"
      :x.sync="activity.elementView.x"
      :y.sync="activity.elementView.y"
      :width.sync="activity.elementView.width"
      :height.sync="activity.elementView.height"
      :_style.sync="style"
      :parentId.sync="activity.elementView.parent"
      :label.sync="activity.name.text"
      v-on:dblclick="showProperty"
      v-on:selectShape="closeComponentChanger"
      v-on:deSelectShape="closeComponentChanger"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
    >
      <geometry-rect
        :_style="{
          'stroke-width': 1.2,
          'r': 6,
          fill: '#FFFFFF',
          'fill-opacity': 0.7,
          'vertical-align': 'top',
          'text-anchor': 'start'
        }"
      >
      </geometry-rect>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </group-element>

    <!--childActivities-->
    <div v-if="bpmnVue && activity.childActivities" v-for="subActivity in activity.childActivities[1]">
      <component v-if="subActivity != null" :is="bpmnVue.getComponentByClassName(subActivity._type)"
                 :activity.sync="subActivity" :definition="definition"
      ></component>
    </div>

    <!--릴레이션은 액티비티간 연결선(흐름)-->
    <div v-if="bpmnVue && activity.sequenceFlows" v-for="subRelation in activity.sequenceFlows">
      <bpmn-relation v-if="subRelation != null" :relation.sync="subRelation"></bpmn-relation>
    </div>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="activity"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>액티비티 명</label>
          <md-input type="text"
                    v-model="activity.name.text"></md-input>
        </md-input-container>
        <md-input-container>
          <label>retryDelay</label>
          <md-input type="number"
                    v-model.number="activity.retryDelay"></md-input>
        </md-input-container>
      </template>
      <template slot="additional-tabs">

      </template>
    </bpmn-property-panel>
  </div>
</template>

<script>
  import IBpmn from '../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-subprocess',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'SubProcess'
      },
      className(){
        return 'org.uengine.kernel.bpmn.SubProcess'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          tracingTag: newTracingTag,
          childActivities: [
            "java.util.ArrayList",
            []
          ],
          sequenceFlows: [],
          elementView: {
            '_type': 'org.uengine.kernel.view.DefaultActivityView',
            'id': newTracingTag,
            'x': x,
            'y': y,
            'width': width,
            'height': height,
            'style': JSON.stringify({})
          }
        }
      }
    },
    data: function () {
      return {};
    },
    watch: {},
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


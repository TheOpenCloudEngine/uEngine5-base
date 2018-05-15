<template>
  <div>
    <group-element
      selectable
      movable
      resizable
      connectable
      deletable
      :id.sync="activity.tracingTag"
      :x.sync="activity.elementView.x"
      :y.sync="activity.elementView.y"
      :width.sync="activity.elementView.width"
      :height.sync="activity.elementView.height"
      :_style.sync="style"
      :parentId.sync="activity.elementView.parent"
      :label.sync="activity.name.text"
      v-on:dblclick="showProperty"
      v-on:selectShape="closeComponentChanger(); selectedActivity();"
      v-on:deSelectShape="closeComponentChanger(); deSelectedActivity();"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
      v-on:addedToGroup="onAddedToGroup"
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
    <div v-if="bpmnVue && activity.childActivities" v-for="childActivity in activity.childActivities[1]">
      <component v-if="childActivity != null" :is="bpmnVue.getComponentByClassName(childActivity._type)"
                 :activity.sync="childActivity" :definition="definition"
      ></component>
    </div>

    <!--릴레이션은 액티비티간 연결선(흐름)-->
    <div v-if="bpmnVue && activity.sequenceFlows" v-for="childRelation in activity.sequenceFlows">
      <bpmn-relation v-if="childRelation != null" :relation.sync="childRelation"></bpmn-relation>
    </div>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="activity"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>Activity Name</label>
          <md-input type="text"
                    v-model="activity.name.text"></md-input>
        </md-input-container>

        <md-subheader>Array Variable for multiplicity</md-subheader>

          <bpmn-variable-selector v-model="activity.forEachVariable" :definition="definition"></bpmn-variable-selector>


        <md-subheader>Variable Mappings</md-subheader>
        <bpmn-parameter-contexts
          :parameter-contexts="activity.variableBindings"
          :definition="definition"
          :for-sub-process="true"
        ></bpmn-parameter-contexts>

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
          selected: false,
          childActivities: [
            "java.util.ArrayList",
            []
          ],
          sequenceFlows: [
          ],
          variableBindings: [
           "java.util.ArrayList",
           []
          ],
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


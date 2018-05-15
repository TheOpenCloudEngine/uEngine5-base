<template>
  <div>
    <geometry-element
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
      <geometry-circle
        :center="[50,50]"
        :radius="50"
        >
      </geometry-circle>
            <geometry-circle
              :center="[50,50]"
              :radius="40"
              :_style="{
                                       'stroke-width': 1
                                     }"
              >
            </geometry-circle>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
        <!-- 위 선 -->
        <geometry-line
          :from="[30,20]"
          :to="[70,20]"></geometry-line>
        <!-- 오른쪽 선 -->
        <geometry-line
          :from="[70,20]"
          :to="[70,80]"></geometry-line>
        <!-- 아래 선-->
        <geometry-line
          :from="[30,80]"
          :to="[70,80]"></geometry-line>
        <!-- 왼쪽 선-->
        <geometry-line
          :from="[30,20]"
          :to="[30,80]"></geometry-line>
        <!-- 위 -->
        <geometry-line
          :from="[33,32]"
          :to="[67,32]"></geometry-line>
        <geometry-line
          :from="[33,44]"
          :to="[67,44]"></geometry-line>
        <geometry-line
          :from="[33,56]"
          :to="[67,56]"></geometry-line>
        <geometry-line
          :from="[33,68]"
          :to="[67,68]"></geometry-line>
      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </geometry-element>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="activity"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>Event Name</label>
          <md-input type="text"
                    v-model="activity.name.text"></md-input>
        </md-input-container>

<md-subheader>Condition</md-subheader>
        <md-checkbox v-model="complexCondition">Complex Rule</md-checkbox>

        <div v-if="complexCondition">
          <org-uengine-kernel-Or v-model="activity.condition" :definition="definition"></org-uengine-kernel-Or>
        </div>

        <org-uengine-kernel-Evaluate v-else v-model="activity.condition" :definition="definition"></org-uengine-kernel-Evaluate>

        <md-input-container>
          <label>Polling Interval (in seconds)</label>
          <md-input type="number"
                    v-model="activity.pollingIntervalInSecond"></md-input>
        </md-input-container>

        <md-input-container>
          <label>Attach Activity ID</label>
          <md-input type="text"
                    v-model="activity.attachedToRef"></md-input>
        </md-input-container>
      </template>
      <template slot="additional-tabs">

      </template>
    </bpmn-property-panel>
  </div>
</template>

<script>
  import IBpmn from '../../IBpmn'

  export default {
    mixins: [IBpmn],
    name: 'bpmn-conditional-intermediate-catch-event',
    props: {},
    created: function(){

      if(this.activity.condition && (this.activity.condition._type.indexOf('Or') > -1 || this.activity.condition._type.indexOf('And') > -1)){
        this.complexCondition = true;
      }

    },
    computed: {
      defaultStyle(){
        return {
          'label-position': 'bottom',
          'label-width': 120,
          'stroke-width': 1.5
        }
      },
      type(){
        return 'IntermediateEvent'
      },
      className(){
        return 'org.uengine.kernel.bpmn.ConditionalCatchEvent'
      },

      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          tracingTag: newTracingTag,
          selected: false,
          pollingIntervalInSecond: 5,
          condition: {
                      _type: 'org.uengine.kernel.Evaluate',
                      pv: {
                        _type: 'org.uengine.kernel.ProcessVariable',
                        name: ''
                      },
                      condition: '==',
                      val: ''
                      //conditionExpression: 'arg2=="call"'
                    },
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
      return {
        complexCondition: false
      };
    },
    watch: {
       complexCondition: function(val){
           if(val){

             if(this.activity.condition && (this.activity.condition._type.indexOf("Or") > -1 || this.activity.condition._type.indexOf("And") > -1)){
               return;
             }

             var existingCondition = this.activity.condition;

             this.activity.condition.conditionsVt = [];
             this.activity.condition._type="org.uengine.kernel.Or";

           }else{
             if(this.activity.condition && (this.activity.condition._type.indexOf("Or") > -1 || this.activity.condition._type.indexOf("And") > -1)){
               this.complexCondition = true; //not changeable to simple condition.
             }
           }
       }
    },
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


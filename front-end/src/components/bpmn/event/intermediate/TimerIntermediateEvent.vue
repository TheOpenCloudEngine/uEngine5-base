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
                                       'stroke-width': 1.1
                                     }"
              >
            </geometry-circle>

      <geometry-circle
        :center="[50,50]"
        :radius="28"
        :_style="{
          'stroke-width': 0.5,
          fill: '#FFFF00'
        }"
      >
      </geometry-circle>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
        <geometry-line
          :from="[58,28]"
          :to="[48,52]"></geometry-line>
        <geometry-line
          :from="[68.1,53]"
          :to="[48,52]"></geometry-line>

      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </geometry-element>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="activity"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>Name</label>
          <md-input type="text"
                    v-model="activity.name.text"></md-input>
        </md-input-container>

        <md-input-container>
          <label>Schedule Type</label>
          <md-select
                    v-model="activity.scheduleType">
            <md-option value="cron">Cron Expression</md-option>
            <md-option value="sec">Interval in second</md-option>
            <br>
            <md-subheader>Available in Enterprise Edition</md-subheader>
            <md-option value="min">Interval in minute</md-option>
            <md-option value="hour">Interval in hour</md-option>
            <md-option value="milisec">Interval in mili-second</md-option>

          </md-select>
        </md-input-container>

        <md-input-container v-if="activity.scheduleType=='cron'">
          <label>Quartz Cron Expression</label>
          <md-input type="text"
                    v-model="activity.expression"></md-input>
          <a href="http://www.cronmaker.com/" target="_blank">Cron Maker</a>
        </md-input-container>
        <md-input-container v-if="activity.scheduleType=='sec'">
          <label>Interval in Second</label>
          <md-input type="number"
                    v-model="activity.expression"></md-input>
        </md-input-container>


        <md-input-container>
          <label>retryDelay</label>
          <md-input type="number"
                    v-model.number="activity.retryDelay"></md-input>
        </md-input-container>
        <md-input-container>
          <label>부착 액티비티 ID</label>
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
    name: 'bpmn-timer-intermediate-event',
    props: {},
    computed: {
      defaultStyle(){
        return {
          'label-position': 'bottom',
          'stroke-width': 1.5
        }
      },
      type(){
        return 'IntermediateEvent'
      },
      className(){
        return 'org.uengine.kernel.bpmn.TimerIntermediateEvent'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          tracingTag: newTracingTag,
          selected: false,
          scheduleType: "sec",
          expression: "10",
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


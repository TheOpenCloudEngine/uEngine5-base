<template>
  <div>
    <geometry-element
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
      <geometry-circle
        :center="[50,50]"
        :radius="50"
        :_style="{
        'stroke-width': 3
        }"
      >
      </geometry-circle>
      <geometry-polygon
        :vertices="[[15, 50],[45, 70],[45, 30]]"
        :_style="{
        'fill': 'black',
        'fill-opacity': 1
      }"
      >
      </geometry-polygon>
      <geometry-polygon
        :vertices="[[45, 50],[75, 70],[75, 30]]"
        :_style="{
        'fill': 'black',
        'fill-opacity': 1
      }"
      >
      </geometry-polygon>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </geometry-element>

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
  import IBpmn from '../../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-compensation-end-event',
    props: {},
    computed: {
      defaultStyle(){
        return {
          'label-position': 'bottom'
        }
      },
      type(){
        return 'EndEvent'
      },
      className(){
        return 'org.uengine.kernel.bpmn.EndEvent'
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


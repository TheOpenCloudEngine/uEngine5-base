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
    >
      <geometry-circle
        :center="[50,50]"
        :radius="5"
        :_style="{
        'stroke-width': 3
        }"
      >
      </geometry-circle>
      <geometry-circle
        :center="[50,50]"
        :radius="5"
        :_style="{
        'fill': 'black',
        'fill-opacity': 1
        }"
      >
      </geometry-circle>
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
        <v-layout row wrap class="pa-3">
          <v-flex xs12>
            <v-text-field
              label="액티비티 명"
              v-model="activity.name.text"
            ></v-text-field>
          </v-flex>
          <v-flex xs12>
            <v-text-field
              type="number"
              label="retryDelay"
              v-model.number="activity.retryDelay"
            ></v-text-field>
          </v-flex>
        </v-layout>
      </template>
      <template slot="additional-tabs">

      </template>
      <template slot="additional-tabs-contents">

      </template>
    </bpmn-property-panel>
  </div>
</template>

<script>
  import IBpmn from '../../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-terminate-end-event',
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


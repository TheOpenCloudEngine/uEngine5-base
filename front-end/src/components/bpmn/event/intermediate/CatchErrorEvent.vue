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
        :radius="44"
        >
      </geometry-circle>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
        <!-- 오른쪽 짧은 선 -->
        <geometry-line
          :from="[76,30]"
          :to="[61,63]"></geometry-line>
        <!-- 오른쪽 긴 선 -->
        <geometry-line
          :from="[76,30]"
          :to="[61,81]"></geometry-line>
        <!-- 왼쪽 잛은 선-->
        <geometry-line
          :from="[24,70]"
          :to="[39,37]"></geometry-line>
        <!-- 왼쪽 긴 선-->
        <geometry-line
          :from="[24,70]"
          :to="[39,19]"></geometry-line>
        <!-- 위 -->
        <geometry-line
          :from="[39,19]"
          :to="[61,63]"></geometry-line>
        <!-- 아래 -->
        <geometry-line
          :from="[61,81]"
          :to="[39,37]"></geometry-line>
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
    name: 'bpmn-error-catch-event',
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
    return 'org.uengine.kernel.bpmn.CatchingErrorEvent'
  },
  createNew(newTracingTag, x, y, width, height){
    return {
      _type: this.className(),
      name: {
        text: ''
      },
      tracingTag: newTracingTag,
      selected: false,
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


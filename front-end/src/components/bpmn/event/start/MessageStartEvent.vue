<template>
  <div>
    <geometry-element
      selectable
      movable
      resizable
      connectable
      deletable
      :enableTo="false"
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
      <sub-elements
        >
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
        <rectangle-element
          :sub-width="'65%'"
          :sub-height="'40%'"
          :sub-align="'center'"
          :sub-top="'29%'">
        </rectangle-element>
        <geometry-line
          :from="[17.4,29.2]"
          :to="[50,50]"></geometry-line>
        <geometry-line
          :from="[82.4,29.2]"
          :to="[50,50]"></geometry-line>
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
          <label>Service Path</label>
          <md-input type="text"
                    v-model="activity.servicePath"></md-input>
        </md-input-container>
        <md-input-container>
          <label>Correlation Key</label>
          <md-input type="text"
                    v-model="activity.correlationKey"></md-input>
        </md-input-container>
        <md-input-container>
          <label>Data for Output</label>
          <md-input type="text"
                    v-model="activity.dataOutput.name"></md-input>
        </md-input-container>

        <p>데이터 속성 별 매핑</p>
        <bpmn-parameter-contexts
          :parameter-contexts="activity.dataOutputMapping"
          :definition="definition"
          label-for-argument="속성"
        ></bpmn-parameter-contexts>

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
    name: 'bpmn-message-start-event',
    props: {},
    computed: {
      defaultStyle(){
        return {
          'label-position': 'bottom',
          'stroke-width': 1.5
        }
      },
      type(){
        return 'StartEvent'
      },
      className(){
        return 'org.uengine.kernel.bpmn.MessageStartEvent'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          dataOutput:{name:''},
          dataOutputMapping: [],
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


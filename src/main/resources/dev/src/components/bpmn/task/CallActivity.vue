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
      v-on:selectShape="closeComponentChanger"
      v-on:deSelectShape="closeComponentChanger"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
    >
      <geometry-rect
        :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 3,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '10'
        }"
      >
      </geometry-rect>

      <sub-elements>

        <bpmn-loop-type :loopType="loopType"></bpmn-loop-type>
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
          <label>연결 프로세스 정의</label>

          <!--TODO: 실제 프로세스 정의 목록에서 혹은 검색으로 가져와야 함 -->
          <md-select name="movie" id="movie" v-model="activity.definitionId">
            <md-option :value="null">선택</md-option>
            <md-option value="new-process-definition2">new-process-definition2</md-option>
            <md-option value="new-process-definition3">new-process-definition3</md-option>
            <md-option value="new-process-definition4">new-process-definition4</md-option>
          </md-select>
        </md-input-container>


        <p>연결 변수 매핑</p>
        <bpmn-parameter-contexts
          v-if="activity.definitionId"
          :calleeDefinitionId="activity.definitionId"
          :parameterContexts="activity.variableBindings"
          :definition="definition"
          :calleeDefinitionId="activity.definitionId"
        ></bpmn-parameter-contexts>

        <p>연결 역할 매핑</p>
        <bpmn-parameter-contexts
          v-if="activity.definitionId"
          :parameterContexts="activity.roleBindings"
          :definition="definition"></bpmn-parameter-contexts>

        <!--</md-input-container>-->
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
    name: 'bpmn-call-activity',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Task'
      },
      className(){
        return 'org.uengine.kernel.bpmn.CallActivity'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          tracingTag: newTracingTag,
          definitionId: "",
          variableBindings: [],
          roleBindings: [],
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
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


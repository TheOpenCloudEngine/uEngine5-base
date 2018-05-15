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
      <geometry-rect
        :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 1.2,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '10'
        }"
      >
      </geometry-rect>

      <sub-elements>
        <image-element
          v-bind:image="script_image"
          :sub-width="'20px'"
          :sub-height="'20px'"
          :sub-top="'5px'"
          :sub-left="'5px'"
        >
        </image-element>

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
          <!--<md-button @click="changeName">change name</md-button>-->
        </md-input-container>
        <md-input-container>
          <label>실행 언어</label>
          <md-select type="text"
                     v-model="activity.language">

            <md-option :value="0">Javascript</md-option>
            <md-option :value="1">Java</md-option>

          </md-select>
        </md-input-container>
        <md-input-container>
          <label>스크립트</label>
          <md-textarea v-model="activity.script"></md-textarea>

        </md-input-container>
        <md-input-container>
          <label>실행 결과 변수 매핑</label>
          <md-textarea v-model="activity.out.name"></md-textarea>

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
    name: 'bpmn-script-task',
    props: {},
    created: function(){
      if(!this.activity.role)
          this.activity.role = {name:''};
    },
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Task'
      },
      className(){
        return 'org.uengine.kernel.ScriptActivity'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          out: {
            name: ''
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
      return {
          links: null,
          script_image: location.pathname + ((location.pathname == '/' || location.pathname.lastIndexOf('/') > 0) ? '' : '/') + 'static/image/symbol/Script.png'
      };
    },
    mounted: function () {

    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">
</style>

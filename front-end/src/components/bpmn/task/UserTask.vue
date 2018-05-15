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
          v-bind:image="user_image"
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
        </md-input-container>
        <md-input-container>
          <label>역할</label>
          <!--<md-select v-model="activity.role.name">-->
          <!--<md-option v-for="role in definition.roles"-->
          <!--:key="role.name"-->
          <!--:value="role.name">-->
          <!--{{ role.name }}-->
          <!--</md-option>-->
          <!--</md-select>-->
          <md-input type="text" readonly
                    v-model="activity.role.name"></md-input>
        </md-input-container>
        <p>파라미터</p>
        <bpmn-parameter-contexts
          :parameter-contexts="activity.parameters"
          :definition="definition"
          :multi="true"
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
  import IBpmn from '../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-user-task',
    props: {},
    created: function () {
      if (!this.activity.role)
        this.activity.role = {name: ''};
    },
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Task'
      },
      className(){
        return 'org.uengine.kernel.HumanActivity'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          role: {
            name: ''
          },
          parameters: [],
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
        user_image: location.pathname + ((location.pathname == '/' || location.pathname.lastIndexOf('/') > 0) ? '' : '/') + 'static/image/symbol/User.png'
      };
    },
    watch: {
      drawer: function (opened) {
        if (opened) {
          this.activity.role.name =
            this.bpmnVue.getWhereRoleAmIByTracingTag(this.activity.tracingTag);
        }
      },

      activity: {
        handler: function(newVal) {
          if (!this.definition._changedByLocaleSelector) {
            if (!this.activity.name.localedTexts) {
              this.activity.name.localedTexts = {
                _type: 'java.util.HashMap'
              };
            }
            this.activity.name.localedTexts[this.definition._selectedLocale] = this.activity.name.text;
          }
          this.definition._changedByLocaleSelector = false;
        },
        deep: true
      }
    },
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">
</style>

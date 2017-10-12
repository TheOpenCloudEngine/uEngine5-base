<template>
  <div>
    <horizontal-lane-element
      selectable
      movable
      resizable
      deletable
      :id.sync="role.elementView.id"
      :x.sync="role.elementView.x"
      :y.sync="role.elementView.y"
      :width.sync="role.elementView.width"
      :height.sync="role.elementView.height"
      :_style.sync="style"
      :parentId.sync="role.elementView.parent"
      :label.sync="role.name"
      v-on:dblclick="showProperty"
      v-on:selectShape="closeComponentChanger"
      v-on:deSelectShape="closeComponentChanger"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
    >
      </geometry-circle>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </horizontal-lane-element>

    <!--<bpmn-property-panel-->
      <!--:drawer.sync="drawer"-->
      <!--:item.sync="role"-->
    <!--&gt;-->
      <!--<template slot="properties-contents">-->
        <!--<md-input-container>-->
          <!--<label>롤 명</label>-->
          <!--<md-input type="text"-->
                    <!--v-model="role.name"></md-input>-->
        <!--</md-input-container>-->
      <!--</template>-->
      <!--<template slot="additional-tabs">-->

      <!--</template>-->
    <!--</bpmn-property-panel>-->
  </div>
</template>

<script>
  import IBpmn from '../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-role',
    props: {},
    computed: {
      defaultStyle: function() {
        return {}
      },
      type: function() {
        return 'Role'
      },
      createNew(x, y, width, height){
        return {
          name: '',
          displayName: {},
          elementView: {
            '_type': 'org.uengine.kernel.view.DefaultActivityView',
            'id': null,//this.uuid(), //오픈그래프 자동 생성
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
    watch: {
      'role.name': function (newVal, oldVal) {
        var me = this;
        //롤의 이름이 변경되었을 때
        //휴먼 액티비티 중 oldname 을 가지고 있는 role 을 같이 변경한다.
        $.each(this.bpmnVue.data.definition.childActivities[1], function (i, activitiy) {
          if (activitiy && activitiy.role && activitiy.role.name == oldVal) {
            activitiy.role = JSON.parse(JSON.stringify(me.role));
            console.log(activitiy.tracingTag + ' Human activitiy role changed by role name updated!!');
          }
        });
      }
    },
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


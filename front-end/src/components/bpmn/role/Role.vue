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
      v-on:selectShape="closeComponentChanger(); selectedActivity();"
      v-on:deSelectShape="closeComponentChanger(); deSelectedActivity();"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
    >
      </geometry-circle>
      <sub-elements>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
      </sub-elements>
      <bpmn-sub-controller :type="type"></bpmn-sub-controller>
    </horizontal-lane-element>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="role"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>Pool/Lane (Role) Name</label>
          <md-input type="text"
                    v-model="role.name"></md-input>
        </md-input-container>

        <md-radio v-model="roleDef" id="roleDef" name="roleDef" md-value="Human">Human</md-radio>
        <md-radio v-model="roleDef" id="roleDef" name="roleDef" md-value="System">System</md-radio>

        <div v-if="roleDef=='Human'">
          <md-radio v-model="roleResolution" id="roleResolution" name="roleResolution" md-value="null">None</md-radio>
          <md-radio v-model="roleResolution" id="roleResolution" name="roleResolution" md-value="org.uengine.five.overriding.IAMRoleResolutionContext">Role Resolution By IAM Scope</md-radio>
          <md-radio v-model="roleResolution" id="roleResolution" name="roleResolution" md-value="org.uengine.kernel.DirectRoleResolutionContext">Role Resolution By Direct user</md-radio>

          <md-input-container v-if="role.roleResolutionContext && role.roleResolutionContext._type == 'org.uengine.five.overriding.IAMRoleResolutionContext'">
            <label>Scope Name</label>
            <md-input v-model="role.roleResolutionContext.scope"></md-input>
          </md-input-container>

          <md-input-container v-if="role.roleResolutionContext && role.roleResolutionContext._type == 'org.uengine.kernel.DirectRoleResolutionContext'">
            <label>User ID</label>
            <md-input v-model="role.roleResolutionContext.endpoint"></md-input>
          </md-input-container>

        </div>


        <div v-if="roleDef=='System'">
          <md-input-container>
            <label>Select serviceId from eureka</label>
            <md-select
                      v-model="role.name">
              <md-option :value="serviceId" v-for="serviceId in serviceIds">{{serviceId}}</md-option>
            </md-select>
          </md-input-container>
         </div>
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
    name: 'bpmn-role',
    props: {},
    created: function(){
      this.loadRolesFromEureka();
    },
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Role'
      },
      createNew(x, y, width, height){
        return {
          name: '',
          displayName: {},
          roleResolutionContext: null,
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
      return {
        serviceIds: null,
        roleDef: 'Human',
        roleResolution: false
      };
    },
    watch: {
      'drawer': function(val){
        this.loadRolesFromEureka();
      },
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
      },
      'roleResolution': function(val){
        if(val){
          if(!this.role.roleResolutionContext)
            this.role.roleResolutionContext = {
              endpoint: null,
              scope: null
            };

          this.role.roleResolutionContext._type = val;

        }else
          this.role.roleResolutionContext = null;
      }
    },
    created: function () {
      if(this.role.roleResolutionContext){
        this.roleResolution = this.role.roleResolutionContext._type;
      }
    },
    methods: {
      loadRolesFromEureka: function(){
         var me = this;

            this.$root.codi('eureka/apps').get()
              .then(function (response) {

                  console.log(response.data);

                  response.data.applications.application.forEach(function(application){
                      if(me.serviceIds == null) me.serviceIds = [];

                      me.serviceIds.push(application.name);

                  });

              });
      }

    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


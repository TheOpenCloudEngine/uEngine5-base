<template>
  <div>
    <edge-element
      selectable
      connectable
      deletable
      :id="relation.sourceRef + '-' + relation.targetRef"
      :vertices.sync="vertices"
      :from.sync="relation.sourceRef"
      :to.sync="relation.targetRef"
      :_style.sync="style"
      :label.sync="relation.name"
      v-on:dblclick="showProperty"
      v-on:selectShape="closeComponentChanger"
      v-on:deSelectShape="closeComponentChanger"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
    >
    </edge-element>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="relation"
      :otherwise.sync="otherwise"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>릴레이션 이름</label>
          <md-input type="text"
                    v-model="relation.name"></md-input>
        </md-input-container>
        <md-layout md-flex="20">
          <md-checkbox v-model="otherwise">otherwise</md-checkbox>
        </md-layout>

        <md-checkbox v-model="complexCondition">복잡 조건</md-checkbox>

        <div v-if="complexCondition">
          <md-input-container>
            <md-select v-model="thisConditionType" md-elevation="2">
              <md-option value="org.uengine.kernel.And">And</md-option>
              <md-option value="org.uengine.kernel.Or">Or</md-option>
            </md-select>
          </md-input-container>

          <org-uengine-kernel-Or v-if="thisConditionType == 'org.uengine.kernel.Or'" :data="relation.condition" :definition="definition"></org-uengine-kernel-Or>
          <org-uengine-kernel-And v-if="thisConditionType == 'org.uengine.kernel.And'" :data="relation.condition" :definition="definition"></org-uengine-kernel-And>
        </div>


        <org-uengine-kernel-Evaluate v-else :data="relation.condition" :definition="definition"></org-uengine-kernel-Evaluate>

      </template>


      <template slot="additional-tabs">
        <md-tab :id="'mapper' + _uid" md-label="Data Mapping">


          <bpmn-mapper :definition="definition"></bpmn-mapper>

        </md-tab>
      </template>
    </bpmn-property-panel>
  </div>
</template>

<script>
  import IBpmn from '../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-relation',
    props: {},
    created: function(){
        console.log('xxx');
    },
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Relation'
      },
      createNew(from, to, vertices){
        return {
          sourceRef: from,
          targetRef: to,
          relationView: {
            style: JSON.stringify({}),
            value: vertices
          },
          condition: {
            _type: 'org.uengine.kernel.Evaluate',
            pv: {
              _type: 'org.uengine.kernel.ProcessVariable',
              name: ''
            },
            condition: '==',
            val: ''
            //conditionExpression: 'arg2=="call"'
          }
        }
      },
      vertices: {
        get: function () {
          var style;
          try {
            return JSON.parse(this.relation.relationView.value);
          } catch (e) {
            return null;
          }
        },
        set: function (val) {
          this.relation.relationView.value = JSON.stringify(val);
        }
      }
    },
    data: function () {
      return {
        otherwise: false,
        complexCondition: false,
        thisConditionType: 'org.uengine.kernel.Or'
      };
    },
    watch: {
      drawer: function (val) {
        //패널 열릴때 other wise 체크
        if (val) {
          if (this.relation.condition._type == 'org.uengine.kernel.Otherwise') {
            this.otherwise = true;
          } else {
            this.otherwise = false;
          }
        }
      },
      otherwise: function (_val) {
        var me = this;
        var condition = {};
        if (_val) { //otherwise 이면
          condition._type = 'org.uengine.kernel.Otherwise'
        } else { //otherwise가 아니면

          this.condition = {
            _type: 'org.uengine.kernel.Evaluate',
            pv: {
              _type: 'org.uengine.kernel.ProcessVariable',
              name: '',
            },
            condition: '==',
            val: ''
          }

        }
        me.relation.condition = condition;
      },
      complexCondition: function(val){
          if(val){
              this.relation.condition.conditionsVt = [];
              this.relation.condition._type="org.uengine.kernel.Or";
          }
      }
    },
    mounted: function () {
    },
    methods: {
      setDefinition: function (def) {
        this.def = def
      },
      setData: function (data) {
        this.data = data
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


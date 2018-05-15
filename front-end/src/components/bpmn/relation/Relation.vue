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
      v-on:selectShape="closeComponentChanger(); selectedFlow();"
      v-on:deSelectShape="closeComponentChanger(); deSelectedFlow();"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
      v-on:addedToGroup="onAddedToGroup"
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
        <md-input-container>
          <label>컨디션</label>
          <md-input type="text"
                    v-model="relation.condition.conditionExpression" v-if="!otherwise"></md-input>
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
    name: 'bpmn-relation-diabled',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Relation'
      },
      createNew(from, to, vertices){
        return {
          selected: false,
          sourceRef: from,
          targetRef: to,
          relationView: {
            style: JSON.stringify({}),
            value: vertices
          },
          condition: {
            _type: 'org.uengine.kernel.ExpressionEvaluateCondition',
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
        otherwise: false
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
          condition._type = 'org.uengine.kernel.ExpressionEvaluateCondition'
          condition.conditionExpression = me.relation.condition.conditionExpression
        }
        me.relation.condition = condition;
      }
    },
    mounted: function () {
    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


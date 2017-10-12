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

    <!--<bpmn-property-panel-->
      <!--:drawer.sync="drawer"-->
      <!--:item.sync="relation"-->
    <!--&gt;-->
      <!--<template slot="properties-contents">-->
        <!--<md-input-container>-->
          <!--<label>릴레이션 이름</label>-->
          <!--<md-input type="text"-->
                    <!--v-model="relation.name"></md-input>-->
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
    name: 'bpmn-relation',
    props: {},
    computed: {
      defaultStyle: function() {
        return {}
      },
      type: function() {
        return 'Relation'
      },
      createNew(from, to, vertices){
        return {
          sourceRef: from,
          targetRef: to,
          relationView: {
            style: JSON.stringify({}),
            value: vertices
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


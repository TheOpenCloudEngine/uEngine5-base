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
        <v-layout row wrap class="pa-3">
          <v-flex xs12>
            <v-text-field
              label="롤 명"
              v-model="role.name"
            ></v-text-field>
          </v-flex>
        </v-layout>
      </template>
      <template slot="additional-tabs">

      </template>
      <template slot="additional-tabs-contents">

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
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Role'
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

  /*네비게이션 패널 넓이*/
  aside.navigation-drawer.navigation-drawer--absolute.navigation-drawer--is-booted.navigation-drawer--open {
    width: 400px;
  }
</style>


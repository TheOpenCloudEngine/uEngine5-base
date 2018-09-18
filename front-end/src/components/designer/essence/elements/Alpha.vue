<template>
  <div>
    <geometry-element
      selectable
      movable
      resizable
      connectable
      deletable
      v-on:dblclick="$refs['dialog'].open()"

      :id.sync="value.tracingTag"
      :x.sync="value.elementView.x"
      :y.sync="value.elementView.y"
      :width.sync="value.elementView.width"
      :height.sync="value.elementView.height"
      :_style.sync="style"
      :parentId.sync="value.elementView.parent"
      :label.sync="value.name"
    >
      <geometry-rect
        :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 1.2,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '15'
        }"

        :x="0"
        :y="0"
        :width="90"
        :height="100"
      >
      </geometry-rect>

      <geometry-line :from="[90,50]" :to="[100,0]">
      </geometry-line>
      <geometry-line :from="[90,50]" :to="[100,100]">
      </geometry-line>

    </geometry-element>


    <md-dialog ref="dialog">
        <modeling-property-panel
              :drawer.sync="drawer"
              v-model="value"
        >
          <template slot="properties-contents">

            <md-input-container>
              <label>알파 명</label>
              <md-input type="text"
                        v-model="value.name"></md-input>
            </md-input-container>
          </template>
          <template slot="additional-tabs">

          </template>
        </modeling-property-panel>
  </md-dialog>


  </div>
</template>

<script>
  import Element from '../../modeling/Element'
  export default {
    mixins: [Element],
    name: 'essence-alpha',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Activity'
      },
      className(){
        return 'org.uengine.esencia.model.Alpha'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name:
            'Alpha'
          ,
          tracingTag: newTracingTag,
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


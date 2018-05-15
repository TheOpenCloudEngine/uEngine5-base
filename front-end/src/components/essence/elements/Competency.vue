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

          <geometry-polygon
           :vertices="[[35.0,7.5],
                                     [37.9,16.1],
                                     [46.9,16.1],
                                     [39.7,21.5],
                                     [42.3,30.1],
                                     [35.0,25.0],
                                     [27.7,30.1],
                                     [30.3,21.5],
                                     [23.1,16.1],
                                     [32.1,16.1]]">
          </geometry-polygon>




    </geometry-element>

        <md-dialog ref="dialog">
            <modeling-property-panel
                  :drawer.sync="drawer"
                  v-model="value"
            >
              <template slot="properties-contents">

                <md-input-container>
                  <label>컴피턴시 명</label>
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
    name: 'essence-competency',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Activity'
      },
      className(){
        return 'org.uengine.esencia.model.Competency'
      },
      createNew(newTracingTag, x, y, width, height){
        return {
          _type: this.className(),
          name: 'Competency',
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


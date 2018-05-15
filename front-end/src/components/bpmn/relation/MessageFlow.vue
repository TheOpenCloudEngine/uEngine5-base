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
      :_style="{ 'stroke-width' : '1.3', 'stroke-dasharray':'- ',
                 'marker': {
                      'end': {
                          'id': 'OG.marker.MessageFlowArrowStartMarker',
                          'size': [8, 6]
                      },
                      'start': {
                          'id': 'OG.marker.MessageFlowArrowEndMarker',
                          'size': [6, 6]
                      }
                  }
                }"
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
          <label>Message Flow Name</label>
          <md-input type="text"
                    v-model="relation.name"></md-input>
        </md-input-container>
          <md-switch v-model="relation.localCall">Local Call</md-switch>
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
    name: 'bpmn-message-flow',
    props: {},

    created: function(){

      if(!OG.marker.MessageFlowArrowStartMarker){
        OG.marker.MessageFlowArrowStartMarker = function () {
                OG.marker.MessageFlowArrowStartMarker.superclass.call(this);
                this.MARKER_ID = 'OG.marker.MessageFlowArrowStartMarker';
              };
        OG.marker.MessageFlowArrowStartMarker.prototype = new OG.marker.IMarker();
        OG.marker.MessageFlowArrowStartMarker.superclass = OG.marker.IMarker;
        OG.marker.MessageFlowArrowStartMarker.prototype.constructor = OG.marker.MessageFlowArrowStartMarker;
        OG.MessageFlowArrowStartMarker = OG.marker.MessageFlowArrowStartMarker;
        OG.marker.MessageFlowArrowStartMarker.prototype.createMarker = function () {
          var geom1, geomCollection = [];
          if (this.geom) {
            return this.geom;
          }
          geom1 = new OG.geometry.Polygon([[20, 15],[50, 51],[20, 85]]);
          geom1.style = new OG.geometry.Style({
            'stroke-width': 0.8,
            fill: '#ffffff',
            'fill-opacity': 1
          });
          geomCollection.push(geom1);
          this.geom = new OG.geometry.GeometryCollection(geomCollection);
          return this.geom;
        };

      }

      if(!OG.marker.MessageFlowArrowEndMarker){
        OG.marker.MessageFlowArrowEndMarker = function () {
                OG.marker.MessageFlowArrowEndMarker.superclass.call(this);
                this.MARKER_ID = 'OG.marker.MessageFlowArrowEndMarker';
              };
        OG.marker.MessageFlowArrowEndMarker.prototype = new OG.marker.IMarker();
        OG.marker.MessageFlowArrowEndMarker.superclass = OG.marker.IMarker;
        OG.marker.MessageFlowArrowEndMarker.prototype.constructor = OG.marker.MessageFlowArrowEndMarker;
        OG.MessageFlowArrowEndMarker = OG.marker.MessageFlowArrowEndMarker;
        OG.marker.MessageFlowArrowEndMarker.prototype.createMarker = function () {
          var geom1, geomCollection = [];
          if (this.geom) {
            return this.geom;
          }
          geom1 = new OG.geometry.Circle([50, 50], 40);
          geom1.style = new OG.geometry.Style({
            'stroke-width': 0.8,
            fill: '#ffffff',
            'fill-opacity': 1
          });
          geomCollection.push(geom1);
          this.geom = new OG.geometry.GeometryCollection(geomCollection);
          return this.geom;
        };

      }


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
          selected: false,
          sourceRef: from,
          _type: 'org.uengine.kernel.bpmn.MessageFlow',
          targetRef: to,
          relationView: {
            style: JSON.stringify({}),
            value: vertices
          },
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
    },
    mounted: function () {
    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


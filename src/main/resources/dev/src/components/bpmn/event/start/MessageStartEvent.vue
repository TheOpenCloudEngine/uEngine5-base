<template>
  <bpmn-property-panel :parentId="id">
    <template slot="properties-contents">
      <v-layout row wrap class="pa-3">
        <v-flex xs12>
          <v-text-field
            label="액티비티 명"
            v-model="activity.name.text"
          ></v-text-field>
        </v-flex>
        <v-flex xs12>
          <v-text-field
            type="number"
            label="retryDelay"
            v-model.number="activity.retryDelay"
          ></v-text-field>
        </v-flex>
      </v-layout>
    </template>
    <template slot="additional-tabs">

    </template>
    <template slot="additional-tabs-contents">

    </template>
  </bpmn-property-panel>
</template>

<script>
  /**
   * 스타트 이벤트의 기본형.
   */
  import StartEvent from './StartEvent'
  export default {
    mixins: [StartEvent],
    name: 'bpmn-message-start-event',
    props: {},
    computed: {
      className(){
        return 'org.uengine.kernel.bpmn.StartEvent'
      },
      shapeId(){
        return 'OG.shape.bpmn.E_Start_Message'
      }
    },
    data: function () {
      var me = this;
      return {}
    },
    watch: {},
    mounted: function () {

    },
    methods: {}
  }


  /**
   * BPMN : Message Start Event Shape
   *
   * @class
   * @extends OG.shape.GeomShape
   * @requires OG.common.*
   * @requires OG.geometry.*
   *
   * @param {String} label 라벨 [Optional]
   * @author <a href="mailto:sppark@uengine.org">Seungpil Park</a>
   * @private
   */
  OG.shape.bpmn.E_Start_Message = function (label) {
    OG.shape.bpmn.E_Start_Message.superclass.call(this);

    this.SHAPE_ID = 'OG.shape.bpmn.E_Start_Message';
    this.label = label;
  };
  OG.shape.bpmn.E_Start_Message.prototype = new OG.shape.bpmn.E_Start();
  OG.shape.bpmn.E_Start_Message.superclass = OG.shape.bpmn.E_Start;
  OG.shape.bpmn.E_Start_Message.prototype.constructor = OG.shape.bpmn.E_Start_Message;
  OG.E_Start_Message = OG.shape.bpmn.E_Start_Message;

  /**
   * 드로잉할 Shape 을 생성하여 반환한다.
   *
   * @return {OG.geometry.Geometry} Shape 정보
   * @override
   */
  OG.shape.bpmn.E_Start_Message.prototype.createShape = function () {
    var geom1, geom2, geom3, geomCollection = [];
    if (this.geom) {
      return this.geom;
    }

    geom1 = new OG.geometry.Circle([50, 50], 50);
    geom1.style = new OG.geometry.Style({
      "stroke-width": 1.5
    });

    geom2 = new OG.geometry.PolyLine([
      [20, 25],
      [50, 45],
      [80, 25],
      [20, 25]
    ]);

    geom3 = new OG.geometry.PolyLine([
      [20, 35],
      [20, 70],
      [80, 70],
      [80, 35],
      [50, 55],
      [20, 35]
    ]);

    geomCollection.push(geom1);
    geomCollection.push(geom2);
    geomCollection.push(geom3);

    this.geom = new OG.geometry.GeometryCollection(geomCollection);
    this.geom.style = new OG.geometry.Style({
      'label-position': 'bottom'
    });

    return this.geom;
  };

</script>


<style scoped lang="scss" rel="stylesheet/scss">

  /*네비게이션 패널 넓이*/
  aside.navigation-drawer.navigation-drawer--absolute.navigation-drawer--is-booted.navigation-drawer--open {
    width: 400px;
  }
</style>


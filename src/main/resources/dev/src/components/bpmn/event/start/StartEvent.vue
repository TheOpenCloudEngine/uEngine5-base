<template>
  <bpmn-property-panel :parentId="_uid">
    <template slot="tabs">
      <v-tabs-item ripple :href="'#properties' + _uid">
        Properties
      </v-tabs-item>
    </template>
    <template slot="tabs-contents">
      <v-tabs-content :id="'properties' + _uid">
        <v-layout row wrap class="pa-3">
          <v-flex xs12>
            <v-text-field
              label="액티비티 ID"
              counter
              max="50"
              v-model="tracingTag"
              :rules="[rules.required, rules.tracingTag]"
            ></v-text-field>
          </v-flex>
          <v-flex xs12>
            <v-text-field
              label="액티비티 명"
              v-model="activity.name.text"
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-tabs-content>
    </template>
  </bpmn-property-panel>
</template>

<script>
  /**
   * 스타트 이벤트의 기본형.
   */
  import BaseEvent from '../BaseEvent'
  export default {
    mixins: [BaseEvent],
    name: 'bpmn-start-event',
    props: {},
    computed: {
      className(){
        return 'org.uengine.kernel.bpmn.StartEvent'
      },
      shapeId(){
        return 'OG.shape.bpmn.E_Start'
      }
    },
    data: function () {
      var me = this;
      return {
        name: this.activity.name.text,
        tracingTag: this.activity.tracingTag,
        rules: {
          required: function (value) {
            if (!value || value.length < 1) {
              return 'Required.';
            } else {
              return true;
            }
          },
          tracingTag: function (value) {
            //동일함.
            if (me.activity.tracingTag == value) {
              return true;
            }
            //이미 있음.
            else if (me.$parent.checkExistTracingTag(value)) {
              return 'TracingTag aleardy exist.';
            }
            //트레이싱 태그 값이 바뀜.
            else if (value && value.length > 0) {
              me.activity.tracingTag = value;
              return true;
            }
          }
        }
      }
    },
    watch: {},
    mounted: function () {

    },
    methods: {}
  }


  /**
   * BPMN : Start Event Shape
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
  OG.shape.bpmn.E_Start = function (label) {
    OG.shape.bpmn.E_Start.superclass.call(this);

    this.SHAPE_ID = 'OG.shape.bpmn.E_Start';
    this.label = label;
    this.inclusion = false;
  };
  OG.shape.bpmn.E_Start.prototype = new OG.shape.bpmn.Event();
  OG.shape.bpmn.E_Start.superclass = OG.shape.bpmn.Event;
  OG.shape.bpmn.E_Start.prototype.constructor = OG.shape.bpmn.E_Start;
  OG.E_Start = OG.shape.bpmn.E_Start;

  /**
   * 드로잉할 Shape 을 생성하여 반환한다.
   *
   * @return {OG.geometry.Geometry} Shape 정보
   * @override
   */
  OG.shape.bpmn.E_Start.prototype.createShape = function () {
    if (this.geom) {
      return this.geom;
    }

    this.geom = new OG.geometry.Circle([50, 50], 50);
    this.geom.style = new OG.geometry.Style({
      'label-position': 'bottom',
      "stroke-width": 1.5
    });

    return this.geom;
  };


  OG.shape.bpmn.E_Start.prototype.createController = function () {
    //선연결 컨트롤러
    var me = this;
    var controllers = [
      {
        image: 'event_end.png',
        create: {
          shape: 'OG.E_End',
          width: 30,
          height: 30,
          style: {}
        }
      },
      {
        image: 'event_intermediate.png',
        create: {
          shape: 'OG.E_Intermediate',
          width: 30,
          height: 30,
          style: {}
        }
      },
      {
        image: 'gateway_exclusive.png',
        create: {
          shape: 'OG.G_Exclusive',
          width: 30,
          height: 30,
          style: {}
        }
      },
      {
        image: 'annotation.png',
        create: {
          shape: 'OG.M_Annotation',
          width: 120,
          height: 30,
          style: {}
        }
      },
      {
        image: 'task.png',
        create: {
          shape: 'OG.A_Task',
          width: 100,
          height: 100,
          style: {}
        }
      },
      {
        image: 'wrench.png',
        action: function (element) {
          $(me.currentCanvas.getRootElement()).trigger('changeMenu', [element]);
        }
      }
    ];
    return controllers;
  };
</script>


<style scoped lang="scss" rel="stylesheet/scss">

  /*네비게이션 패널 넓이*/
  aside.navigation-drawer.navigation-drawer--absolute.navigation-drawer--is-booted.navigation-drawer--open {
    width: 400px;
  }
</style>


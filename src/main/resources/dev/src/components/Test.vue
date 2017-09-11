<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <opengraph>
      <template>
        <geometry-element :angle="30" :id.sync="id" :x.sync="x" :y="y" :width.sync="width" :height.sync="height">
          <geometry-rect :upper-left="[0,0]" :width="1000" :height="1000" :_style="{stroke: 'red'}"></geometry-rect>
          <geometry-polyline :vertices="[[0,0],[500,200],[1000,0]]" :_style="{stroke: 'blue'}">
          </geometry-polyline>
          <geometry-rect :upper-left="[30,30]" :width="500" :height="200" :_style="{stroke: 'red'}" v-if="newgeom">
          </geometry-rect>
          <sub-elements>
            <geometry-element :sub-width="'50%'" :sub-height="'50%'" :sub-top="'30px'" :sub-left="'-50%'">
              <geometry-rect :_style="{stroke: 'orange'}"></geometry-rect>
            </geometry-element>
          </sub-elements>

          <sub-controller cloneable :image="'task.png'">
            <geometry-element :width="150" :height="150">
              <geometry-rect :upper-left="[0,0]" :width="1000" :height="1000" :_style="{stroke: 'red'}"></geometry-rect>
            </geometry-element>
          </sub-controller>

          <sub-controller :image="'wrench.png'" v-on:click="sayHello"></sub-controller>
        </geometry-element>

        <text-element :id="'BBB'" :x="600" :y="300" :width="200" :height="100" :text="'hello'" :angle="45">
        </text-element>

        <ellipse-element :id="'AAA'" :x="500" :y="100" :width="200" :height="100" :label="'hello'"></ellipse-element>

        <group-element :x="400" :y="500" :width="200" :height="100" :label="'hello'" :_style="{stroke:'black'}">
        </group-element>

        <horizontal-lane-element :x="700" :y="300" :width="400" :height="200" :label="'hello'"
                                 :_style="{stroke:'black'}">
        </horizontal-lane-element>

        <edge-element :vertices="[[100,100],[300,300],[400,400]]" :from="'AAA'" :to="'BBB'" :label="'hello'"></edge-element>
      </template>
    </opengraph>

    <v-card v-if="!monitor" class="grey lighten-4 tools">
      <v-card-text>
        <span class="icons bpmn-icon-hand-tool"></span>
        <span class="icons bpmn-icon-lasso-tool"></span>
        <span class="icons bpmn-icon-space-tool"></span>
        <span class="icons bpmn-icon-connection-multi"></span>

        <hr class="separator">

        <span v-for="item in dragItems"
              class="icons draggable" :class="item.icon"
              :_shape_type="item._shape_type"
              :_shape_id="item._shape_id"
              :_width="item._width"
              :_height="item._height"
        ></span>

      </v-card-text>
    </v-card>

    <v-card v-if="!monitor" class="grey lighten-4 import">
      <v-card-text>
        <span class="icons fa fa-folder-open"></span>
        <span class="icons fa fa-cloud-upload"></span>
      </v-card-text>
    </v-card>

    <v-card v-if="!monitor" class="grey lighten-4 export">
      <v-card-text>
        <span class="icons fa fa-download"></span>
        <span class="icons fa fa-picture-o"></span>
      </v-card-text>
    </v-card>

    <v-card v-if="!monitor" class="grey lighten-4 history">
      <v-card-text>
        <span class="icons fa fa-undo" v-on:click="undo"></span>
        <span class="icons fa fa-repeat" v-on:click="redo"></span>
      </v-card-text>
    </v-card>

    <v-card v-if="shapeMenu" class="grey lighten-4 shapeMenu">
      <v-card-text>
        <span class="icons fa fa-download"></span>
        <span class="icons fa fa-picture-o"></span>
      </v-card-text>
    </v-card>


    <v-card class="grey lighten-4 zoom">
      <v-card-text>
        <span class="icons fa fa-arrows-alt"></span>

        <hr class="separator">

        <span class="icons fa fa-plus-square-o"></span>
        <span class="icons fa fa-minus-square-o"></span>
      </v-card-text>
    </v-card>
  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean
    },
    data () {
      return {
        x: 300,
        y: 300,
        width: 200,
        height: 200,
        newgeom: true,
        id: null,
        definition: null,
        definitionName: null,
        processVariables: [],
        dialog: false,
        items: [],
        mode: 'editor',
        shapeMenu: false,
        dragItems: [
          {
            'icon': 'bpmn-icon-start-event-none',
            '_shape_id': 'OG.shape.bpmn.E_Start',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-intermediate-event-none',
            '_shape_id': 'OG.shape.bpmn.E_Intermediate',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-end-event-none',
            '_shape_id': 'OG.shape.bpmn.E_End',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-gateway-xor',
            '_shape_id': 'OG.shape.bpmn.G_Exclusive',
            '_width': '40',
            '_height': '40'
          },
          {
            'icon': 'bpmn-icon-task',
            '_shape_id': 'OG.shape.bpmn.A_Task',
            '_width': '100',
            '_height': '100'
          },
          {
            'icon': 'bpmn-icon-subprocess-expanded',
            '_shape_id': 'OG.shape.bpmn.A_Subprocess',
            '_width': '200',
            '_height': '150'
          },
          {
            'icon': 'bpmn-icon-data-object',
            '_shape_id': 'OG.shape.bpmn.D_Data',
            '_width': '50',
            '_height': '50'
          },
          {
            'icon': 'bpmn-icon-data-store',
            '_shape_id': 'OG.shape.bpmn.D_Store',
            '_width': '50',
            '_height': '50'
          },
          {
            'icon': 'bpmn-icon-lane',
            '_shape_id': 'OG.shape.VerticalPoolShape',
            '_width': '300',
            '_height': '300'
          },
          {
            'icon': 'bpmn-icon-participant',
            '_shape_id': 'OG.shape.HorizontalLaneShape',
            '_width': '400',
            '_height': '200'
          }
        ]
      }
    },
    computed: {},
    mounted() {
      var me = this;
//      setTimeout(function () {
//        me.id = 'KKK';
//        me.width = 300;
//        me.height = 300;
//        me.newgeom = false;
//      }, 1000)
    },
    watch: {
      x: function (newVal, oldVal) {
        console.log('x', newVal);
      },
      y: function (newVal, oldVal) {
        console.log('y', newVal);
      },
      width: function (newVal, oldVal) {
        console.log('width', newVal);
      },
      height: function (newVal, oldVal) {
        console.log('height', newVal);
      },
    },
    methods: {
      sayHello: function (cloneComponent) {
        console.log('cloneComponent', cloneComponent);
      },
      undo: function () {

      },
      redo: function () {

      }
    }
  }
</script>

<style scoped lang="scss" rel="stylesheet/scss">
  .canvas-panel {
    top: 0px;
    bottom: 0px;
    left: 0px;
    right: 0px;
    position: absolute;
    overflow: hidden;

    .tools {
      position: absolute;
      width: 42px;
      left: 20px;
      top: 20px;

      .card__text {
        padding: 0px;
      }
    }

    .import, .export, .zoom, .save, .history {
      position: absolute;
      .card__text {
        padding: 10px 10px 10px 0px;
        height: 42px;
      }
      .icons {
        font-size: 25px;
        margin-left: 10px;
      }
    }
    .import {
      left: 80px;
      bottom: 20px;
    }
    .export {
      left: 180px;
      bottom: 20px;
    }
    .history {
      left: 280px;
      bottom: 20px;
    }
    .zoom {
      right: 20px;
      bottom: 120px;
      width: 42px;

      .card__text {
        padding: 0px;
        height: auto;
      }
      .icons {
        font-size: 25px;
        margin-top: 10px;
      }
    }

    .icons {
      cursor: pointer;
      font-size: 30px;
      &:hover {
        color: #ffc124;
      }
    }
  }
</style>


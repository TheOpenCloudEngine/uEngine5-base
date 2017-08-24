<template>
  <div class="canvas-panel">
    <div class="canvas" :id="id">

    </div>

    <v-card class="grey lighten-4 tools">
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

    <v-card class="grey lighten-4 import">
      <v-card-text>
        <span class="icons fa fa-folder-open"></span>
        <span class="icons fa fa-cloud-upload"></span>
      </v-card-text>
    </v-card>

    <v-card class="grey lighten-4 export">
      <v-card-text>
        <span class="icons fa fa-download"></span>
        <span class="icons fa fa-picture-o"></span>
      </v-card-text>
    </v-card>


    <v-layout row wrap>
      <v-flex xs7>

      </v-flex>
      <v-flex xs3>
        <v-text-field
          v-model="id"
          name="input-1-3"
          label="MyProcess"
          single-line
        ></v-text-field>
      </v-flex>
      <v-flex xs2>
        <v-btn fab dark class="cyan" v-on:click="save">
          <v-icon dark>edit</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>

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
    data () {
      return {
        id: null,
        canvas: null,
        dragItems: [
          {
            'icon': 'bpmn-icon-start-event-none',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.E_Start',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-intermediate-event-none',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.E_Intermediate',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-end-event-none',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.E_End',
            '_width': '30',
            '_height': '30'
          },
          {
            'icon': 'bpmn-icon-gateway-xor',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.G_Exclusive',
            '_width': '40',
            '_height': '40'
          },
          {
            'icon': 'bpmn-icon-task',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.A_Task',
            '_width': '100',
            '_height': '100'
          },
          {
            'icon': 'bpmn-icon-subprocess-expanded',
            '_shape_type': 'GROUP',
            '_shape_id': 'OG.shape.bpmn.A_Subprocess',
            '_width': '200',
            '_height': '150'
          },
          {
            'icon': 'bpmn-icon-data-object',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.D_Data',
            '_width': '50',
            '_height': '50'
          },
          {
            'icon': 'bpmn-icon-data-store',
            '_shape_type': 'GEOM',
            '_shape_id': 'OG.shape.bpmn.D_Store',
            '_width': '50',
            '_height': '50'
          },
          {
            'icon': 'bpmn-icon-lane',
            '_shape_type': 'GROUP',
            '_shape_id': 'OG.shape.VerticalPoolShape',
            '_width': '300',
            '_height': '300'
          },
          {
            'icon': 'bpmn-icon-participant',
            '_shape_type': 'GROUP',
            '_shape_id': 'OG.shape.HorizontalLaneShape',
            '_width': '400',
            '_height': '200'
          }
        ]
      }
    },
    mounted() {
      this.getDefinition();

    },
    watch: {
      '$route'(to, from) {
        this.getDefinition();
      }
    },
    methods: {
      save: function () {
        var me = this;
        if (!me.canvas) {
          return;
        }
        var adaptor = new TypedJsonAdaptor(me.canvas);
        var exportJson = adaptor.exportJson();
        console.log(exportJson);
        this.$root.codi('definition{/id}').save({id: this.id + '.json'}, exportJson)
          .then(function (response) {
            console.log(response);
          });

      },
      getDefinition: function () {
        var me = this;
        this.id = this.$route.params.id;

        //신규 생성
        if (this.id == 'new-process-definition') {
          setTimeout(function () {
            me.render();
          }, 100);
        }
        else {
          this.$root.codi('definition{/id}').get({id: this.id + '.json'})
            .then(function (response) {
              me.render(response.data);
            })
        }
      },
      render: function (data) {
        //canvas = new OG.Canvas('canvas', [1000, 800], 'transparent');
        var canvas = new OG.Canvas(this.id, [1000, 800], '#f7f7f7', 'url(/static/image/grid.gif)');
        canvas._CONFIG.DEFAULT_STYLE.EDGE["edge-type"] = "plain";
        canvas._CONFIG.GUIDE_CONTROL_LINE_NUM = 1;
        canvas._CONFIG.FOCUS_CANVAS_ONSELECT = true;
        canvas._CONFIG.WHEEL_SCALABLE = true;
        canvas._CONFIG.DRAG_PAGE_MOVABLE = true;
        canvas._CONFIG.AUTOMATIC_GUIDANCE = false;
        canvas._CONFIG.IMAGE_BASE = '/static/image/symbol/';
        canvas._CONFIG.POOL_DROP_EVENT = true;

        canvas.initConfig({
          selectable: true,
          dragSelectable: true,
          movable: true,
          resizable: true,
          connectable: true,
          selfConnectable: true,
          connectCloneable: true,
          connectRequired: true,
          labelEditable: true,
          groupDropable: true,
          collapsible: true,
          enableHotKey: true,
          enableContextMenu: true,
          useSlider: false,
          stickGuide: true,
          checkBridgeEdge: true,
          autoHistory: false
        });

        this.canvas = canvas;

        var adaptor;
        var json;
        if (data) {
          adaptor = new TypedJsonAdaptor(canvas);
          adaptor.importJson(data);
        }

        //버튼 이벤트 등록
        this.bindEvents();
      },
      bindEvents: function () {

        //this.$el
        var me = this;
        var el = me.$el;
        var canvasEl = $(el).find('.canvas');

        //console.log($(el).find('.draggable'));
        $(el).find('.draggable').draggable({
          start: function () {
            console.log(123);
            canvasEl.data('DRAG_SHAPE', {
              '_shape_type': $(this).attr('_shape_type'),
              '_shape_id': $(this).attr('_shape_id'),
              '_width': $(this).attr('_width'),
              '_height': $(this).attr('_height')
            });
          },
          helper: 'clone',
          appendTo: canvasEl
        });

        //var canvasEl = $(el).find(".canvas");
        canvasEl.droppable({
          drop: function (event, ui) {
            var shapeInfo = canvasEl.data('DRAG_SHAPE'), shape, element;
            if (shapeInfo) {
              shape = eval('new ' + shapeInfo._shape_id + '()');
              var dropX = event.pageX - canvasEl.offset().left + canvasEl[0].scrollLeft;
              var dropY = event.pageY - canvasEl.offset().top + canvasEl[0].scrollTop;
              dropX = dropX / me.canvas._CONFIG.SCALE;
              dropY = dropY / me.canvas._CONFIG.SCALE;
              element = me.canvas.drawShape([dropX, dropY],
                shape, [parseInt(shapeInfo._width, 10), parseInt(shapeInfo._height, 10)]);
              canvasEl.removeData('DRAG_SHAPE');
            }
          }
        });
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

    .canvas {
      position: absolute;
      width: 100%;
      height: 100%;
      background: #f7f7f7;
      overflow: scroll;
    }

    .tools {
      position: absolute;
      width: 42px;
      left: 20px;
      top: 20px;

      .card__text {
        padding: 0px;
      }
    }

    .import, .export, .zoom, .save {
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

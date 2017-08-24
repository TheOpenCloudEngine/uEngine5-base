<template>
  <div class="canvas-panel">
    <div class="canvas">
      <div class="canvas-space" style="width:2000px;height: 2000px" :id="id"></div>
    </div>
    <div :id="sliderId">
    </div>

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

    <v-card v-if="shapeMenu" class="grey lighten-4 shapeMenu">
      <v-card-text>
        <span class="icons fa fa-download"></span>
        <span class="icons fa fa-picture-o"></span>
      </v-card-text>
    </v-card>


    <v-layout row wrap>
      <v-flex xs6>

      </v-flex>
      <v-flex xs3>
        <v-text-field
          v-model="id"
          name="input-1-3"
          label="MyProcess"
          single-line
        ></v-text-field>
      </v-flex>
      <v-flex xs3 v-if="!monitor">
        <v-btn fab dark class="cyan" v-on:click="save">
          <v-icon dark>edit</v-icon>
        </v-btn>
        <v-dialog v-model="dialog" fullscreen transition="dialog-bottom-transition" :overlay=false>
          <v-btn primary dark slot="activator">Variable</v-btn>
          <v-card>
            <v-toolbar dark class="primary">
              <v-btn icon @click.native="dialog = false" dark>
                <v-icon>close</v-icon>
              </v-btn>
              <v-toolbar-title>Process Variable</v-toolbar-title>
              <v-spacer></v-spacer>
              <v-toolbar-items>
                <v-btn dark flat @click.native="dialog = false">Save</v-btn>
              </v-toolbar-items>
            </v-toolbar>
            <object-grid java="org.uengine.kernel.ProcessVariable" :online="false" :data.sync="processVariables"
                         :full-fledged="true">
            </object-grid>
          </v-card>
        </v-dialog>
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
    props: {
      monitor: Boolean
    },
    data () {
      return {
        mode: 'editor',
        shapeMenuContents: null,
        shapeMenuHeaders: null,
        shapeMenuTarget: null,
        shapeMenu: false,
        processVariables: [],
        dialog: false,
        id: null,
        sliderId: null,
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
        ],
        shapeIdMappings: {
          /**
           * Start Event
           */
          startEvent: {
            java: 'org.uengine.kernel.bpmn.StartEvent',
            shapeId: 'OG.shape.bpmn.E_Start',
            properties: {}
          },
          messageStartEvent: {
            java: 'org.uengine.kernel.bpmn.StartEvent',
            shapeId: 'OG.shape.bpmn.E_Start_Message',
            properties: {}
          },
          timerStartEvent: {
            java: 'org.uengine.kernel.bpmn.StartEvent',
            shapeId: 'OG.shape.bpmn.E_Start_Timer',
            properties: {}
          },
          conditionalStartEvent: {
            java: 'org.uengine.kernel.bpmn.StartEvent',
            shapeId: 'OG.shape.bpmn.E_Start_Rule',
            properties: {}
          },
          signalStartEvent: {
            java: 'org.uengine.kernel.bpmn.StartEvent',
            shapeId: 'OG.shape.bpmn.E_Start_Signal',
            properties: {}
          },

          /**
           * Intermediate Event
           */
          intermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate',
            properties: {}
          },
          messageIntermediateCatchEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Message',
            properties: {}
          },
          messageIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Message_Throw',
            properties: {}
          },
          timerIntermediateCatchEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Timer',
            properties: {}
          },
          escalationIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Escalation',
            properties: {}
          },
          conditionalIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Multiple',
            properties: {}
          },
          linkIntermediateCatchEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Link',
            properties: {}
          },
          linkIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Link_Throw',
            properties: {}
          },
          compensationIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Compensation',
            properties: {}
          },
          signalIntermediateCatchEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Signal',
            properties: {}
          },
          signalIntermediateThrowEvent: {
            java: 'org.uengine.kernel.bpmn.Event',
            shapeId: 'OG.shape.bpmn.E_Intermediate_Signal_Throw',
            properties: {}
          },

          /**
           * End Event
           */
          endEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End',
            properties: {}
          },
          escalationEndEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End_Escalation',
            properties: {}
          },
          errorEndEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End_Error',
            properties: {}
          },
          compensationEndEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End_Compensation',
            properties: {}
          },
          signalEndEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End_Signal',
            properties: {}
          },
          terminateEndEvent: {
            java: 'org.uengine.kernel.bpmn.EndEvent',
            shapeId: 'OG.shape.bpmn.E_End_Terminate',
            properties: {}
          },

          /**
           * Gateway
           */
          gateway: {
            java: 'org.uengine.kernel.bpmn.Gateway',
            shapeId: 'OG.shape.bpmn.G_Gateway',
            properties: {}
          },
          parallelGateway: {
            java: 'org.uengine.kernel.bpmn.ParallelGateway',
            shapeId: 'OG.shape.bpmn.G_Parallel',
            properties: {}
          },
          exclusiveGateway: {
            java: 'org.uengine.kernel.bpmn.ExclusiveGateway',
            shapeId: 'OG.shape.bpmn.G_Exclusive',
            properties: {}
          },
          inclusiveGateway: {
            java: 'org.uengine.kernel.bpmn.InclusiveGateway',
            shapeId: 'OG.shape.bpmn.G_Inclusive',
            properties: {}
          },
          complexGateway: {
            java: 'org.uengine.kernel.bpmn.Gateway',
            shapeId: 'OG.shape.bpmn.G_Complex',
            properties: {}
          },
          eventBasedGateway: {
            java: 'org.uengine.kernel.bpmn.Gateway',
            shapeId: 'OG.shape.bpmn.G_Event',
            properties: {}
          },
          /**
           * Task
           */
          task: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_Task',
            properties: {}
          },
          sendTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_SendTask',
            properties: {}
          },
          receiveTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_ReceiveTask',
            properties: {}
          },
          userTask: {
            java: 'org.uengine.kernel.HumanActivity',
            shapeId: 'OG.shape.bpmn.A_HumanTask',
            properties: {}
          },
          manualTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_ManualTask',
            properties: {}
          },
          businessTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_BusinessTask',
            properties: {}
          },
          serviceTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_ServiceTask',
            properties: {}
          },
          scriptTask: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_ScriptTask',
            properties: {}
          },
          callActivity: {
            java: 'org.uengine.kernel.DefaultActivity',
            shapeId: 'OG.shape.bpmn.A_CallActivity',
            properties: {}
          },
          /**
           * Sub Process
           */
          subProcess: {
            java: 'org.uengine.kernel.bpmn.SubProcess',
            shapeId: 'OG.shape.bpmn.A_Subprocess',
            properties: {}
          },
          transaction: {
            java: 'org.uengine.kernel.bpmn.SubProcess',
            shapeId: 'OG.shape.bpmn.A_Transaction',
            properties: {}
          },
          eventSubProcess: {
            java: 'org.uengine.kernel.bpmn.SubProcess',
            shapeId: 'OG.shape.bpmn.A_SubProcess_Event',
            properties: {}
          },
          /**
           * Data
           */
          dataObject: {
            java: 'org.uengine.kernel.bpmn.DataStore',
            shapeId: 'OG.shape.bpmn.D_Data',
            properties: {}
          },
          dataStore: {
            java: 'org.uengine.kernel.bpmn.DataStore',
            shapeId: 'OG.shape.bpmn.D_Store',
            properties: {}
          },
          /**
           * Pool
           */
          pool: {
            shapeId: 'OG.shape.VerticalPoolShape',
            properties: {}
          },
          /**
           * Role
           */
          role: {
            shapeId: 'OG.shape.HorizontalLaneShape',
            properties: {}
          }
        }
      }
    },
    mounted() {
      var me = this;
      me.setMode();
    },
    watch: {
      '$route'(to, from) {
        this.setMode();
      }
    },
    methods: {
      setMode: function () {
        var me = this;
        if (me.monitor) {
          me.getInstance();
        } else {
          me.getDefinition();
        }
      },
      getInstance: function () {
        var me = this;
        this.id = this.$route.params.id;
        this.sliderId = this.id + '-slider';

        var defId;
        me.$root.codi('instances{/id}').get({id: this.id})
          .then(function (response) {
            let split = response.data.defId.split('/');
            defId = split[split.length - 1];
          })
          .then(function () {
            me.$root.codi('definition{/id}').get({id: defId})
              .then(function (response) {
                me.render(response.data);
                me.getStatus();
              })
          })
      },
      getStatus: function () {
        var me = this;
        me.$root.codi('instance{/id}/variables').get({id: me.id})
          .then(function (response) {
            var statusData = response.data;
            for (var key in response.data) {
              if (key.indexOf(':_status:prop') != -1) {
                var elementId = key.replace(':_status:prop', '');
                var status = response.data[key];
                me.updateElementStatus(elementId, status);
              }
            }
          })
      },
      updateElementStatus: function (elementId, status) {
        var me = this;
        let element = me.canvas.getElementById(elementId);
        if (element) {
          element.shape.status = status;
          me.canvas.getRenderer().redrawShape(element);
        }
      },

      save: function () {
        var me = this;
        if (!me.canvas) {
          return;
        }
        var adaptor = new TypedJsonAdaptor(me.canvas, this.shapeIdMappings);
        var exportJson = adaptor.exportJson();

        //processVariables 주입
        var processVariables = this.processVariables;
        if (processVariables && processVariables.length) {
          var copy = JSON.parse(JSON.stringify(processVariables));
          $.each(copy, function (i, variable) {
            if (variable.displayName) {
              variable.displayName = {
                text: variable.displayName
              }
            }
          });
          exportJson.definition.processVariableDescriptors = copy;
        }

        this.$root.codi('definition{/id}').save({id: this.id + '.json'}, exportJson)
          .then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
      },
      findShapeIdMappings: function (shapeId) {
        var map = undefined;
        for (var key in this.shapeIdMappings) {
          if (this.shapeIdMappings[key].shapeId == shapeId) {
            map = this.shapeIdMappings[key];
          }
        }
        return map;
      },
      getDefinition: function () {
        var me = this;
        this.id = this.$route.params.id;
        this.sliderId = this.id + '-slider';

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

              //processVariables displayName 전환.
              var processVariables = response.data.definition.processVariableDescriptors;
              if (processVariables && processVariables.length) {
                var copy = JSON.parse(JSON.stringify(processVariables));
                $.each(copy, function (i, variable) {
                  if (variable.displayName) {
                    variable.displayName = variable.displayName.text;
                  }
                });
                me.processVariables = copy;
              }
            })
        }
      },
      render: function (data) {
        var me = this;
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
          connectCloneable: false,
          connectRequired: true,
          labelEditable: false,
          groupDropable: true,
          collapsible: true,
          enableHotKey: true,
          enableContextMenu: false,
          useSlider: false,
          stickGuide: true,
          checkBridgeEdge: true,
          autoHistory: false
        });

        this.canvas = canvas;

        var adaptor;
        var json;
        if (data) {
          adaptor = new TypedJsonAdaptor(canvas, this.shapeIdMappings);
          adaptor.importJson(data);
        }

        canvas.addSlider({
          slider: $("#" + me.sliderId),
          width: 200,
          height: 300,
          appendTo: ".canvas-panel",
          position: {
            my: "left top",
            at: "left top",
            of: $('#' + me.id)
          }
        });

        //버튼 이벤트 등록
        this.bindEvents();
      },
      bindEvents: function () {

        //this.$el
        var me = this;
        var el = me.$el;
        var canvasEl = $(el).find('.canvas-space');

        //console.log($(el).find('.draggable'));
        $(el).find('.draggable').draggable({
          start: function () {
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
              var mappings = me.findShapeIdMappings(shapeInfo._shape_id);
              shape.data = {
                properties: JSON.parse(JSON.stringify(mappings.properties))
              };

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


        $(me.canvas.getRootElement()).bind('changeMenu', function (event, shapeElement) {
          console.log(shapeElement);
        });
        $(canvasEl).click(function () {
          console.log(123123);
        })
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
      top: 0px;
      left: 0px;
      background: #f7f7f7;
      overflow-x: scroll;
      overflow-y: scroll;
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

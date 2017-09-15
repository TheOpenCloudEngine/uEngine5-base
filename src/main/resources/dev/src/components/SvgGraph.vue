<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">

    <bpmn-vue v-if="definition" class="full-canvas" ref="bpmn-vue"
              :definition.sync="definition"
              v-on:canvasReady="bindEvents">
      <!--<template slot="role" scope="props">-->
        <!--<bpmn-role :role="props.item" :canvas="props.canvas"></bpmn-role>-->
      <!--</template>-->
      <!--<template slot="activity" scope="props">-->
        <!--<component :is="getSVGComponentName(props.item)" :activity="props.item"-->
                   <!--:canvas="props.canvas"></component>-->
      <!--</template>-->
      <!--<template slot="relation" scope="props">-->
        <!--<bpmn-relation :relation="props.item" :canvas="props.canvas"></bpmn-relation>-->
      <!--</template>-->
    </bpmn-vue>

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

  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean
    },
    data () {
      return {
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
      me.setMode();
    },
    watch: {
      '$route'(to, from) {
        this.setMode();
      }
    },
    methods: {
      bindEvents: function (canvas) {
        //this.$el
        var me = this;
        var el = me.$el;
        var canvasEl = $(el).find('.full-canvas'); //me.$refs['bpmn-vue'].$el; //$(el).find('.full-canvas');

        //아이콘 드래그 드랍 이벤트 등록
        $(el).find('.draggable').draggable({
          start: function () {
            canvasEl.data('DRAG_SHAPE', {
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
              var dropX = event.pageX - canvasEl.offset().left + canvasEl[0].scrollLeft;
              var dropY = event.pageY - canvasEl.offset().top + canvasEl[0].scrollTop;
              dropX = dropX / canvas._CONFIG.SCALE;
              dropY = dropY / canvas._CONFIG.SCALE;

              shapeInfo = {
                shapeId: shapeInfo._shape_id,
                x: dropX,
                y: dropY,
                width: parseInt(shapeInfo._width, 10),
                height: parseInt(shapeInfo._height, 10),
                label: ''
              }
              me.$refs['bpmn-vue'].addComponenet(shapeInfo);
            }
            canvasEl.removeData('DRAG_SHAPE');
          }
        });
      },
      undo: function () {
        this.$refs['bpmn-vue'].undo();
      }
      ,
      redo: function () {
        this.$refs['bpmn-vue'].redo();
      }
      ,
      setMode: function () {
        var me = this;
        if (me.monitor) {
          me.getInstance();
        } else {
          me.getDefinition();
        }
      }
      ,
      getSVGComponentName(activity){
        var componentName;
        if (activity) {
          var shapeId = activity.elementView.shapeId;
          $.each(window.Vue.bpmnComponents, function (i, component) {
            if (component.computed.shapeId) {
              if (component.computed.shapeId() == shapeId) {
                componentName = component.name;
              }
            }
          });
          return componentName;
        }
      },
      getInstance: function () {
        var me = this;
        me.id = this.$route.params.id;

        var defId;
        me.$root.codi('instances{/id}').get({id: me.id})
          .then(function (response) {
            let split = response.data.defId.split('/');
            defId = split[split.length - 1];
          })
          .then(function () {
            me.$root.codi('definition{/id}').get({id: defId})
              .then(function (response) {
                me.definition = response.data.definition;
                //me.getStatus();
              })
          })
      }
      ,
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
      }
      ,
      updateElementStatus: function (elementId, status) {
//        var me = this;
//        let element = me.canvas.getElementById(elementId);
//        if (element) {
//          element.shape.status = status;
//          me.canvas.getRenderer().redrawShape(element);
//        }
      }
      ,
      getDefinition: function () {
        var me = this;
        me.id = this.$route.params.id;
        //신규 생성
        if (me.id == 'new-process-definition') {
          me.definition = {
            _type: 'org.uengine.kernel.ProcessDefinition',
            name: {},
            childActivities: [
              'java.util.ArrayList',
              []
            ],
            'roles': [],
            'sequenceFlows': []
          }
        }
        else {
          this.$root.codi('definition{/id}').get({id: me.id + '.json'})
            .then(function (response) {
              me.definition = response.data.definition;

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
      }
      ,
      save: function () {
        var me = this;
        //processVariables 주입
        var processVariables = me.processVariables;
        if (processVariables && processVariables.length) {
          var copy = JSON.parse(JSON.stringify(processVariables));
          $.each(copy, function (i, variable) {
            if (variable.displayName) {
              variable.displayName = {
                text: variable.displayName
              }
            }
          });
          me.definition.processVariableDescriptors = copy;
        }

        //각 액티비티, 롤, 시퀀스 플로우 중 빈 컴포넌트값을 거른다.
        var definitionToSave = JSON.parse(JSON.stringify(me.definition));
        definitionToSave.childActivities[1] = [];
        definitionToSave.roles = [];
        definitionToSave.sequenceFlows = [];
        $.each(me.definition.childActivities[1], function (i, activity) {
          if (activity) {
            definitionToSave.childActivities[1].push(activity);
          }
        })
        $.each(me.definition.roles, function (i, role) {
          if (role) {
            definitionToSave.roles.push(role);
          }
        })
        $.each(me.definition.sequenceFlows, function (i, sequenceFlow) {
          if (sequenceFlow) {
            definitionToSave.sequenceFlows.push(sequenceFlow);
          }
        })

        var data = {definition: definitionToSave};
        this.$root.codi('definition{/id}').save({id: me.id + '.json'}, data)
          .then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
      }
      ,
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

    .full-canvas {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 0px;
      left: 0px;
      overflow: hidden;
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


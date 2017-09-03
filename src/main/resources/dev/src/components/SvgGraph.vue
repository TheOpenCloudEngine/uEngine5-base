<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">

    <bpmn-vue v-if="definition" class="full-canvas"
              :activities="definition.childActivities[1]"
              :roles="definition.roles"
              :relations="definition.sequenceFlows"
    >
      <template slot="role" scope="props">
        <bpmn-role :role="props.item" :canvas="props.canvas"></bpmn-role>
      </template>
      <template slot="activity" scope="props">
        <component :is="getSVGComponentName(props.item)" :activity="props.item"
                   :canvas="props.canvas"></component>
      </template>
      <template slot="relation" scope="props">
        <bpmn-relation :relation="props.item" :canvas="props.canvas"></bpmn-relation>
      </template>
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
        <span class="icons fa fa-folder-open" v-on:click="updateTest"></span>
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
        history: [],
        historyIndex: 0,
        undoing: false,
        undoed: false,
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
    computed: {
      canUndo: function () {
        return this.historyIndex > 0
      },
      canRedo: function () {
        return this.history.length - 1 - this.historyIndex > 0
      }
    },
    mounted() {
      var me = this;
      me.setMode();
    },
    watch: {
      '$route'(to, from) {
        this.setMode();
      },
      definition: {
        handler: function (after, before) {
          console.log('definition update');
          if (!this.undoing) {

            if (this.undoed) { //if undoed just before, clear the history from the current historyIndex
              this.history.splice(this.historyIndex, this.history.length - this.historyIndex);
              this.undoed = false;
            }

            this.history.push(JSON.parse(JSON.stringify(after))); //heavy
            this.historyIndex = this.history.length;
          } else {
            this.undoing = false;
          }
        },
        deep: true
      },
    },
    methods: {
      undo: function () {
        if (this.canUndo) {
          this.historyIndex -= 1
          this.undoing = true;
          this.undoed = true;
          this.definition = this.history[this.historyIndex];
          console.log(this.history.length, this.definition);
          //this.showProperties(this.properties);
        }
      },
      redo: function () {
        if (this.canRedo) {
          this.historyIndex += 1
          this.undoing = true;
          this.undoed = true;
          this.definition = this.history[this.historyIndex]
          console.log(this.history.length, this.definition);
          //this.showProperties(this.properties);
        }
      },
      updateTest: function () {
        //this.definition.childActivities[1][0].elementView.x = 100;
        this.definition.childActivities[1].splice(1, 1);
      },
      setMode: function () {
        var me = this;
        if (me.monitor) {
          me.getInstance();
        } else {
          me.getDefinition();
        }
      },
      getSVGComponentName(activity){
        var componentName;
        if (activity) {
          var className = activity._type;
          $.each(window.bpmnComponents, function (i, component) {
            if (component.computed.className) {
              if (component.computed.className() == className) {
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
//        var me = this;
//        let element = me.canvas.getElementById(elementId);
//        if (element) {
//          element.shape.status = status;
//          me.canvas.getRenderer().redrawShape(element);
//        }
      },
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
      },
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

        this.$root.codi('definition{/id}').save({id: me.id + '.json'}, {definition: me.definition})
          .then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
      },
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


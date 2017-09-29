<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">

    <bpmn-vue v-if="definition" class="full-canvas" ref="bpmn-vue"
              :definition.sync="definition"
              v-on:bpmnReady="bindEvents">
    </bpmn-vue>

    <md-card v-if="!monitor" class="tools">
      <span class="icons bpmn-icon-hand-tool"></span>
      <span class="icons bpmn-icon-lasso-tool"></span>
      <span class="icons bpmn-icon-space-tool"></span>
      <span class="icons bpmn-icon-connection-multi"></span>

      <hr class="separator">

      <span v-for="item in dragItems"
            class="icons draggable"
            :class="item.icon"
            :_component="item.component"
            :_width="item.width"
            :_height="item.height"
      ></span>
    </md-card>

    <md-card v-if="!monitor" class="import">
      <md-layout>
        <md-layout>
          <span class="icons fa fa-folder-open"></span>
        </md-layout>
        <md-layout>
          <span class="icons fa fa-cloud-upload"></span>
        </md-layout>
      </md-layout>
    </md-card>

    <md-card v-if="!monitor" class="export">
      <md-layout>
        <md-layout>
          <span class="icons fa fa-download"></span>
        </md-layout>
        <md-layout>
          <span class="icons fa fa-picture-o"></span>
        </md-layout>
      </md-layout>
    </md-card>

    <md-card v-if="!monitor" class="history">
      <md-layout>
        <md-layout>
          <span class="icons fa fa-undo" v-on:click="undo"></span>
        </md-layout>
        <md-layout>
          <span class="icons fa fa-repeat" v-on:click="redo"></span>
        </md-layout>
      </md-layout>
    </md-card>

    <md-card v-if="!monitor" class="zoom">
      <span class="icons fa fa-arrows-alt"></span>

      <hr class="separator">

      <span class="icons fa fa-plus-square-o"></span>
      <span class="icons fa fa-minus-square-o"></span>
    </md-card>

    <md-layout>
      <md-layout md-flex="50">

      </md-layout>
      <md-layout md-flex="50">

        <!--프로세스 아이디-->
        <md-layout>
          <md-input-container>
            <label>Process Name</label>
            <md-input v-model="id" type="text"></md-input>
          </md-input-container>
        </md-layout>

        <!--프로세스 세이브-->
        <md-layout v-if="!monitor">
          <md-button v-if="!monitor" class="md-fab md-warn md-mini" @click="save">
            <md-icon>save</md-icon>
          </md-button>
        </md-layout>

        <!--프로세스 변수-->
        <md-layout v-if="!monitor">
          <md-button class="md-raised" id="processVariables" @click="openProcessVariables">ProcessVariable
          </md-button>
        </md-layout>

      </md-layout>
    </md-layout>
  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean
    },
    data () {
      return {
        aaa: 'AAA',
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
            'component': 'bpmn-start-event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-intermediate-event-none',
            'component': 'bpmn-intermediate-event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-end-event-none',
            'component': 'bpmn-end-event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-gateway-xor',
            'component': 'bpmn-exclusive-gateway',
            'width': '40',
            'height': '40'
          },
          {
            'icon': 'bpmn-icon-task',
            'component': 'bpmn-task',
            'width': '100',
            'height': '100'
          },
          {
            'icon': 'bpmn-icon-subprocess-expanded',
            'component': 'bpmn-subprocess',
            'width': '200',
            'height': '150'
          },
          {
            'icon': 'bpmn-icon-data-object',
            'component': 'bpmn-data-object',
            'width': '50',
            'height': '50'
          },
          {
            'icon': 'bpmn-icon-data-store',
            'component': 'bpmn-data-store',
            'width': '50',
            'height': '50'
          },
          {
            'icon': 'bpmn-icon-lane',
            'component': 'bpmn-pool',
            'width': '300',
            'height': '300'
          },
          {
            'icon': 'bpmn-icon-participant',
            'component': 'bpmn-role',
            'width': '400',
            'height': '200'
          }
        ]
      }
    },
    computed: {},

    //컴포넌트가 Dom 에 등록되었을 떄(실제 렌더링 되기 위해 활성화 되었을 때.)
    mounted() {
      var me = this;
      me.setMode();
    },

    //watch : prop 나, data 요소의 값이 변경됨을 감지하는 녀석.
    watch: {
      aaa: function (after, before) {
        //after => 바뀐 후 값
        //before => 바뀌기 전.
        console.log('after,before', after, before);
      },
      '$route'(to, from) {
        this.setMode();
      }
    },
    methods: {
      openProcessVariables() {
        this.$refs['bpmn-vue'].openProcessVariables();
      },
      bindEvents: function (opengraph) {
        //this.$el
        var me = this;
        var el = me.$el;
        var canvasEl = $(opengraph.container);
        if (!canvasEl || !canvasEl.length) {
          return;
        }
        //아이콘 드래그 드랍 이벤트 등록
        $(el).find('.draggable').draggable({
          start: function () {
            canvasEl.data('DRAG_SHAPE', {
              'component': $(this).attr('_component'),
              'width': $(this).attr('_width'),
              'height': $(this).attr('_height')
            });
          },
          helper: 'clone',
          appendTo: canvasEl
        });

        canvasEl.droppable({
          drop: function (event, ui) {
            var componentInfo = canvasEl.data('DRAG_SHAPE'), shape, element;
            if (componentInfo) {
              var dropX = event.pageX - canvasEl.offset().left + canvasEl[0].scrollLeft;
              var dropY = event.pageY - canvasEl.offset().top + canvasEl[0].scrollTop;

              dropX = dropX / opengraph.scale;
              dropY = dropY / opengraph.scale;

              componentInfo = {
                component: componentInfo.component,
                x: dropX,
                y: dropY,
                width: parseInt(componentInfo.width, 10),
                height: parseInt(componentInfo.height, 10),
                label: ''
              }
              me.$refs['bpmn-vue'].addComponenet(componentInfo);
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
      //여기서는, 라우터에서 전달해준 monitor prop 를 가지고 디자이너 모드인지, 모니터 모드인지 판별함.
      setMode: function () {
        var me = this;
        if (me.monitor) {
          me.getInstance();
        } else {
          me.getDefinition();
        }
      }
      ,

      getInstance: function () {
        var me = this;
        me.id = this.$route.params.id;

        var defId;

        //이 부분에 대한 것은, ServiceLocator.vue 를 보도록.
        //ServiceLocator.vue 는 App.vue (최상단 컴포넌트) 안에 붙어있습니다.
        me.$root.codi('instances{/id}').get({id: me.id})
          .then(function (response) {
            let split = response.data.defId.split('/');
            defId = split[split.length - 1];
          })
          .then(function () {
            me.$root.codi('definition{/id}').get({id: defId})
              .then(function (response) {



                // definition 이란 것은 디자이너가 도형을 그리는 스펙 정의.
                // status 를 불러와서 definition 을 손본 후, me.definition 에 등록할 것.

                me.definition = response.data.definition;


                //me.getStatus();
              })
          })
      }
      ,
      getStatus: function () {
//        var me = this;
//        me.$root.codi('instance{/id}/variables').get({id: me.id})
//          .then(function (response) {
//            var statusData = response.data;
//            for (var key in response.data) {
//              if (key.indexOf(':_status:prop') != -1) {
//                var elementId = key.replace(':_status:prop', '');
//                var status = response.data[key];
//                me.updateElementStatus(elementId, status);
//              }
//            }
//          })
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
            })
        }
      }
      ,
      save: function () {
        var me = this;
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
      width: 48px;
      left: 20px;
      top: 20px;
      padding: 4px;
      overflow: hidden;
      .icons {
        margin-top: 5px;
        margin-bottom: 5px;
      }
    }
    .zoom {
      position: absolute;
      width: 42px;
      right: 20px;
      bottom: 120px;
      .icons {
        font-size: 25px;
        margin-left: 10px;
        margin-top: 5px;
        margin-bottom: 5px;
      }
    }
    .icons {
      cursor: pointer;
      font-size: 30px;
      &:hover {
        color: #ffc124;
      }
    }

    .import, .export, .save, .history {
      position: absolute;
      padding: 8px;
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
  }
</style>


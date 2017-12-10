<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <md-layout>
      <md-layout md-flex="20">
        <md-list v-if="monitor">
          <bpmn-tree-list
            :model="treeData"
            :id="id">
          </bpmn-tree-list>
        </md-list>
      </md-layout>
      <md-layout md-flex="80">
        <bpmn-vue v-if="definition" class="full-canvas" ref="bpmn-vue"
                  :definition.sync="definition"
                  :monitor="monitor"
                  v-on:bpmnReady="bindEvents">
        </bpmn-vue>
        <md-layout>
          <md-layout md-flex="50">
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
          </md-layout>
          <md-layout md-flex="50">

            <!--프로세스 아이디-->
            <md-layout v-if="!monitor">
              <md-input-container>
                <label>Process Name</label>
                <md-input v-model="definitionName" type="text"></md-input>
              </md-input-container>
            </md-layout>

            <!--인스턴스 이름-->
            <md-layout v-if="monitor">
              <md-input-container>
                <label>Instance Name</label>
                <md-input v-model="definitionName" type="text" readonly></md-input>
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

            <md-layout v-if="monitor">
              <md-button class="md-raised" id="userPicker" @click="openUserPicker">담당자 변경</md-button>
              <user-picker
                :id="id"
                ref="userPicker"
                :roles="definition.roles"
                v-if="definition"
                style="min-width: 70%;"></user-picker>
            </md-layout>

          </md-layout>
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
    data() {
      return {
        aaa: 'AAA',
        id: null,
        path: '',
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
        ],
        trees: [],
        treeData: {}
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

        var access_token = localStorage["access_token"];
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var backend = hybind(serviceLocator.getServiceHost(), {headers: {'access_token': access_token}});

        var instance = {};
        backend.$bind("instance/" + me.id, instance);

        instance.$load().then(function () {

          instance.definition.$load().then(function (definition) {
            me.definitionName = definition.name;

            definition.raw.$load().then(function (raw_definition) {
              me.getStatus(function (result) {

                var definition = raw_definition.definition;

                for (var key in definition.childActivities[1]) {

                  //데이터 꾸미기 status 로 definition 바꾸기.
                  if (definition.childActivities[1][key]["tracingTag"] == result.elementId) {
                    definition.childActivities[1][key]["status"] = result.status;
                  }
                  definition.status = result.status;
                  me.definition = definition;

                }

              });

            });

          });

        });

      }
      ,
      //트리 구조를 위해 mainInstanceId가 있는지 확인한다.
      //재귀호출하여 상위 인스턴스가 없을 때까지 찾는다.
      findParent: function (instanceId) {
        var me = this;
        me.$root.codi('instances{/id}').get({id: instanceId})
          .then(function (response) {
            var mainInstId = response.data.mainInstId;
            var name = me.getLastText(response.data.defId).replace('.json', '');
            var instanceId = me.getLastText(response.data._links.self.href);
            if (mainInstId == null) {
              me.trees.push({
                name: name,
                id: instanceId,
                parentId: instanceId,
                children: null
              });
              me.treeStruecture(instanceId);
              return false;
            }
            me.findParent(mainInstId);
          })
      }
      ,
      //트리 구조를 위해 subprocess가 있는지 확인한다.
      //재귀호출하여 하위 참조 인스턴스가 없을 때까지 찾는다.
      treeStructure: function (instanceId) {
        //instanceId가 null로 들어오는 경우가 있어 체크함
        if (instanceId == null) {
          return;
        }

        var me = this;
        me.$root.codi('instances/search/findChild?instId=' + instanceId).get()
          .then(function (response) {
            $.each(response.data, function (key, instances) {
              if (key == '_embedded') {
                if (instances.instances.length == 0) {
                  var tree = me.listToTree(me.trees);
                  me.treeData = tree[0];
                  return false;
                }
                var name = instances.instances[0]["defId"].replace('codi/', '');
                var childId = instances.instances[0]["_links"]["self"]["href"];
                childId = me.getLastText(childId);
                me.trees.push({
                  name: name,
                  id: childId,
                  parentId: instanceId,
                  children: null
                });
              }
              me.treeStructure(childId);
            });
          })
      }
      ,
      getLastText: function (_val) {
        var length = _val.length;
        var lastSlash = _val.lastIndexOf('/') + 1;
        var lastText = _val.substring(lastSlash, length);

        return lastText;
      }
      ,
      listToTree: function (list) {
        var map = {}, node, roots = [], i;
        for (i = 0; i < list.length; i += 1) {
          map[list[i].id] = i;
          list[i].children = [];
        }
        for (i = 0; i < list.length; i += 1) {
          node = list[i];
          if (i !== 0) {
            list[map[node.parentId]].children.push(node);
          } else {
            roots.push(node);
          }
        }
        return roots;
      }
      ,
      toggle: function () {
        if (this.isFolder) {
          this.open = !this.open
        }
      }
      ,
      getStatus: function (callback) {
        var me = this;
        me.$root.codi('instance{/id}/variables').get({id: me.id})
          .then(function (response) {
            for (var key in response.data) {
              if (key.indexOf(':_status:prop') != -1) {
                var result = [];
                result.elementId = key.replace(':_status:prop', '');
                result.status = response.data[key];

                callback(result);
                //me.updateElementStatus(elementId, status);
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
        me.id = me.$route.params.id;
        if (me.$route.params.path) {
          var pathSplit = me.$route.params.path.split('_');
          for (var i = 0; i < pathSplit.length; i++) {
            me.path += pathSplit[i] + "/";
          }
        }
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
          var url = 'definition/raw/' + me.path + me.id.replace('.xml','.json');
          this.$root.codi(url).get().then(function (response) {
            me.definition = response.data.definition;
            me.definitionName = me.definition.name.text;
          })
        }
      }
      ,
      save: function () {
        var me = this;
        //각 액티비티, 롤, 시퀀스 플로우 중 빈 컴포넌트값을 거른다.
        var definitionToSave = JSON.parse(JSON.stringify(me.definition));

        if (me.id == 'new-process-definition') {
          if (me.definitionName !== null) {
            me.id = me.definitionName;
          }
        }

        definitionToSave.name.text = me.definitionName;

        var nullFilter = function (array) {
          return array.filter(function (x) {
            if (x) {
              return true;
            } else {
              return false;
            }
          });
        }

        //롤 널 체크
        definitionToSave.roles = nullFilter(definitionToSave.roles);

        var recursiveCheck = function (activity) {
          if (!activity) {
            return;
          }
          if (activity.sequenceFlows && activity.sequenceFlows.length) {
            activity.sequenceFlows = nullFilter(activity.sequenceFlows);
          }
          if (activity.childActivities && activity.childActivities[1] && activity.childActivities[1].length) {
            activity.childActivities[1] = nullFilter(activity.childActivities[1]);
            $.each(activity.childActivities[1], function (i, child) {
              //롤 배정
              if (child._type == 'org.uengine.kernel.HumanActivity') {
                child.role.name =
                  me.$refs['bpmn-vue'].getWhereRoleAmIByTracingTag(child.tracingTag);
                console.log('HumanActivity ' + child.name.text + ' saved role as ' + child.role.name);
              }
              recursiveCheck(child);
            })
          }
        }
        //액티비티, 릴레이션 널 체크, 휴먼 액티비티 롤 배정 (bpmn 패널을 더블클릭하면 배정되나, 안열어보고 배치한 것을 위해 설정)
        recursiveCheck(definitionToSave, null);

        //휴먼 액티비티 롤 배정 (bpmn 패널을 더블클릭하면 배정되나, 안열어보고 배치한 것을 위해 설정)
//        this.activity.role.name =
//          this.bpmnVue.getWhereRoleAmIByTracingTag(this.activity.tracingTag);

        var access_token = localStorage["access_token"];
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var backend = hybind(serviceLocator.getServiceHost(), {headers: {'access_token': access_token}});

        var definition = {};
        backend.$bind("definition/raw/" + me.path + me.id.replace('.xml', '.json'), definition);
        definition.definition = definitionToSave;
        definition.$save().then(
          function (response) {
            me.$root.$children[0].success('저장되었습니다.');
          },
          function (response) {
            me.$root.$children[0].error('저장할 수 없습니다.');
          }
        );
      }
      ,
      openUserPicker(ref) {
        this.$refs['userPicker'].openUserPicker();
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

    .full-canvas {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 10%;
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


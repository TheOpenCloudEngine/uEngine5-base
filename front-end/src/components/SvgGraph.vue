<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <md-layout>
      <md-layout md-flex="20">
        <md-list v-if="monitor" class="tree-list">
          <bpmn-tree-list
            :trees="trees">
          </bpmn-tree-list>
        </md-list>
      </md-layout>
      <md-layout @contextmenu.native="openMenu" @mousedown.native="closeMenu">

        <bpmn-vue v-if="definition" class="full-canvas" ref="bpmn-vue"
                  :definition.sync="definition"
                  :monitor="monitor"
                  :backend="backend"
                  v-on:bpmnReady="bindEvents">
        </bpmn-vue>

        <md-card v-if="!monitor" class="tools" style="top:100px;">
          <span v-for="item in dragItems"
                class="icons draggable"
                :class="item.icon"
                :_component="item.component"
                :_width="item.width"
                :_height="item.height">
            <md-tooltip md-direction="right">{{item.label}}</md-tooltip>
          </span>
        </md-card>
        <md-card v-if="!monitor" class="tools" style="top:362px;">
          <span class="icons fa fa-undo" v-on:click="undo" style="margin-left:7px;">
            <md-tooltip md-direction="right">Undo</md-tooltip>
          </span>
          <span class="icons fa fa-repeat" v-on:click="redo" style="margin-left:7px;">
            <md-tooltip md-direction="right">Redo</md-tooltip>
          </span>
        </md-card>

        <!--md-card v-if="!monitor" class="import">
          <md-layout>
            <md-layout>
              <span class="icons fa fa-folder-open"></span>
            </md-layout>
            <md-layout>
              <span class="icons fa fa-cloud-upload"></span>
            </md-layout>
          </md-layout>
        </md-card-->

        <!--md-card v-if="!monitor" class="export">
          <md-layout>
            <md-layout>
              <span class="icons fa fa-download"></span>
            </md-layout>
            <md-layout>
              <span class="icons fa fa-picture-o"></span>
            </md-layout>
          </md-layout>
        </md-card-->

        <!--md-card v-if="!monitor" class="history">
          <md-layout>
            <md-layout>
              <span class="icons fa fa-undo" v-on:click="undo"></span>
            </md-layout>
            <md-layout>
              <span class="icons fa fa-repeat" v-on:click="redo"></span>
            </md-layout>
          </md-layout>
        </md-card-->

        <!--md-card v-if="!monitor" class="zoom">
          <span class="icons fa fa-arrows-alt"></span>
          <hr class="separator">
          <span class="icons fa fa-plus-square-o"></span>
          <span class="icons fa fa-minus-square-o"></span>
        </md-card-->

        <md-layout>

          <!--프로세스 아이디-->
          <md-layout v-if="!monitor">
            <md-input-container>
              <label>Process Name</label>
              <md-input v-model="definitionName" type="text"></md-input>
            </md-input-container>
          </md-layout>


          <!--프로세스 정의-->
          <md-layout v-if="!monitor">
            <md-button c lass="md-primary" id="processVariables" @click="openDefinitionSettings"><md-icon>sort_by_alpha</md-icon> Defintion Settings</md-button>
          </md-layout>

          <!--프로세스 변수-->
          <md-layout v-if="!monitor">
            <md-button c lass="md-primary" id="processVariables" @click="openProcessVariables"><md-icon>sort_by_alpha</md-icon> Process Variable</md-button>
          </md-layout>
          <!--로케일-->
          <md-layout v-if="!monitor && definition">
            <md-input-container>
              <label>Language</label>
              <md-select v-model="definition._selectedLocale" @change="changeLocale">
                <md-option value="ko">Korean</md-option>
                <md-option value="en">English</md-option>
              </md-select>
            </md-input-container>
          </md-layout>

          <!--프로세스 세이브-->
          <md-layout v-if="!monitor">
            <md-button v-if="!monitor" class="md-fab md-warn md-mini" @click="save">
              <md-icon>save</md-icon>
            </md-button>
          </md-layout>


          <!--인스턴스 이름-->
          <md-layout v-if="monitor">
            <md-input-container>
              <label>Instance Name</label>
              <md-input v-model="definitionName" type="text" readonly></md-input>
            </md-input-container>
          </md-layout>

          <md-layout v-if="monitor">
            <!--프로세스 변수-->
            <md-button class="md-raised" id="instanceVariables" @click="openInstanceVariables">Process Variable</md-button>
            <bpmn-instance-variables
              :id="id"
              :definition="definition"
              v-if="definition"
              ref="instanceVariables"></bpmn-instance-variables>
            <!--담당자 변경-->
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
    <!--Back to Here Menu Start -->
    <!-- Todo : contextMenuActivated를 이용하여 작업해야 함
    <ul class='custom-menu' v-if="contextMenuActivated">
    -->
    <ul class='custom-menu'>
      <li data-action="backToHere">Back To Here</li>
    </ul>
    <!--Back to Here Menu End -->
  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean,
      backend: Object
    },
    data() {
      return {
        contextMenuActivated: false,
        id: null,
        rootId: null,
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
            'label': 'Start Event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-intermediate-event-none',
            'component': 'bpmn-intermediate-event',
            'label': 'Intermediate Event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-end-event-none',
            'component': 'bpmn-end-event',
            'label': 'End Event',
            'width': '30',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-gateway-xor',
            'component': 'bpmn-exclusive-gateway',
            'label': 'Gateway',
            'width': '40',
            'height': '40'
          },
          {
            'icon': 'bpmn-icon-task',
            'component': 'bpmn-task',
            'label': 'Task',
            'width': '100',
            'height': '100'
          },
//          {
//            'icon': 'bpmn-icon-subprocess-expanded',
//            'component': 'bpmn-subprocess',
//            'width': '200',
//            'height': '150'
//          },
//          {
//            'icon': 'bpmn-icon-data-object',
//            'component': 'bpmn-data-object',
//            'width': '50',
//            'height': '50'
//          },
//          {
//            'icon': 'bpmn-icon-data-store',
//            'component': 'bpmn-data-store',
//            'width': '50',
//            'height': '50'
//          },
//          {
//            'icon': 'bpmn-icon-lane',
//            'component': 'bpmn-pool',
//            'width': '300',
//            'height': '300'
//          },
          {
            'icon': 'bpmn-icon-participant',
            'component': 'bpmn-role',
            'label': 'Role Lane',
            'width': '400',
            'height': '200'
          }
        ],
        trees: [],
        bthTracingTag: ""
      }
    },
    computed: {},

    //컴포넌트가 Dom 에 등록되었을 떄(실제 렌더링 되기 위해 활성화 되었을 때.)
    mounted() {
      var me = this;
      me.setMode();

      // If the menu element is clicked //TODO - vue js 방식으로 전환, IE - 9
      $(".custom-menu li").click(function(){
        // This is the triggered action name
        switch($(this).attr("data-action")) {
          // A case for each action. Your actions here
          case "backToHere":
            me.onBackToHere();
            break;
        }
        // Hide it AFTER the action was triggered
        $(".custom-menu").hide(0);
      });

    },

    //watch : prop 나, data 요소의 값이 변경됨을 감지하는 녀석.
    watch: {
      '$route'(to, from) {
        this.setMode();
      }
    },
    methods: {
      openProcessVariables() {
        this.$refs['bpmn-vue'].openProcessVariables();
      },
      openDefinitionSettings() {
        this.$refs['bpmn-vue'].openDefinitionSettings();
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
      },
      redo: function () {
        this.$refs['bpmn-vue'].redo();
      },
      //여기서는, 라우터에서 전달해준 monitor prop 를 가지고 디자이너 모드인지, 모니터 모드인지 판별함.
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
        me.id = this.$route.params.id;
        me.rootId = this.$route.params.rootId;
        var instance = {};
        me.backend.$bind("instance/" + me.id, instance);
        instance.$load().then(function () {
          instance.definition.$load().then(function (definition) {
            me.definitionName = definition.name;
            definition.raw.$load().then(function (raw_definition) {
              var definition = raw_definition.definition;
              me.getStatus(function (result) {
                for (var key in definition.childActivities[1]) {
                  //데이터 꾸미기 status 로 definition 바꾸기.
                  if (definition.childActivities[1][key]["tracingTag"] == result.elementId) {
                    definition.childActivities[1][key]["status"] = result.status;
                  }
                  definition.status = result.status;
                  me.definition = definition;
                }
              });
              me.treeStructure();
            });
          });
        });
      },
      //트리 구조를 위해 subprocess가 있는지 확인한다.
      treeStructure: function () {
        var me = this;

        var instance = {};
        me.backend.$bind("instances/search/findChild?instId=" + me.rootId, instance);

        var tree = [];
        instance.$load().then(function (instances) {

          for(var i in instances) {
            if(instances[i] instanceof Object) {
              //hateoas에서는 self 링크에 자신의 id가 담겨있다.
              var selfLink = instances[i].$bind.self;
              var instId = selfLink.substring(selfLink.lastIndexOf("/")+1, selfLink.length);
              tree[i] = {
                "name": instances[i].defName,
                "id": parseInt(instId),
                "rootInstId": instances[i].rootInstId,
                "mainInstId": instances[i].mainInstId,
                "children": null
              };
            }
          }
          me.trees = me.listToTree(tree);
        });
      },
      listToTree: function (list) {
        var map = {}, node, roots = [], i;
        for (i = 0; i < list.length; i += 1) {
          map[list[i].id] = i;
          list[i].children = [];
        }
        for (i = 0; i < list.length; i += 1) {
          node = list[i];
          if (i !== 0) {
            list[map[node.mainInstId]].children.push(node);
          } else {
            roots.push(node);
          }
        }
        return roots;
      },
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
        me.id = me.$route.params.id;
        if (me.$route.params.path) {
          var pathSplit = me.$route.params.path.split('-');
          for (var i = 0; i < pathSplit.length; i++) {
            me.path += pathSplit[i] + "/";
          }
        }
        // 신규 생성
        if (me.id == 'new-process-definition') {
          me.definition = {
            _type: 'org.uengine.kernel.ProcessDefinition',
            name: {},
            childActivities: [
              'java.util.ArrayList',
              []
            ],
//            processVariableDescriptors: [{
//              _type: 'org.uengine.kernel.ProcessVariable',
//              name: 'Var1',
//            }],
//            'roles': [{
//                _type: 'org.uengine.kernel.Role',
//                name: 'initiator'
//            }],
            'sequenceFlows': [],
            _selectedLocale: 'ko',
            _changedByLocaleSelector: false
          }
          me.changeLocale();
        } else {
          var url = 'definition/raw/' + me.path + me.id + '.json';
          this.$root.codi(url).get().then(function (response) {
            var definition = response.data.definition;
            definition._selectedLocale = 'ko';
            definition._changedByLocaleSelector = false;
            me.definition = definition;
            me.definitionName = me.definition.name.text;
            me.changeLocale();
          })
        }
      },
      save: function () {
        var me = this;
        //각 액티비티, 롤, 시퀀스 플로우 중 빈 컴포넌트값을 거른다.
        var definitionToSave = JSON.parse(JSON.stringify(me.definition));
        var fileName = me.id;

        //save 시 확장자는 .json이어야 한다.
        //확장자가 존재하지 않으면 폴더로 인식한다.
        if (fileName == 'new-process-definition') {
          if (me.definitionName !== null) {
            fileName = me.definitionName;
          }
        }
        fileName = fileName + ".json";

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

        // 프로세스 정의 처리
        definitionToSave.shortDescription = this.$refs['bpmn-vue'].defintionSettings.shortDescription;

        var definition = {};
        me.backend.$bind("definition/raw/" + me.path + fileName, definition);
        definition.definition = definitionToSave;
        definition.$save().then(
          function (response) {
            me.$root.$children[0].success('저장되었습니다.');
          },
          function (response) {
            me.$root.$children[0].error('저장할 수 없습니다.');
          }
        );
      },
      openInstanceVariables(ref) {
        this.$refs['instanceVariables'].openInstanceVariables();
      },
      openUserPicker(ref) {
        this.$refs['userPicker'].openUserPicker();
      },
      openMenu(event) {
        var me = this;
        //인스턴스일 경우에만 오른쪽 버튼 막기
        if (me.monitor) {
          me.instanceOpenMenu(event);
        }
      },
      instanceOpenMenu(event) {
        event.preventDefault(); //오른쪽 버튼 막기
        var me = this;
        var childActivities = me.definition.childActivities[1];
        var _type = "";
        var _status = "";
        for (var key in childActivities) {
          if (childActivities[key].tracingTag == event.toElement.parentNode.id) {
            _type = childActivities[key]._type;
            _status = childActivities[key].status;
            me.bthTracingTag = childActivities[key].tracingTag;
          }
        }
        _type = _type.substring(_type.lastIndexOf('.') + 1, _type.length); // 타입만 나오도록 수정

        //Activity일 경우에 오른쪽 버튼 이벤트 시작
        if (_type == 'Task' || _type == 'SendTask' || _type == 'ReceiveTask' || _type == 'UserTask' || _type == 'ManualTask' ||
          _type == 'BusinessTask' || _type == 'ServiceTask' || _type == 'ScriptTask' || _type == 'HumanActivity' || _type == 'DefaultActivity') {
          //Status가 Completed 일 경우에만 Back To Here 버튼 나오게 수정
          if (_status == 'Completed') {
            // Show contextmenu
            $(".custom-menu").finish().show(0).// In the right position (the mouse)
            css({
              top: event.offsetY + "px",
              left: event.offsetX + "px"
            });
          }
        }
      },
      closeMenu() {
        if (!$(event.target).parents(".custom-menu").length > 0) {
          // Hide it
          $(".custom-menu").hide(0);
        }
      },
      onBackToHere() {
        var me = this;
        var url = "instance/" + me.id + "/activity/" + me.bthTracingTag + "/backToHere";
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create().then(function() {
          me.$root.$children[0].success('작업 내역을 선택한 위치로 되돌렸습니다.');
          //메세지가 나오기 전에 바로 화면 refresh를 시도하는 것을 막기 위해
          //타이머를 설정하여 일정 시간이 지나면 화면을 refresh 한다.
          setInterval(function () {
            // router refresh
            me.$router.go(me.$router.currentRoute);
          }, 1500)
        });
      },
      changeLocale() {
        var me = this;
        me.definition._changedByLocaleSelector = true;
        if (me.definition.childActivities) {
          me.definition.childActivities[1].forEach(function(activity) {
            if (activity && activity.name && activity.name.localedTexts) {
              if (activity.name.localedTexts[me.definition._selectedLocale]) {
                activity.name.text = activity.name.localedTexts[me.definition._selectedLocale];
              }
            }
          });
        }
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

  /* The whole thing */
  .custom-menu {
    display: none;
    z-index: 1000;
    position: absolute;
    overflow: hidden;
    border: 1px solid #CCC;
    white-space: nowrap;
    font-family: sans-serif;
    background: #FFF;
    color: #333;
    border-radius: 5px;
    padding: 0;
  }

  /* Each of the items in the list */
  .custom-menu li {
    padding: 8px 12px;
    cursor: pointer;
    list-style-type: none;
    transition: all .3s ease;
    user-select: none;
  }

  .custom-menu li:hover {
    background-color: #DEF;
  }
</style>

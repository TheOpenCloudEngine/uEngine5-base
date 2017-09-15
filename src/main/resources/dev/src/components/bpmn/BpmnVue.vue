<template>
  <opengraph
    focus-canvas-on-select
    :enableContextmenu="false"
    :enableRootContextmenu="false"
    v-if="filteredDefinition">
    <div v-for="role in filteredDefinition.roles">
      <bpmn-role :role="role"></bpmn-role>
    </div>
    <div v-for="activity in filteredDefinition.childActivities[1]">
      <component :is="activity.elementView.component" :activity="activity"></component>
    </div>
    <div v-for="relation in filteredDefinition.sequenceFlows">
      <bpmn-relation :relation="relation"></bpmn-relation>
    </div>
  </opengraph>
</template>

<script>
  export default {
    name: 'bpmn-vue',
    props: {
      definition: Object
    },

    data: function () {
      let id = this.uuid();
      let sliderId = id + '-slider';
      var filteredDefinition = this.definition;
      if (!filteredDefinition.sequenceFlows) {
        filteredDefinition.sequenceFlows = [];
      }
      if (!filteredDefinition.roles) {
        filteredDefinition.roles = [];
      }
      if (!filteredDefinition.childActivities) {
        filteredDefinition.childActivities = [
          'java.util.ArrayList',
          []
        ]
      }
      if (!filteredDefinition.childActivities[1]) {
        filteredDefinition.childActivities[1] = [];
      }
      return {
        drawer: true,
        text: 'sdfsdf',
        filteredDefinition: filteredDefinition,
        history: [JSON.parse(JSON.stringify(this.definition))],
        historyIndex: 0,
        undoing: false,
        undoed: false,
        id: id,
        sliderId: sliderId,
        canvas: null
      };
    },

    watch: {
      filteredDefinition: {
        handler: function (after, before) {
          console.log('definition update');

          //액티비티이며, 트레이싱 태그가 변경될 경우 (아이디와 트레이싱 태그값이 틀림)
          //1.Here: 릴레이션 source, target 변경. from,to 를 source,target 아이디로 변경
          //2.BpmnComponent: 릴레이션 아이디가 틀리게 옴.
          //3.BpmnComponent: 선연결이 사라짐.
          //4.BpmnComponent: 새로 선연결을 함.
//          var me = this;
//          let activities = after.childActivities[1];
//          let roles = after.roles;
//          let sequenceFlows = after.sequenceFlows;
//          if (activities && activities.length) {
//            $.each(activities, function (i, activitiy) {
//
//              //트레이싱 태그가 변동되었을 경우
//              if (activitiy && activitiy.tracingTag != activitiy.elementView.id) {
//                var oldId = activitiy.elementView.id;
//                activitiy.elementView.id = activitiy.tracingTag;
//
//                if (sequenceFlows && sequenceFlows.length) {
//                  $.each(sequenceFlows, function (i, relation) {
//                    if (relation && relation.sourceRef == oldId) {
//                      relation.sourceRef = activitiy.tracingTag;
//                      relation.relationView.from = me.replaceTerminalId(relation.relationView.from, activitiy.tracingTag);
//                    }
//                    if (relation && relation.targetRef == oldId) {
//                      relation.targetRef = activitiy.tracingTag;
//                      relation.relationView.to = me.replaceTerminalId(relation.relationView.to, activitiy.tracingTag);
//                    }
//                  });
//                }
//              }
//              //Name 이 변동되었을 경우
//              if (activitiy && activitiy.name && activitiy.name.text != activitiy.elementView.label) {
//                activitiy.elementView.label = activitiy.name.text;
//              }
//            })
//          }
//
//          //롤의 이름이 변경되었을 때
//          //1.Here : 휴먼 액티비티 중 oldname 을 가지고 있는 role 을 같이 변경한다.
//          if (roles && roles.length) {
//            $.each(roles, function (i, role) {
//              if (role && role.name != role.elementView.label) {
//                var oldName = role.elementView.label;
//                role.elementView.label = role.name;
//
//                if (activities && activities.length) {
//                  $.each(activities, function (i, activitiy) {
//                    if (activitiy && activitiy.role && activitiy.role.name == oldName) {
//                      activitiy.role = JSON.parse(JSON.stringify(role));
//                    }
//                  })
//                }
//              }
//            })
//          }
//
//          if (!this.undoing) {
//
//            if (this.undoed) { //if undoed just before, clear the history from the current historyIndex
//              this.history.splice(this.historyIndex, this.history.length - this.historyIndex);
//              this.undoed = false;
//            }
//
//            this.history.push(JSON.parse(JSON.stringify(after))); //heavy
//            this.historyIndex = this.history.length;
//          } else {
//            this.undoing = false;
//          }
//          this.$emit('update:definition', this.filteredDefinition)
        },
        deep: true
      },
    },

    computed: {
      canUndo: function () {
        return this.historyIndex > 0
      },
      canRedo: function () {
        return this.history.length - 1 - this.historyIndex > 0
      }
    },

    mounted: function () {
//      this.render();
//      this.bindEvents();
    },

    methods: {
      /**
       * @param {Object} shapeInfo (shapeId,x,y,width,height,label)
       **/
      addComponenet: function (shapeInfo) {
        var me = this;
        var component = me.getSVGComponentByShapeId(shapeInfo.shapeId);
        var className = component.computed.className();
        var additionalData = {};
        //롤 추가인 경우
        if (shapeInfo.shapeId == 'OG.shape.HorizontalLaneShape') {
          additionalData = {
            'name': '',
            'displayName': {},
            'elementView': {
              '_type': 'org.uengine.kernel.view.DefaultActivityView',
              'id': this.uuid(), //오픈그래프 자동 생성
              'shapeId': shapeInfo.shapeId,
              'x': shapeInfo.x,
              'y': shapeInfo.y,
              'width': shapeInfo.width,
              'height': shapeInfo.height,
              'label': shapeInfo.label,
              'style': JSON.stringify({})
            }
          }
          me.filteredDefinition.roles.push(JSON.parse(JSON.stringify(additionalData)));
        }
        //액티비티 추가인 경우
        else {
          var newTracingTag = me.createNewTracingTag();
          console.log('newTracingTag', newTracingTag);
          additionalData = {
            '_type': className,
            'name': {
              'text': ''
            },
            'tracingTag': newTracingTag,
            'elementView': {
              '_type': 'org.uengine.kernel.view.DefaultActivityView',
              'id': newTracingTag,
              'shapeId': shapeInfo.shapeId,
              'x': shapeInfo.x,
              'y': shapeInfo.y,
              'width': shapeInfo.width,
              'height': shapeInfo.height,
              'label': shapeInfo.label,
              'style': JSON.stringify({})
            }
          }
          me.filteredDefinition.childActivities[1].push(JSON.parse(JSON.stringify(additionalData)));
        }
      },
      getSVGComponentByShapeId(shapeId){
        var componentByShapeId;
        if (shapeId) {
          $.each(window.Vue.bpmnComponents, function (i, component) {
            if (component.computed.shapeId) {
              if (component.computed.shapeId() == shapeId) {
                componentByShapeId = component;
              }
            }
          });
          return componentByShapeId;
        }
      },
      undo: function () {
        if (this.canUndo) {
          this.historyIndex -= 1
          this.undoing = true;
          this.undoed = true;
          this.filteredDefinition = this.history[this.historyIndex];
          console.log(this.history.length, this.historyIndex, this.filteredDefinition);
        }
      },
      redo: function () {
        if (this.canRedo) {
          this.historyIndex += 1
          this.undoing = true;
          this.undoed = true;
          this.filteredDefinition = this.history[this.historyIndex]
          console.log(this.history.length, this.historyIndex, this.filteredDefinition);
        }
      }
      ,
      createNewTracingTag: function () {
        var me = this, maxTracingTag = 0,
          isInt = function (value) {
            return !isNaN(value) &&
              parseInt(Number(value)) == value && !isNaN(parseInt(value, 10));
          }
        //히스토리에 있는 데이터도 참조하여, 충돌되는 트레이싱 태그가 없도록 한다. (가장 큰 트레이싱 태그 +1)
        if (me.history && me.history.length) {
          $.each(me.history, function (i, definition) {
            $.each(definition.childActivities[1], function (c, activity) {
              if (activity && isInt(activity.tracingTag) && activity.tracingTag > maxTracingTag) {
                maxTracingTag = activity.tracingTag;
              }
            })
          })
        }
        return maxTracingTag + 1 + '';
      },
      checkExistTracingTag: function (tracingTag) {
        var me = this, isExist = false;
        if (me.filteredDefinition) {
          $.each(me.filteredDefinition.childActivities[1], function (c, activity) {
            if (activity && activity.tracingTag == tracingTag) {
              isExist = true;
            }
          })
        }
        return isExist;
      },
      findComponentById: function (id) {
        var me = this;
        var selected = null;
        $.each(me.$children, function (i, component) {
          if (component.id == id) {
            selected = component;
          }
        });
        return selected;
      },
      replaceTerminalId: function (terminal, id) {
        let split = terminal.split('_TERMINAL_');
        return id + '_TERMINAL_' + split[1];
      },
      bindEvents: function () {
        var me = this;
        var removed;
        //내부적으로 삭제된 경우
        me.canvas.onRemoveShape(function (event, element) {
          console.log('removeShape by user action', element.id);
          me.removeComponentByElement(element.id);
        });

        //캔버스의 유저 액션으로 인한 변동사항 발생.
        me.canvas.onAddHistory(function () {
          console.log('onAddHistory!!');
          $.each(me.$children, function (i, children) {
            children.updateVue();
          })
        });

        me.canvas.onConnectShape(function (event, edgeElement, fromElement, toElement) {
          console.log('onConnectShape');
          var from = $(edgeElement).attr('_from');
          var to = $(edgeElement).attr('_to');
          var value = edgeElement.shape.geom.vertices.toString();
          var id = fromElement.id + '-' + toElement.id;

          //기존의 id 를 쓰고있는 relation 컴포넌트를 찾아서, null 처리할 수 있도록 한다.
          me.removeComponentByElement(id);

          var relation = {
            sourceRef: fromElement.id,
            targetRef: toElement.id,
            relationView: {
              from: from,
              to: to,
              value: value
            }
          }
          me.filteredDefinition.sequenceFlows.push(relation);
          //Next Flow: onAddHistory > updateVue > filteredDefinition update

          //Remove Native Edge (Random Id Shape)
          setTimeout(function () {
            me.canvas.removeShape(edgeElement, true);
          }, 10)
        })


        me.canvas.onDuplicated(function (event, edgeElement, sourceElement, targetElement) {
          var boundary = targetElement.shape.geom.getBoundary();
          var component = me.getSVGComponentByShapeId(targetElement.shape.SHAPE_ID);
          var className = component.computed.className();
          var newTracingTag = me.createNewTracingTag();
          console.log('newTracingTag', newTracingTag);
          var additionalActivity = {
            '_type': className,
            'name': {
              'text': ''
            },
            'tracingTag': newTracingTag,
            'elementView': {
              '_type': 'org.uengine.kernel.view.DefaultActivityView',
              'id': newTracingTag,
              'shapeId': targetElement.shape.SHAPE_ID,
              'x': boundary.getCentroid().x,
              'y': boundary.getCentroid().y,
              'width': boundary.getWidth(),
              'height': boundary.getHeight(),
              'label': '',
              'style': JSON.stringify({})
            }
          }

          var from = $(edgeElement).attr('_from');
          var to = me.replaceTerminalId($(edgeElement).attr('_to'), newTracingTag);
          var value = edgeElement.shape.geom.vertices.toString();
          var id = sourceElement.id + '-' + newTracingTag;

          var additionalRelation = {
            sourceRef: sourceElement.id,
            targetRef: newTracingTag,
            relationView: {
              from: from,
              to: to,
              value: value
            }
          }
          //Next Flow: onAddHistory > updateVue > filteredDefinition update

          //Remove Native Edge And Shape (Random Id Shape)
          setTimeout(function () {
            //edgeElement will remove together
            me.canvas.removeShape(targetElement, true);
          }, 10)

          me.filteredDefinition.childActivities[1].push(JSON.parse(JSON.stringify(additionalActivity)));

          //TODO why first sequenceFlow is Mounted if not use timeout?
          setTimeout(function () {
            me.filteredDefinition.sequenceFlows.push(JSON.parse(JSON.stringify(additionalRelation)));
          }, 10);
        });

        //Lane 이 분기되었을 경우.
        //Lane 분기는 생성되었을 시 아이디를 그대로 씀으로 삭제처리하지 않는다.
        me.canvas.onDivideLane(function (event, dividedLane) {
          var boundary = dividedLane.shape.geom.getBoundary();
          var component = me.getSVGComponentByShapeId(dividedLane.shape.SHAPE_ID);
          var additionalRole = {
            'name': '',
            'displayName': {},
            'elementView': {
              '_type': 'org.uengine.kernel.view.DefaultActivityView',
              'id': dividedLane.id,
              'parent': me.canvas.getParent(dividedLane).id,
              'shapeId': dividedLane.shape.SHAPE_ID,
              'x': boundary.getCentroid().x,
              'y': boundary.getCentroid().y,
              'width': boundary.getWidth(),
              'height': boundary.getHeight(),
              'label': '',
              'style': JSON.stringify({})
            }
          }
          me.filteredDefinition.roles.push(JSON.parse(JSON.stringify(additionalRole)));
        });
      },
//      render: function () {
//        var me = this;
//        //canvas = new OG.Canvas('canvas', [1000, 800], 'transparent');
//        var canvas = new OG.Canvas(this.id, [2000, 2000], '#f7f7f7', 'url(/static/image/grid.gif)');
//        canvas._CONFIG.DEFAULT_STYLE.EDGE["edge-type"] = "plain";
//        canvas._CONFIG.GUIDE_CONTROL_LINE_NUM = 1;
//        canvas._CONFIG.FOCUS_CANVAS_ONSELECT = true;
//        canvas._CONFIG.WHEEL_SCALABLE = true;
//        canvas._CONFIG.DRAG_PAGE_MOVABLE = true;
//        canvas._CONFIG.AUTOMATIC_GUIDANCE = true;
//        canvas._CONFIG.IMAGE_BASE = '/static/image/symbol/';
//        canvas._CONFIG.POOL_DROP_EVENT = true;
//        canvas._CONFIG.AUTO_EXTENSIONAL = false;
//
//        canvas.initConfig({
//          selectable: true,
//          dragSelectable: true,
//          movable: true,
//          resizable: true,
//          connectable: true,
//          selfConnectable: true,
//          connectCloneable: false,
//          connectRequired: true,
//          labelEditable: false,
//          groupDropable: true,
//          collapsible: true,
//          enableHotKey: true,
//          enableContextMenu: false,
//          useSlider: false,
//          stickGuide: true,
//          checkBridgeEdge: true,
//          autoHistory: false
//        });
//
//        this.canvas = canvas;
//        this.$emit('canvasReady', canvas);
//      },
      removeComponentByElement: function (id) {
        var me = this;
        //릴레이션 삭제
        $.each(me.filteredDefinition.sequenceFlows, function (i, relation) {
          if (relation && relation.sourceRef + '-' + relation.targetRef + '' == id) {
            me.filteredDefinition.sequenceFlows[i] = null;
          }
        });
        //롤 삭제
        $.each(me.filteredDefinition.roles, function (i, role) {
          if (role && role.elementView && role.elementView.id == id) {
            me.filteredDefinition.roles[i] = null;
          }
        });
        //액티비티 삭제
        $.each(me.filteredDefinition.childActivities[1], function (i, activity) {
          if (activity && activity.elementView && activity.elementView.id == id) {
            me.filteredDefinition.childActivities[1][i] = null;
          }
        });
      },
      /**
       * 무작위 랜덤 아이디 생성
       * @returns {string} 랜덤 아이디
       */
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">
  .canvas-wrapper {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0px;
    left: 0px;
    overflow: scroll;
  }

  .canvas-container {
    position: relative;
    width: 2000px;
    height: 2000px;
    background: #f7f7f7;
  }
</style>


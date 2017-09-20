<template>
  <opengraph
    focus-canvas-on-select
    :enableContextmenu="false"
    :enableRootContextmenu="false"
    v-if="filteredDefinition"
    ref="opengraph"
    v-on:canvasReady="canvasReady"
    v-on:userAction="onUserAction"
    v-on:connectShape="onConnectShape"
    v-on:removeShape="onRemoveShape"
    v-on:divideLane="onDivideLane"
  >
    <div v-for="role in filteredDefinition.roles">
      <bpmn-role v-if="role != null" :role="role"></bpmn-role>
    </div>
    <div v-for="activity in filteredDefinition.childActivities[1]">
      <component v-if="activity != null" :is="activity.elementView.component" :activity="activity"></component>
    </div>
    <div v-for="relation in filteredDefinition.sequenceFlows">
      <bpmn-relation v-if="relation != null" :relation="relation"></bpmn-relation>
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

      //값 밸리데이션 해서 누락값 넣기. ex) style 값이 없으면 style 들 넣어주기.
      //시퀀스 플로우 검증.
      var filteredDefinition = JSON.parse(JSON.stringify(this.definition));
      if (!filteredDefinition.sequenceFlows) {
        filteredDefinition.sequenceFlows = [];
      }
      $.each(filteredDefinition.sequenceFlows, function (i, relation) {
        if (!relation.relationView.style) {
          relation.relationView.style = JSON.stringify({});
        }
      })

      //롤 검증.
      if (!filteredDefinition.roles) {
        filteredDefinition.roles = [];
      }
      $.each(filteredDefinition.roles, function (i, role) {
        if (!role.elementView.style) {
          role.elementView.style = JSON.stringify({});
        }
      })


      //액티비티 검증.
      if (!filteredDefinition.childActivities) {
        filteredDefinition.childActivities = [
          'java.util.ArrayList',
          []
        ]
      }
      if (!filteredDefinition.childActivities[1]) {
        filteredDefinition.childActivities[1] = [];
      }
      $.each(filteredDefinition.childActivities[1], function (i, activity) {
        if (!activity.elementView.style) {
          activity.elementView.style = JSON.stringify({});
        }
      })

      //트리거 등록 (강제로 watch 핸들러를 만들기 위해)
      filteredDefinition.trigger = {};
      console.log('filteredDefinition', filteredDefinition);

      return {
        enableHistoryAdd: false,
        drawer: true,
        text: 'sdfsdf',
        filteredDefinition: filteredDefinition,
        history: [JSON.parse(JSON.stringify(filteredDefinition))],
        historyIndex: 0,
        undoing: false,
        undoed: false,
        id: id,
        sliderId: sliderId,
        canvas: null,
        propertyEditing: false
      };
    },

    watch: {
      filteredDefinition: {
        handler: function (after, before) {
          this.addHistory(after);
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
      },
      bpmnRole: function () {
        return 'bpmn-vue';
      }
    },

    mounted: function () {

    },

    methods: {
      addHistory: function (newDefinition) {
        console.log(this);
        var definition = newDefinition ? newDefinition : this.filteredDefinition;
        //프로퍼티 패널이 열려있는 상황에서의 데피니션 변화는 모두 히스토리에 저장한다.
        if (this.propertyEditing) {
          this.enableHistoryAdd = false;
          console.log('definition updated while property panel open.');
        }
        //그 외의 경우는 정해진 상황을 강제하여 히스토리에 저장한다.
        else {
          if (this.enableHistoryAdd) {
            this.enableHistoryAdd = false;
          } else {
            console.log('definition updated, but not allow add history.');
            return;
          }
        }
        //this.$emit('update:definition', this.filteredDefinition);

        if (!this.undoing) {
          console.log('definition updated, we will add history.', definition);
          if (this.undoed) { //if undoed just before, clear the history from the current historyIndex
            this.history.splice(this.historyIndex, this.history.length - this.historyIndex);
            this.undoed = false;
          }
          this.history.push(JSON.parse(JSON.stringify(definition))); //heavy
          this.historyIndex = this.history.length - 1;
        } else {
          console.log('definition updated, but triggered by undo,redo action. will skip add history.');
          this.undoing = false;
        }
      },
      /**
       * 도형이 삭제되었을 경우.
       **/
      onRemoveShape: function (component) {
        console.log('remove component by user action', component.id);
        this.removeComponentById(component.id);
      },
      /**
       * Lane 이 분기되었을 경우.
       **/
      onDivideLane: function (dividedLane) {
        var me = this;
        var boundary = dividedLane.shape.geom.getBoundary();
        var additionalRole = {
          'name': '',
          'displayName': {},
          'elementView': {
            '_type': 'org.uengine.kernel.view.DefaultActivityView',
            'id': dividedLane.id,
            'component': 'bpmn-role',
            'parent': me.canvas.getParent(dividedLane).id,
            'x': boundary.getCentroid().x,
            'y': boundary.getCentroid().y,
            'width': boundary.getWidth(),
            'height': boundary.getHeight(),
            'style': JSON.stringify({})
          }
        }
        me.filteredDefinition.roles.push(additionalRole);
      },
      /**
       * 도형이 연결되었을 경우.
       **/
      onConnectShape: function (edge, from, to) {
        var me = this;
        //존재하는 릴레이션인 경우 (뷰 컴포넌트), 데이터 매핑에 의해 자동으로 from, to 가 변경되어있기 때문에 따로 로직은 필요없음.
        //신규 릴레이션인 경우에는 릴레이션 생성
        if (edge.shape && from && to) {
          var vertices = '[' + edge.shape.geom.vertices.toString() + ']';
          var additionalRelation = {
            sourceRef: from.id,
            targetRef: to.id,
            relationView: {
              style: JSON.stringify({}),
              value: vertices
            }
          }
          me.canvas.removeShape(edge.id, true);
          me.filteredDefinition.sequenceFlows.push(additionalRelation);
        }
      },
      /**
       * 그래프 상에서 사용자 액션에 의한 변경사항 발생시
       **/
      onUserAction: function () {
        console.log('** onUserAction fired.');
        this.enableHistoryAdd = true;
        //TODO 데피니션 업데이트 watch 를 강제 활성화시키는 더 좋은 방법 찾아보기.
        this.filteredDefinition.trigger = JSON.parse(JSON.stringify(this.filteredDefinition.trigger));
      },
      /**
       * 캔버스 준비시
       **/
      canvasReady: function (opengraph) {
        this.canvas = opengraph.canvas;
        this.$emit('canvasReady', opengraph);
      },
      /**
       * 드랍이벤트 발생시
       * @param {Object} shapeInfo (shapeId,x,y,width,height,label)
       **/
      addComponenet: function (componentInfo, newTracingTag) {
        this.enableHistoryAdd = true;
        var me = this;
        var additionalData = {};
        //릴레이션 추가인 경우
        if (componentInfo.component == 'bpmn-relation') {
          additionalData = {
            sourceRef: componentInfo.from,
            targetRef: componentInfo.to,
            relationView: {
              style: JSON.stringify({}),
              value: componentInfo.vertices
            }
          }
          me.filteredDefinition.sequenceFlows.push(additionalData);
        }
        //롤 추가인 경우
        else if (componentInfo.component == 'bpmn-role') {
          additionalData = {
            'name': '',
            'displayName': {},
            'elementView': {
              '_type': 'org.uengine.kernel.view.DefaultActivityView',
              'id': null,//this.uuid(), //오픈그래프 자동 생성
              'component': 'bpmn-role',
              'x': componentInfo.x,
              'y': componentInfo.y,
              'width': componentInfo.width,
              'height': componentInfo.height,
              'style': JSON.stringify({})
            }
          }
          me.filteredDefinition.roles.push(additionalData);
        }
        //액티비티 추가인 경우
        else {
          var bpmnComponent = me.getComponentByName(componentInfo.component);
          var className = bpmnComponent.computed.className();
          if (!newTracingTag) {
            newTracingTag = me.createNewTracingTag();
          }
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
              'component': componentInfo.component,
              'x': componentInfo.x,
              'y': componentInfo.y,
              'width': componentInfo.width,
              'height': componentInfo.height,
              'style': JSON.stringify({})
            }
          }
          me.filteredDefinition.childActivities[1].push(additionalData);
        }
      },
      getComponentByName: function (name) {
        var componentByName;
        $.each(window.Vue.bpmnComponents, function (i, component) {
          if (component.name == name) {
            componentByName = component;
          }
        });
        return componentByName;
      },
      undo: function () {
        if (this.canUndo) {
          this.historyIndex -= 1
          this.undoing = true;
          this.undoed = true;
          this.filteredDefinition = this.history[this.historyIndex];
          console.log('length: ' + this.history.length, ' historyIndex : ', this.historyIndex, this.filteredDefinition);
        }
      },
      redo: function () {
        if (this.canRedo) {
          this.historyIndex += 1
          this.undoing = true;
          this.undoed = true;
          this.filteredDefinition = this.history[this.historyIndex]
          console.log('length: ' + this.history.length, ' historyIndex : ', this.historyIndex, this.filteredDefinition);
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
                maxTracingTag = parseInt(activity.tracingTag);
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
      bindEvents: function () {
        var me = this;
        var removed;

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

      removeComponentById: function (id) {
        var me = this;
        //릴레이션 삭제
        $.each(me.filteredDefinition.sequenceFlows, function (i, relation) {
          if (relation && relation.sourceRef + '-' + relation.targetRef + '' == id) {
            console.log('** remove sequenceFlow', id);
            me.filteredDefinition.sequenceFlows[i] = undefined;
          }
        });
        //롤 삭제
        $.each(me.filteredDefinition.roles, function (i, role) {
          if (role && role.elementView && role.elementView.id == id) {
            console.log('** remove role', id);
            me.filteredDefinition.roles[i] = undefined;
          }
        });
        //액티비티 삭제
        $.each(me.filteredDefinition.childActivities[1], function (i, activity) {
          if (activity && activity.elementView && activity.elementView.id == id) {
            console.log('** remove activitiy', id);
            me.filteredDefinition.childActivities[1][i] = undefined;
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


<template>
  <div>
    <opengraph
      focus-canvas-on-select
      :enableContextmenu="false"
      :enableRootContextmenu="false"
      :slider="true"
      v-if="data.definition"
      ref="opengraph"
      v-on:canvasReady="bpmnReady"
      v-on:userAction="onUserAction"
      v-on:connectShape="onConnectShape"
      v-on:removeShape="onRemoveShape"
      v-on:divideLane="onDivideLane"
    >
      <div v-for="role in data.definition.roles">
        <bpmn-role v-if="role != null" :role="role"></bpmn-role>
      </div>
      <div v-for="activity in data.definition.childActivities[1]">
        <component v-if="activity != null" :is="activity.elementView.component" :activity="activity"></component>
      </div>
      <div v-for="relation in data.definition.sequenceFlows">
        <bpmn-relation v-if="relation != null" :relation="relation"></bpmn-relation>
      </div>
    </opengraph>
    <bpmn-component-changer
      :bpmnComponent="componentToChange"
    >
    </bpmn-component-changer>
  </div>
</template>

<script>
  export default {
    name: 'bpmn-vue',
    props: {
      definition: Object
    },
    mounted: function () {
      this.id = this.uuid();
      this.data.definition = this.validateDefinition(this.definition);
      this.history = [JSON.parse(JSON.stringify(this.data.definition))];
      this.$nextTick(function () {
        //$nextTick delays the callback function until Vue has updated the DOM
        // (which usually happens as a result of us changing the data
        //  so make any DOM changes here
        this.canvas._CONFIG.FAST_LOADING = false;
        this.canvas.updateSlider();
      })
    }
    ,
    data: function () {
//      let id = this.uuid();
//      let sliderId = id + '-slider';
//
//      var definition = this.validateDefinition(this.definition);
      return {
        enableHistoryAdd: false,
        drawer: true,
        text: 'sdfsdf',
        data: {
          definition: null,
          trigger: {}
        },
        history: [],
        historyIndex: 0,
        undoing: false,
        undoed: false,
        id: null,
        canvas: null,
        propertyEditing: false,
        componentToChange: null
      };
    },

    watch: {
      data: {
        handler: function (after, before) {
          this.$emit('update:definition', after.definition);

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

          if (!this.undoing) {
            console.log('definition updated, we will add history.', after.definition);
            if (this.undoed) { //if undoed just before, clear the history from the current historyIndex
              this.history.splice(this.historyIndex, this.history.length - this.historyIndex);
              this.undoed = false;
            }
            this.history.push(JSON.parse(JSON.stringify(after.definition))); //heavy
            this.historyIndex = this.history.length - 1;
          } else {
            console.log('definition updated, but triggered by undo,redo action. will skip add history.');
            this.undoing = false;
          }
        }
        ,
        deep: true
      }
    },

    computed: {
      canUndo: function () {
        return this.historyIndex > 0
      }
      ,
      canRedo: function () {
        return this.history.length - 1 - this.historyIndex > 0
      }
      ,
      bpmnRole: function () {
        return 'bpmn-vue';
      }
    }
    ,
    methods: {
      validateDefinition: function (definition) {
        //값 밸리데이션 해서 누락값 넣기. ex) style 값이 없으면 style 들 넣어주기.

        //시퀀스 플로우 검증.
        if (!definition.sequenceFlows) {
          definition.sequenceFlows = [];
        }
        $.each(definition.sequenceFlows, function (i, relation) {
          if (!relation.relationView.style) {
            relation.relationView.style = JSON.stringify({});
          }
        })

        //롤 검증.
        if (!definition.roles) {
          definition.roles = [];
        }
        $.each(definition.roles, function (i, role) {
          if (!role.elementView.style) {
            role.elementView.style = JSON.stringify({});
          }
        })

        //액티비티 검증.
        if (!definition.childActivities) {
          definition.childActivities = [
            'java.util.ArrayList',
            []
          ]
        }
        if (!definition.childActivities[1]) {
          definition.childActivities[1] = [];
        }
        $.each(definition.childActivities[1], function (i, activity) {
          if (!activity.elementView.style) {
            activity.elementView.style = JSON.stringify({});
          }
        })
        return definition;
      },
      /**
       * 도형이 삭제되었을 경우.
       **/
      onRemoveShape: function (component) {
        console.log('remove component by user action', component.id);
        this.removeComponentById(component.id);
      }
      ,
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
        me.data.definition.roles.push(additionalRole);
      }
      ,
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
          me.data.definition.sequenceFlows.push(additionalRelation);
        }
      }
      ,
      /**
       * 그래프 상에서 사용자 액션에 의한 변경사항 발생시
       **/
      onUserAction: function () {
        console.log('** onUserAction fired.');
        this.enableHistoryAdd = true;
        //TODO 데피니션 업데이트 watch 를 강제 활성화시키는 더 좋은 방법 찾아보기.
        this.data.trigger = JSON.parse(JSON.stringify(this.data.trigger));
      }
      ,
      /**
       * 캔버스 준비시
       **/
      bpmnReady: function (opengraph) {
        this.canvas = opengraph.canvas;
        this.canvas._CONFIG.FAST_LOADING = true;
        console.log('this.canvas._CONFIG.FAST_LOADING');
        this.$emit('bpmnReady', opengraph);
      }
      ,
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
          me.data.definition.sequenceFlows.push(additionalData);
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
          me.data.definition.roles.push(additionalData);
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
          me.data.definition.childActivities[1].push(additionalData);
        }
      }
      ,
      /**
       * 컴포넌트 이름으로 Bpmn 컴포넌트를 가져온다.
       **/
      getComponentByName: function (name) {
        var componentByName;
        $.each(window.Vue.bpmnComponents, function (i, component) {
          if (component.name == name) {
            componentByName = component;
          }
        });
        return componentByName;
      }
      ,
      undo: function () {
        if (this.canUndo) {
          this.canvas._CONFIG.FAST_LOADING = true;
          this.historyIndex -= 1
          this.undoing = true;
          this.undoed = true;
          this.data.definition = this.history[this.historyIndex];
          console.log('length: ' + this.history.length, ' historyIndex : ', this.historyIndex, this.data.definition);

          this.$nextTick(function () {
            this.canvas._CONFIG.FAST_LOADING = false;
            this.canvas.updateSlider();
          })
        }
      }
      ,
      redo: function () {
        if (this.canRedo) {
          this.canvas._CONFIG.FAST_LOADING = true;
          this.historyIndex += 1
          this.undoing = true;
          this.undoed = true;
          this.data.definition = this.history[this.historyIndex]
          console.log('length: ' + this.history.length, ' historyIndex : ', this.historyIndex, this.data.definition);

          this.$nextTick(function () {
            this.canvas._CONFIG.FAST_LOADING = false;
            this.canvas.updateSlider();
          })
        }
      }
      ,
      /**
       * 새로운 트레이싱 태그를 생성한다.
       **/
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
      }
      ,
      /**
       * 데피니션에 트레이싱 태그가 있는지 확인한다.
       **/
      checkExistTracingTag: function (tracingTag) {
        var me = this, isExist = false;
        if (me.data.definition) {
          $.each(me.data.definition.childActivities[1], function (c, activity) {
            if (activity && activity.tracingTag == tracingTag) {
              isExist = true;
            }
          })
        }
        return isExist;
      }
      ,

      /**
       * 아이디에 해당하는 Bpmn 컴포넌트를 삭제한다.
       **/
      removeComponentById: function (id) {
        var me = this;
        //릴레이션 삭제
        $.each(me.data.definition.sequenceFlows, function (i, relation) {
          if (relation && relation.sourceRef + '-' + relation.targetRef + '' == id) {
            console.log('** remove sequenceFlow', id);
            me.data.definition.sequenceFlows[i] = undefined;
          }
        });
        //롤 삭제
        $.each(me.data.definition.roles, function (i, role) {
          if (role && role.elementView && role.elementView.id == id) {
            console.log('** remove role', id);
            me.data.definition.roles[i] = undefined;
          }
        });
        //액티비티 삭제
        $.each(me.data.definition.childActivities[1], function (i, activity) {
          if (activity && activity.elementView && activity.elementView.id == id) {
            console.log('** remove activitiy', id);
            me.data.definition.childActivities[1][i] = undefined;
          }
        });
      }
      ,
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


<style lang="scss" rel="stylesheet/scss">
  /*네비게이션 패널 넓이*/
  aside.navigation-drawer.navigation-drawer--absolute.navigation-drawer--is-booted.navigation-drawer--open {
    width: 400px;
  }
</style>


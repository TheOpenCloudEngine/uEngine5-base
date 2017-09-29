<template>
  <div>
    <opengraph
      focus-canvas-on-select
      wheelScalable
      dragPageMovable
      :enableContextmenu="false"
      :enableRootContextmenu="false"
      :slider="false"
      v-if="data.definition"
      ref="opengraph"
      v-on:canvasReady="bpmnReady"
      v-on:userAction="onUserAction"
      v-on:connectShape="onConnectShape"
      v-on:removeShape="onRemoveShape"
      v-on:divideLane="onDivideLane"
    >

      <!--롤은 Lane 형식의 큰 틀-->
      <div v-for="role in data.definition.roles">
        <bpmn-role v-if="role != null" :role.sync="role"></bpmn-role>
      </div>

      <!--액티비티는 각 활동 요소-->
      <div v-for="activity in data.definition.childActivities[1]">
        <!--component 로 지칭한 것은 뒤의 is 가 가르키는 컴포넌트 이름으로 뜸-->

        <!--//TODO 여기의 status 를 http://localhost:8080/instance/1/variables 에서 얻어온 status 로 교체하여야 한다.-->
        <!--ex) :status="???"-->
        <!--그러기 위해서는 SvgGraph(데이터 불러오는 부분) 에서, definition 가져온 이후에, definition 안에 있는 childActivities 를 까서-->
        <!--그 안에 tracingTag 가 동일한 것들에 대해 status 를 매핑시켜주어야 한다.-->
        <component v-if="activity != null" :is="getComponentByClassName(activity._type)"
                   :activity.sync="activity" :definition="data.definition"
                   status=""
        ></component>
      </div>

      <!--릴레이션은 액티비티간 연결선(흐름)-->
      <div v-for="relation in data.definition.sequenceFlows">
        <bpmn-relation v-if="relation != null" :relation.sync="relation"></bpmn-relation>
      </div>
    </opengraph>
    <bpmn-component-changer
      v-if="data.definition"
      :data="componentChangerData"
    >
    </bpmn-component-changer>

    <md-dialog
      v-if="data.definition"
      md-open-from="#processVariables" md-close-to="#processVariables" ref="processVariables">
      <md-dialog-title>Process Variables</md-dialog-title>

      <md-dialog-content>
        <object-grid java="org.uengine.kernel.ProcessVariable" :online="false" :data.sync="processVariables"
                     :full-fledged="true">
        </object-grid>
      </md-dialog-content>

      <md-dialog-actions>
        <md-button class="md-primary" @click="closeProcessVariables">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
  </div>
</template>

<script>
  /**
   * 디자이너를 그리는 부분.
   */
  export default {
    name: 'bpmn-vue',
    props: {
      definition: Object
    },
    mounted: function () {
      this.id = this.uuid();
      this.data.definition = this.validateDefinition(this.definition);
      var processVariables = this.data.definition.processVariableDescriptors;
      if (processVariables && processVariables.length) {
        var copy = JSON.parse(JSON.stringify(processVariables));
        $.each(copy, function (i, variable) {
          if (variable.displayName) {
            variable.displayName = variable.displayName.text;
          }
        });
        this.processVariables = copy;
      }


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
      return {
        enableHistoryAdd: false,
        processVariables: [],
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
        componentChangerData: null
      };
    },

    watch: {
      processVariables: {
        handler: function (after, before) {
          console.log('processVariables update!!', after);
          if (after && after.length) {
            var copy = JSON.parse(JSON.stringify(after));
            $.each(copy, function (i, variable) {
              if (variable.displayName) {
                variable.displayName = {
                  text: variable.displayName
                }
              }
            });
            this.data.definition.processVariableDescriptors = copy;
          }
        },
        deep: true
      },
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
      openProcessVariables(ref) {
        this.$refs['processVariables'].open();
      },
      closeProcessVariables(ref) {
        this.$refs['processVariables'].close();
      },
      /**
       * 이 과정에서는 부모-자식 양방향 통신이 필요한 요소나,
       * watch 가 필요한 요소들이 null 로 인해 감지를 하지 못할 경우를 위해
       * 값을 채워넣는다.
       *
       * 값을 채우지 않고 null 이 흘러간다면, 뒤이은 컴포넌트에서 값을 업데이트하여도 Vue 라이프사이클은 이를 감지하지 못한다.
       **/
      validateDefinition: function (value) {
        var bpmnComponent, required, me = this;
        var definition = JSON.parse(JSON.stringify(value));

        //시퀀스 플로우 검증.
        if (!definition.sequenceFlows) {
          definition.sequenceFlows = [];
        }
        bpmnComponent = me.getComponentByName('bpmn-relation');
        $.each(definition.sequenceFlows, function (i, relation) {
          required = bpmnComponent.computed.createNew();
          for (var key in required) {
            if (!relation[key]) {
              relation[key] = required[key];
            }
          }
          if (!relation.relationView.style) {
            relation.relationView.style = JSON.stringify({});
          }
        })

        //롤 검증.
        if (!definition.roles) {
          definition.roles = [];
        }
        bpmnComponent = me.getComponentByName('bpmn-role');
        $.each(definition.roles, function (i, role) {
          required = bpmnComponent.computed.createNew();
          for (var key in required) {
            if (!role[key]) {
              role[key] = required[key];
            }
          }
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
          bpmnComponent = me.getComponentByClassName(activity._type);
          required = bpmnComponent.computed.createNew();
          for (var key in required) {
            if (!activity[key]) {
              activity[key] = required[key];
            }
          }
          if (!activity.elementView.style) {
            activity.elementView.style = JSON.stringify({});
          }
        })

        //processVariableDescriptors 검증
        if (!definition.processVariableDescriptors) {
          definition.processVariableDescriptors = [];
        }

        return definition;
      },
      /**
       * 도형이 삭제되었을 경우.
       **/
      onRemoveShape: function (component) {
        console.log('remove component by user action', component.id);
        this.removeComponentByOpenGraphComponentId(component.id);
      }
      ,
      /**
       * Lane 이 분기되었을 경우.
       **/
      onDivideLane: function (dividedLane) {
        var me = this;
        var boundary = dividedLane.shape.geom.getBoundary();
        var bpmnComponent = me.getComponentByName('bpmn-role');

        var additionalData = bpmnComponent.computed.createNew(
          boundary.getCentroid().x,
          boundary.getCentroid().y,
          boundary.getWidth(),
          boundary.getHeight());

        additionalData.elementView.id = dividedLane.id;
        additionalData.elementView.parent = me.canvas.getParent(dividedLane).id;

        me.data.definition.roles.push(additionalData);
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
          var bpmnComponent = me.getComponentByName('bpmn-relation');
          var additionalData = bpmnComponent.computed.createNew(
            from.id,
            to.id,
            vertices);

          me.canvas.removeShape(edge.id, true);
          me.data.definition.sequenceFlows.push(additionalData);
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
          var bpmnComponent = me.getComponentByName('bpmn-relation');
          additionalData = bpmnComponent.computed.createNew(
            componentInfo.from,
            componentInfo.to,
            componentInfo.vertices);

          me.data.definition.sequenceFlows.push(additionalData);
        }
        //롤 추가인 경우
        else if (componentInfo.component == 'bpmn-role') {
          var bpmnComponent = me.getComponentByName('bpmn-role');

          additionalData = bpmnComponent.computed.createNew(
            componentInfo.x,
            componentInfo.y,
            componentInfo.width,
            componentInfo.height);
          me.data.definition.roles.push(additionalData);
        }
        //액티비티 추가인 경우
        else {
          var bpmnComponent = me.getComponentByName(componentInfo.component);
          if (!newTracingTag) {
            newTracingTag = me.createNewTracingTag();
          }
          console.log('newTracingTag', newTracingTag);

          additionalData = bpmnComponent.computed.createNew(
            newTracingTag,
            componentInfo.x,
            componentInfo.y,
            componentInfo.width,
            componentInfo.height);

          me.data.definition.childActivities[1].push(additionalData);
        }
      }
      ,
      /**
       * 자바 클래스로 Bpmn 컴포넌트를 가져온다.
       **/
      getComponentByClassName: function (className) {
        var componentByClassName;
        $.each(window.Vue.bpmnComponents, function (i, component) {
          if (component.computed && component.computed.className && component.computed.className() == className) {
            componentByClassName = component;
          }
        });
        return componentByClassName;
      },
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
       * 오픈그래프 컴포넌트 아이디에 해당하는 Bpmn 컴포넌트를 삭제한다.
       **/
      removeComponentByOpenGraphComponentId: function (id) {
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

</style>


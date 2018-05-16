<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <md-layout>
      <md-layout md-flex="20">
      </md-layout>

      <opengraph
        focus-canvas-on-select
        wheelScalable
        :dragPageMovable="dragPageMovable"
        :enableContextmenu="false"
        :enableRootContextmenu="false"
        :slider="false"
        :movable="!editMode"
        :resizable="!editMode"
        :selectable="!editMode"
        :connectable="!editMode"
        v-if="value"
        ref="opengraph"
        v-on:canvasReady="bindEvents"
        v-on:connectShape="onConnectShape"
      >
        <!--엘리먼트-->
        <div v-if="value[elementListBeanPath]">
          <component v-for="(element, index) in value[elementListBeanPath][1]" v-if="element != null"
                     :is="getComponentByClassName(element._type)"
                     v-model="value[elementListBeanPath][1][index]" :definition="value"
          ></component>
        </div>

        <!--릴레이션-->
        <div v-if="value[relationListBeanPath] && elementsLoadDone">
          <component v-for="(relation, index) in value[relationListBeanPath][1]" v-if="relation != null"
                     :is="relationVueComponentName" v-model="value[relationListBeanPath][1][index]" :definition="value">
          </component>
        </div>

      </opengraph>

      <md-card class="tools" style="top:100px;">
        <span class="bpmn-icon-hand-tool" v-bind:class="{ icons : !dragPageMovable, hands : dragPageMovable }"
              _width="30" _height="30" v-on:click="toggleGrip">
          <md-tooltip md-direction="right">Hands</md-tooltip>
        </span>
        <span v-for="item in elementTypes"
              class="icons draggable"
              align="center"
              :_component="item.component"
              :_width="item.width"
              :_height="item.height">
          <img height="30px" width="30px" src="../../../../static/image/symbol/class.png">
          <md-tooltip md-direction="right">{{item.label}}</md-tooltip>
        </span>
      </md-card>

      <md-layout v-if="!editMode">
        <md-input-container>
          <label>Definition Name</label>
          <md-input v-model="value.name" type="text"></md-input>
        </md-input-container>
      </md-layout>

      <md-layout v-if="versions">
        <md-input-container>
          <label>Revision</label>
          <md-select v-model="selectedVersion" @change="changeVersion">
            <md-option v-for="version in versions.slice().reverse()" :value="version">rev. {{version.ver}}
              <md-chip v-if="productionVersionId == version.versionId">production</md-chip>
            </md-option>
          </md-select>
        </md-input-container>
      </md-layout>

      <!--로케일-->
      <md-layout v-if="value" style="max-width: 200px;">
        <md-input-container>
          <label>Language</label>
          <md-select v-model="value._selectedLocale" @change="changeLocale">
            <md-option value="ko">Korean</md-option>
            <md-option value="en">English</md-option>
          </md-select>
        </md-input-container>
      </md-layout>

      <!--프로세스 세이브-->
      <md-layout v-if="!editMode" style="margin-left: 30px;">
        <md-button v-if="!editMode" @click="save">
          <md-icon v-if="id && id.indexOf('@') > 1">history</md-icon>
          <md-icon v-else>save</md-icon>
          save
        </md-button>

        <slot name="toolbar-contents"></slot>
      </md-layout>


    </md-layout>
  </div>
</template>

<script>
  export default {
    name: 'modeling-designer',
    props: {
      backend: Object,
      id: String,
      elementTypes: Array,
      value: Object,
      elementListBeanPath: String,
      relationListBeanPath: String,
      relationVueComponentName: String,
      editMode: Boolean
    },
    data() {

      return {
        canvas: null,
        dragPageMovable: false,
        timerMap: {},
        enableHistoryAdd: false,
        history: [],
        historyIndex: 0,
        undoing: false,
        undoed: false,
        canvas: null,
        propertyEditing: false,
        componentChangerData: null,
        preventEvent: false,
        relationVueComponentName: 'modeling-relation',
        elementsLoadDone: false
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

    },
    created: function () {

      //if(!this.id) this.id = "NewClassModel";

      if (this.id != null) {
        try {
          this.load();
        } catch (e) {
          console.log(e)
        }
      }


      if (!this.relationVueComponentName) this.relationVueComponentName = 'modeling-relation';

      this.history = [JSON.parse(JSON.stringify(this.value))];
      this.$nextTick(function () {
        //$nextTick delays the callback function until Vue has updated the DOM
        // (which usually happens as a result of us changing the data
        //  so make any DOM changes here
        this.canvas._CONFIG.FAST_LOADING = false;
        this.canvas.updateSlider();

        //timer end
        this.$refs.opengraph.printTimer(startTime, new Date().getTime());
      });

    },
    //컴포넌트가 Dom 에 등록되었을 떄(실제 렌더링 되기 위해 활성화 되었을 때.)
    mounted() {
    },

    //watch : prop 나, data 요소의 값이 변경됨을 감지하는 녀석.
    watch: {
      value: {
        handler: function (after, before) {

          this.$nextTick(function () {

            this.reconnectRelations()

          })


          this.$emit('input', this.value);

          if (this.enableHistoryAdd) {
            this.enableHistoryAdd = false;
          }

          if (!this.undoing) {
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
      },
    },

    methods: {

      validateDefinition: function (value) {
        return value;
      },
      /**
       * 도형이 삭제되었을 경우.
       **/
      onRemoveShape: function (component) {
        console.log('remove component by user action', component.id);
        this.removeComponentByOpenGraphComponentId(component.id);
      },

      addElement: function (componentInfo, newTracingTag, originalData) {
        this.enableHistoryAdd = true;
        var me = this;
        var additionalData = {};

        var vueComponent = me.getComponentByName(componentInfo.component);

        if (vueComponent == null)
          throw "No registered vue component named: " + componentInfo.component;

        var element;

        if (componentInfo.component == this.relationVueComponentName) {
          element = vueComponent.computed.createNew(
            componentInfo.sourceElement,
            componentInfo.targetElement,
            componentInfo.vertices,
          );
        } else {
          element = vueComponent.computed.createNew(
            this.uuid(),
            componentInfo.x,
            componentInfo.y,
            componentInfo.width,
            componentInfo.height
          );
        }

        if (componentInfo.component == this.relationVueComponentName) {
          if (!this.value[this.relationListBeanPath])
            this.value[this.relationListBeanPath] = ['java.util.ArrayList', []]

          this.value[this.relationListBeanPath][1].push(element);
        } else {
          if (!this.value[this.elementListBeanPath])
            this.value[this.elementListBeanPath] = ['java.util.ArrayList', []]

          this.value[this.elementListBeanPath][1].push(element);
        }


        this.$emit("addElement", element)
      },

      /**
       * 자바 클래스로 Bpmn 컴포넌트를 가져온다.
       **/
      getComponentByClassName: function (className) {
        var componentByClassName;
        $.each(window.Vue._components, function (i, component) {
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
        $.each(window.Vue._components, function (i, component) {
          if (component.name == name) {
            componentByName = component;
          }
        });

        return componentByName;
      },

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
      },

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
      },

      /**
       * 도형이 연결되었을 경우.
       **/
      onConnectShape: function (edge, from, to) {
        var me = this;
        //존재하는 릴레이션인 경우 (뷰 컴포넌트), 데이터 매핑에 의해 자동으로 from, to 가 변경되어있기 때문에 따로 로직은 필요없음.
        //=> 바뀌어야 함.
        //신규 릴레이션인 경우에는 릴레이션 생성
        var edgeElement, originalData;
        var isComponent = false;
        if (edge.shape) {
          edgeElement = edge;
        } else {
          isComponent = true;
          edgeElement = edge.element;
        }

        if (edgeElement && from && to) {
          var vertices = '[' + edgeElement.shape.geom.vertices.toString() + ']';
          var componentInfo = {
            component: this.relationVueComponentName,
            sourceElement: from.$parent.value,
            targetElement: to.$parent.value,
            vertices: vertices,
            isFilled: true,
            relationView: {
              style: JSON.stringify({}),
              value: vertices,
            }
          }

          from.$parent.value.elementView.id = from.id;
          to.$parent.value.elementView.id = to.id;

          if (isComponent) {
            me.canvas.removeShape(edgeElement, true);
            //this.removeComponentByOpenGraphComponentId(edgeElement.id);
            //기존 컴포넌트가 있는 경우 originalData 와 함께 생성
            this.addElement(componentInfo, null, JSON.parse(JSON.stringify(originalData)));
          } else {
            me.canvas.removeShape(edgeElement, true);
            //기존 컴포넌트가 없는 경우 신규 생성
            this.addElement(componentInfo);
          }
        }
      }
      ,

      reconnectRelations: function () {
        var me = this;

//        setTimeout(function(){
        me.elementsLoadDone = true;//}, 2000);
//        var me = this;
//        this.value[this.relationListBeanPath][1].forEach(function(relation){
//
//          var from = relation.relationView.from;
//          var to = relation.relationView.to;
//
//          relation.from = from;
//          relation.to = to;
//        });

      },


      bindEvents: function (opengraph) {
        var me = this;
        var el = me.$el;
        var canvasEl = $(opengraph.container);
        if (!canvasEl || !canvasEl.length) {
          return;
        }
        this.canvas = opengraph.canvas;
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
              me.addElement(componentInfo);
            }
            canvasEl.removeData('DRAG_SHAPE');
          }
        });
      },
      undo: function () {
        this.$refs['canvas'].undo();
      },
      redo: function () {
        this.$refs['canvas'].redo();
      },

      load: function () {
        var me = this;
        var def = {};
        var id = me.id;
        me.backend.$bind("definition/raw/" + me.id + ".json", def);
        def.$load().then(function (definition) {

          if (definition) {
            me.value = definition.definition;
            me.value.id = id;
            me.id = id;

            me.$emit("input", me.value)
            me.$emit("loadModel", me.value)
          }

        });
      },

      save: function () {

        var me = this;
        var definitionToSave = JSON.parse(JSON.stringify(me.value));

        //request definition name
        if (!me.value.name) {
          me.value.name = prompt("Specify definition name");
          if (!me.value.name) {
            return;
          }
        }

        //if new definition, set id as definitionName.
        if (!me.id) {
          me.id = me.value.name;
        }

        var nullFilter = function (array) {
          return array.filter(function (x) {
            if (x) {
              return true;
            } else {
              return false;
            }
          });
        }

        if (me.value[me.elementListBeanPath])
          me.value[me.elementListBeanPath][1] = nullFilter(me.value[me.elementListBeanPath][1]);

        if (me.value[me.relationListBeanPath])
          me.value[me.relationListBeanPath][1] = nullFilter(me.value[me.relationListBeanPath][1]);


        var definition = {};
        me.backend.$bind("definition/raw/" + me.id + ".json", definition);
        definition.definition = definitionToSave;
        definition.$save().then(
          function (definition) {
            me.$root.$children[0].success('저장되었습니다.');
            me.$emit("saved", me.id);

            me.loadVersions();

            if (nextAction) nextAction(definition);

          },
          function (response) {
            me.$root.$children[0].error('저장할 수 없습니다.');
          }
        );
      },

      changeLocale: function () {
      },

      toggleGrip: function () {
        this.dragPageMovable = !this.dragPageMovable;

        if (this.dragPageMovable) {
          this.cursorStyle = 'cursor: url("/static/image/symbol/hands.png"), auto;';
          this.handsStyle = ' color: #ffc124;';
        } else {
          this.cursorStyle = null;
          this.handsStyle = null;
        }
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

    .fullcanvas {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 10%;
      left: 0px;
      overflow: hidden;
    }

    .fullcanvashands {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 10%;
      left: 0px;
      overflow: hidden;
      cursor: url('/static/image/symbol/hands.png'), auto;
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
      .hands {
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
      .hands {
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

    .hands {
      cursor: pointer;
      font-size: 30px;
      color: #ffc124;
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

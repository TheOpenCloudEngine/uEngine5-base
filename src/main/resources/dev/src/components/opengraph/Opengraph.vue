<template>
  <div>
    <slot v-if="canvas">

    </slot>
    <div class="canvas-wrapper">
      <div class="canvas-container" :id="id">
      </div>
    </div>
    <div :id="sliderId">
    </div>
  </div>
</template>

<script>
  export default {
    name: 'opengraph',
    props: {
      //definition: Object
    },

    data: function () {
      let id = this.uuid();
      let sliderId = id + '-slider';
      return {
        id: id,
        sliderId: sliderId,
        canvas: null,
        elements: {}
      }
    },

    watch: {},

    computed: {
      opengraphRole: function () {
        return 'canvas';
      }
    },

    mounted: function () {
      this.render();
      this.bindEvents();
    },

    methods: {
      /**
       * 캔버스의 이벤트 핸들러 emit
       **/
      bindEvents: function () {
        var me = this;
        if (!me.canvas) {
          return;
        }
        /**
         * Shape 이 처음 Draw 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement)
         */
        me.canvas.onDrawShape(function (event, shapeElement) {
          me.$emit('drawShape', me.getElementById(shapeElement.id) || shapeElement);
        });

        /**
         * History Update 되었을때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event)
         */
        me.canvas.onAddHistory(function (event) {
          me.$emit('addHistory');
        });

        /**
         * Lane 이 divide 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, dividedLane)
         */
        me.canvas.onDivideLane(function (event, dividedLane) {
          me.$emit('divideLane', me.getElementById(dividedLane.id) || dividedLane);
        });

        /**
         * 라벨이 Draw 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, labelText)
         */
        me.canvas.onDrawLabel(function (event, shapeElement, labelText) {
          me.$emit('drawLabel', me.getElementById(shapeElement.id) || shapeElement, labelText);
        });

        /**
         * 라벨이 Change 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, afterText, beforeText)
         */
        me.canvas.onLabelChanged(function (event, shapeElement, afterText, beforeText) {
          me.$emit('labelChanged', me.getElementById(shapeElement.id) || shapeElement, afterText, beforeText);
        });


        /**
         * 라벨이 Change 되기전 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, afterText, beforeText)
         */
        me.canvas.onBeforeLabelChange(function (event, shapeElement, afterText, beforeText) {
          me.$emit('beforeLabelChange', me.getElementById(shapeElement.id) || shapeElement, afterText, beforeText);
        });


        /**
         * Shape 이 Redraw 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement)
         */
        me.canvas.onRedrawShape(function (event, shapeElement) {
          me.$emit('redrawShape', me.getElementById(shapeElement.id) || shapeElement);
        });

        /**
         * Shape 이 Remove 될 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement)
         */
        me.canvas.onRemoveShape(function (event, shapeElement) {
          me.$emit('removeShape', me.getElementById(shapeElement.id) || shapeElement);
        });

        /**
         * Shape 이 Rotate 될 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, angle)
         */
        me.canvas.onRotateShape(function (event, shapeElement, angle) {
          me.$emit('rotateShape', me.getElementById(shapeElement.id) || shapeElement, angle);
        });

        /**
         * Shape 이 Move 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, offset)
         */
        me.canvas.onMoveShape(function (event, shapeElement, offset) {
          me.$emit('moveShape', me.getElementById(shapeElement.id) || shapeElement, offset);
        });

        /**
         * Shape 이 Resize 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, shapeElement, offset)
         */
        me.canvas.onResizeShape(function (event, shapeElement, offset) {
          me.$emit('resizeShape', me.getElementById(shapeElement.id) || shapeElement, offset);
        });

        /**
         * Shape 이 Connect 되기전 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, edgeElement, fromElement, toElement)
         */
        me.canvas.onBeforeConnectShape(function (event, edgeElement, fromElement, toElement) {
          var result;
          me.$emit('beforeConnectShape',
            me.getElementById(edgeElement.id) || edgeElement,
            me.getElementById(fromElement.id) || fromElement,
            me.getElementById(toElement.id) || toElement,
            function (emitResult) {
              result = emitResult;
            });
          return result;
        });

        /**
         * Shape 이 Remove 되기전 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, element)
         */
        me.canvas.onBeforeRemoveShape(function (event, element) {
          var result;
          me.$emit('beforeRemoveShape',
            me.getElementById(element.id) || element,
            function (emitResult) {
              result = emitResult;
            });
          return result;
        });
        /**
         * Shape 이 Connect 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, edgeElement, fromElement, toElement)
         */
        me.canvas.onConnectShape(function (event, edgeElement, fromElement, toElement) {
          me.$emit('connectShape',
            me.getElementById(edgeElement.id) || edgeElement,
            me.getElementById(fromElement.id) || fromElement,
            me.getElementById(toElement.id) || toElement);
        });
        /**
         * Shape 이 Disconnect 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, edgeElement, fromElement, toElement)
         */
        me.canvas.onDisconnectShape(function (event, edgeElement, fromElement, toElement) {
          me.$emit('disconnectShape',
            me.getElementById(edgeElement.id) || edgeElement,
            me.getElementById(fromElement.id) || fromElement,
            me.getElementById(toElement.id) || toElement);
        });


        /**
         * Shape 이 Grouping 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, groupElement)
         */
        me.canvas.onGroup(function (event, groupElement) {
          me.$emit('group', me.getElementById(groupElement.id) || groupElement);
        });

        /**
         * Shape 이 UnGrouping 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, ungroupedElements)
         */
        me.canvas.onUnGroup(function (event, ungroupedElements) {
          var ungroupedComponents = [];
          for (var i = 0; i < ungroupedElements.lenght; i++) {
            ungroupedComponents.push(me.getElementById(ungroupedElements[i].id) || ungroupedElements[i]);
          }
          me.$emit('unGroup', ungroupedComponents);
        });

        /**
         * Group 이 Collapse 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, element)
         */
        me.canvas.onCollapsed(function (event, element) {
          me.$emit('collapsed', me.getElementById(element.id) || element);
        });

        /**
         * Group 이 Expand 되었을 때의 이벤트 리스너
         *
         * @param {Function} callbackFunc 콜백함수(event, element)
         */
        me.canvas.onExpanded(function (event, element) {
          me.$emit('expanded', me.getElementById(element.id) || element);
        });

        /**
         *
         * @param {Function} callbackFunc 콜백함수(event, edgeElement, sourceElement, targetElement)
         */
        me.canvas.onDuplicated(function (event, edgeElement, sourceElement, targetElement) {
          me.$emit('clone',
            me.getElementById(edgeElement.id) || edgeElement,
            me.getElementById(sourceElement.id) || sourceElement,
            me.getElementById(targetElement.id) || targetElement);
        });
      },
      getElementById: function (id) {
        return this.elements[id];
      },
      render: function () {
        var me = this;
        //canvas = new OG.Canvas('canvas', [1000, 800], 'transparent');
        var canvas = new OG.Canvas(this.id, [2000, 2000], '#f7f7f7', 'url(/static/image/grid.gif)');
        canvas._CONFIG.DEFAULT_STYLE.EDGE["edge-type"] = "plain";
        canvas._CONFIG.GUIDE_CONTROL_LINE_NUM = 1;
        canvas._CONFIG.FOCUS_CANVAS_ONSELECT = true;
        canvas._CONFIG.WHEEL_SCALABLE = true;
        canvas._CONFIG.DRAG_PAGE_MOVABLE = true;
        canvas._CONFIG.AUTOMATIC_GUIDANCE = true;
        canvas._CONFIG.IMAGE_BASE = '/static/image/symbol/';
        canvas._CONFIG.POOL_DROP_EVENT = true;
        canvas._CONFIG.AUTO_EXTENSIONAL = false;

        canvas.initConfig({
          selectable: true,
          dragSelectable: true,
          movable: true,
          resizable: true,
          connectable: true,
          selfConnectable: true,
          connectCloneable: false,
          connectRequired: true,
          labelEditable: false,
          groupDropable: true,
          collapsible: true,
          enableHotKey: true,
          enableContextMenu: false,
          useSlider: false,
          stickGuide: true,
          checkBridgeEdge: true,
          autoHistory: false
        });

        this.canvas = canvas;
        this.$emit('canvasReady', canvas);
      },
      addElement: function (elementComponenet, id) {
        this.elements[id] = elementComponenet;
      },
      removeElement: function (id) {
        delete this.elements[id];
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


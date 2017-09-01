<template>

</template>

<script>
  export default {
    name: 'bpmn-base',
    props: {
      activity: Object,
      role: Object,
      relation: Object,
      canvas: Object
    },
    computed: {
      className(){
        return ''
      },
      shapeId(){
        return ''
      },
      x: {
        get: function () {
          return this.getView().x
        },
        set: function (val) {
          this.getView().x = val;
        }
      },
      y: {
        get: function () {
          return this.getView().y
        },
        set: function (val) {
          this.getView().y = val;
        }
      },
      width: {
        get: function () {
          return this.getView().width
        },
        set: function (val) {
          this.getView().width = val;
        }
      },
      height: {
        get: function () {
          return this.getView().height
        },
        set: function (val) {
          this.getView().height = val;
        }
      },
      label: {
        get: function () {
          return this.getView().label
        },
        set: function (val) {
          this.getView().label = val;
        }
      },
      parent: {
        get: function () {
          return this.getView().parent
        },
        set: function (val) {
          this.getView().parent = val;
        }
      },
      id: {
        get: function () {
          return this.getView().id
        },
        set: function (val) {
          this.getView().id = val;
        }
      },
      from: {
        get: function () {
          return this.getView().from
        },
        set: function (val) {
          this.getView().from = val;
        }
      },
      to: {
        get: function () {
          return this.getView().to
        },
        set: function (val) {
          this.getView().to = val;
        }
      },
      value: {
        get: function () {
          return this.getView().value
        },
        set: function (val) {
          this.getView().value = val;
        }
      },
      //선분의 변곡점 getter
      //TODO 문제: splice 처리할 경우 모든 컴포넌트가 destroy 되는 듯 하다.
      //TODO 이것을 선분 에디트시 이벤트 처리로 바꿔야 한다. onMoveEdge
      //TODO connectWithTerminal 처리시 undo,redo 에 대응할 수 없다.
      vertices: function () {
        if (this.shape && this.shape instanceof OG.EdgeShape && this.shape.geom.vertices) {
          return this.shape.geom.vertices.toString();
        }
      }
    },
    data: function () {
      return {
        element: null,
        shape: null,
        preventWatch: false
      }
    },
    watch: {
      //선분의 변곡점 변경은 상시 감시하도록 한다.
      vertices: function (newVal, oldVal) {
        this.preventWatch = true;
        this.value = newVal;
        //this.preventWatch = false;
      },
      activity: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            console.log('activity changed');
            this.updateShape();
          }
          this.preventWatch = false;
        },
        deep: true,
      },
      role: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            console.log('role changed');
            this.updateShape();
          }
          this.preventWatch = false;
        },
        deep: true,
      },
      relation: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            console.log('relation changed');
            this.updateShape();
          }
          this.preventWatch = false;
        },
        deep: true,
      },
    },
    mounted: function () {
      console.log('Mounted!!');
      this.updateShape();
    },
    //상위 컴포넌트에 의해 삭제된 경우.
    beforeDestroy: function () {
      console.log('beforeDestroy!!', this.id);
      var me = this;
      let existElement = me.canvas.getElementById(me.id);
      if (existElement) {
        me.canvas.removeShape(existElement, true);
      }
    },
    methods: {
      getView: function () {
        if (this.activity) {
          return this.activity.elementView
        }
        if (this.role) {
          return this.role.elementView
        }
        if (this.relation) {
          return this.relation.relationView
        }
      },
      drawShape: function () {
        var me = this;
        var shape = eval('new ' + me.shapeId + '(me.label)');
        me.element = this.canvas.drawShape([me.x, me.y], shape, [me.width, me.height], me.style, me.id, me.parent, true, true);
        me.shape = me.element.shape;
        this.bindEvents();
      },
      drawEdge: function () {
        var me = this;
        var list = JSON.parse('[' + me.value + ']');
        var geom = new OG.geometry.PolyLine(list);
        geom.type = 'PolyLine';

        me.id = me.relation.sourceRef + '-' + me.relation.targetRef;
        me.element = this.canvas.connectWithTerminalId(
          me.from, me.to, null,
          me.label, me.id, me.shapeId, geom);
        me.shape = me.element.shape;
        this.bindEvents();
      },
      updateShape: function () {
        if (this.activity || this.role) {
          this.drawShape();
        } else if (this.relation) {
          this.drawEdge();
        }
      },
      updateVue: function () {
        console.log('updateVue!!');
        this.preventWatch = true;
        var me = this;
        let boundary = me.canvas.getBoundary(me.element);
        me.width = boundary.getWidth();
        me.height = boundary.getHeight();
        me.x = boundary.getCentroid().x;
        me.y = boundary.getCentroid().y;
        me.label = me.shape.label;

        var parent = me.canvas.getRenderer().getParent(me.element);
        if (parent) {
          me.parent = parent.id;
        } else {
          me.parent = null;
        }
        me.id = me.element.id;
        if (me.shape.TYPE === OG.Constants.SHAPE_TYPE.EDGE) {
          if ($(me.element).attr('_from')) {
            me.from = $(me.element).attr('_from');
          }
          if ($(me.element).attr('_to')) {
            me.to = $(me.element).attr('_to');
          }
        }
      },
      bindEvents: function () {
        var me = this;
        if (!me.shape) {
          return;
        }
        me.shape.onResize = function (offset) {

        };

        me.shape.onDrawLabel = function (text) {

        };
        me.shape.onLabelChanged = function (text, beforeText) {

        };

        me.shape.onBeforeRemoveShape = function () {

        }

        me.shape.onRemoveShape = function () {

        }

        me.shape.onDrawShape = function () {

        }

        me.shape.onBeforeLabelChange = function () {

        }

        me.shape.onRedrawShape = function () {
          me.updateVue();
        };

        me.shape.onBeforeConnectShape = function (edge, fromShape, toShape) {

        }

        me.shape.onConnectShape = function (edge, fromShape, toShape) {

        }

        me.shape.onDisconnectShape = function (edge, fromShape, toShape) {

        }

        me.shape.onGroup = function (groupShapeEle) {

        }

        me.shape.onUnGroup = function () {

        }

        me.shape.onMoveShape = function (offset) {

        }

        me.shape.onRotateShape = function (angle) {

        }

        me.shape.onDuplicated = function (target, duplicated) {

        }

        me.shape.onPasteShape = function (copied, pasted) {

        }
        me.shape.onAddToGroup = function (groupElement, elements, eventOffset) {

        }
        me.shape.onAddedToGroup = function (groupElement, element, eventOffset) {

        }

        me.shape.onSelectShape = function () {

        }

        me.shape.onDeSelectShape = function () {

        }
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>

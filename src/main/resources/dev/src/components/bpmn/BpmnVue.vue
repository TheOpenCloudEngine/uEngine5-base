<template>
  <div>
    <slot v-if="canvas" name="role"
          v-for="item in roles"
          :canvas="canvas"
          :item="item">
    </slot>
    <slot v-if="canvas" name="activity"
          v-for="item in activities"
          :canvas="canvas"
          :item="item">
    </slot>
    <slot v-if="canvas" name="relation"
          v-for="item in relations"
          :canvas="canvas"
          :item="item">
    </slot>
    <div class="canvas-container" :id="id">

    </div>
    <div :id="sliderId"></div>
  </div>
</template>

<script>
  export default {
    name: 'bpmn-vue',
    props: {
      activities: Array,
      roles: Array,
      relations: Array
    },

    data: function () {
      let id = this.uuid();
      let sliderId = id + '-slider';
      return {
        id: id,
        sliderId: sliderId,
        canvas: null
      };
    },

    watch: {},

    computed: {},

    mounted: function () {
      this.render();
      this.bindEvents();
    },

    methods: {
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
      bindEvents: function () {
        var me = this;
        var removed;
        //내부적으로 삭제된 경우
        me.canvas.onRemoveShape(function (event, element) {
          console.log('removeShape by user action', element.id);
          me.removeComponentByElement(element.id);
        });

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
          me.relations.push(relation);
          //Next Flow: onAddHistory > updateVue > definition update

          //Remove Native Edge (Random Id Shape)
          setTimeout(function () {
            me.canvas.removeShape(edgeElement, true);
          }, 10)
        })
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
      },
      removeComponentByElement: function (id) {
        var me = this;
        //릴레이션 삭제
        $.each(me.relations, function (i, relation) {
          if (relation && relation.sourceRef + '-' + relation.targetRef + '' == id) {
            me.relations[i] = null;
          }
        });
        //롤 삭제
        $.each(me.roles, function (i, role) {
          if (role && role.elementView && role.elementView.id == id) {
            me.roles[i] = null;
          }
        });
        //액티비티 삭제
        $.each(me.activities, function (i, activity) {
          if (activity && activity.elementView && activity.elementView.id == id) {
            me.activities[i] = null;
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
  .canvas-container {
    position: relative;
    width: 2000px;
    height: 2000px;
    background: #f7f7f7;
  }
</style>


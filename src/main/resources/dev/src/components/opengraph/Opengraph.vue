<template>
  <div>
    <!--<slot v-if="canvas" name="role"-->
    <!--v-for="item in filteredDefinition.roles"-->
    <!--:canvas="canvas"-->
    <!--:item="item">-->
    <!--</slot>-->
    <!--<slot v-if="canvas" name="activity"-->
    <!--v-for="item in filteredDefinition.childActivities[1]"-->
    <!--:canvas="canvas"-->
    <!--:item="item">-->
    <!--</slot>-->
    <!--<slot v-if="canvas" name="relation"-->
    <!--v-for="item in filteredDefinition.sequenceFlows"-->
    <!--:canvas="canvas"-->
    <!--:item="item">-->
    <!--</slot>-->
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
      //this.bindEvents();
    },

    methods: {
//      updateVue: function (component) {
//        component.$emit('update:width', 100);
//      },
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


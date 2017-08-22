<template>
  <div class="canvas-panel">
    <div class="canvas" :id="id">

    </div>

    <v-card class="grey lighten-4 tools">
      <v-card-text>
        <span class="icons bpmn-icon-hand-tool"></span>
        <span class="icons bpmn-icon-lasso-tool"></span>
        <span class="icons bpmn-icon-space-tool"></span>
        <span class="icons bpmn-icon-connection-multi"></span>

        <hr class="separator">

        <span class="icons bpmn-icon-start-event-none"></span>
        <span class="icons bpmn-icon-intermediate-event-none"></span>
        <span class="icons bpmn-icon-end-event-none"></span>
        <span class="icons bpmn-icon-gateway-xor"></span>
        <span class="icons bpmn-icon-task"></span>
        <span class="icons bpmn-icon-subprocess-expanded"></span>
        <span class="icons bpmn-icon-data-object"></span>
        <span class="icons bpmn-icon-data-store"></span>
        <span class="icons bpmn-icon-participant"></span>

      </v-card-text>
    </v-card>

    <v-card class="grey lighten-4 import">
      <v-card-text>
        <span class="icons fa fa-folder-open"></span>
        <span class="icons fa fa-plus-circle"></span>
      </v-card-text>
    </v-card>

    <v-card class="grey lighten-4 export">
      <v-card-text>
        <span class="icons fa fa-download"></span>
        <span class="icons fa fa-picture-o"></span>
      </v-card-text>
    </v-card>

    <v-card class="grey lighten-4 zoom">
      <v-card-text>
        <span class="icons fa fa-arrows-alt"></span>

        <hr class="separator">

        <span class="icons fa fa-plus-square-o"></span>
        <span class="icons fa fa-minus-square-o"></span>
      </v-card-text>
    </v-card>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        id: null
      }
    },
    mounted() {
      this.getDefinition();

    },
    watch: {
      '$route'(to, from) {
        this.getDefinition();
      }
    },
    methods: {
      getDefinition: function () {
        var me = this;
        this.id = this.$route.params.id;
        this.$root.codi('definition{/id}').get({id: this.id + '.json'})
          .then(function (response) {
            me.render(response.data);
          })
      },
      render: function (data) {
        var adaptor = new TypedJsonAdaptor();
        var json = adaptor.importData(data);

        //canvas = new OG.Canvas('canvas', [1000, 800], 'transparent');
        var canvas = new OG.Canvas(this.id, [1000, 800], '#f7f7f7', 'url(/static/image/grid.gif)');
        canvas._CONFIG.DEFAULT_STYLE.EDGE["edge-type"] = "plain";
        canvas._CONFIG.GUIDE_CONTROL_LINE_NUM = 1;
        canvas._CONFIG.FOCUS_CANVAS_ONSELECT = true;
        canvas._CONFIG.WHEEL_SCALABLE = true;
        canvas._CONFIG.DRAG_PAGE_MOVABLE = true;
        canvas._CONFIG.AUTOMATIC_GUIDANCE = false;
        canvas._CONFIG.IMAGE_TRASH = '/static/image/symbol/trash.png';

        canvas.initConfig({
          selectable: true,
          dragSelectable: true,
          movable: true,
          resizable: true,
          connectable: true,
          selfConnectable: true,
          connectCloneable: true,
          connectRequired: true,
          labelEditable: true,
          groupDropable: true,
          collapsible: true,
          enableHotKey: true,
          enableContextMenu: true,
          useSlider: false,
          stickGuide: true,
          checkBridgeEdge: true,
          autoHistory: false
        });

        canvas.loadJSON(json);
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

    .canvas {
      position: absolute;
      width: 100%;
      height: 100%;
      background: #f7f7f7;
      overflow: scroll;
    }

    .tools {
      position: absolute;
      width: 42px;
      left: 20px;
      top: 20px;

      .card__text {
        padding: 0px;
      }
    }

    .import, .export, .zoom {
      position: absolute;
      .card__text {
        padding: 10px 10px 10px 0px;
        height: 42px;
      }
      .icons {
        font-size: 25px;
        margin-left: 10px;
      }
    }
    .import {
      left: 20px;
      bottom: 20px;
    }
    .export {
      left: 120px;
      bottom: 20px;
    }
    .zoom {
      right: 20px;
      bottom: 120px;
      width: 42px;

      .card__text {
        padding: 0px;
        height: auto;
      }
      .icons {
        font-size: 25px;
        margin-top: 10px;
      }
    }

    .icons {
      cursor: pointer;
      font-size: 30px;
      &:hover {
        color: #ffc124;
      }
    }
  }
</style>

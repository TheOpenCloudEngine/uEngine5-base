<template>
  <div>
    <v-card v-if="bpmnComponent"
            class="grey lighten-4 change-panel"
    >
      <v-card-text>
        <span class="icons bpmn-icon-hand-tool"></span>
        <span class="icons bpmn-icon-lasso-tool"></span>
        <span class="icons bpmn-icon-space-tool"></span>
        <span class="icons bpmn-icon-connection-multi"></span>

        <hr class="separator">

        <!--<span v-for="item in dragItems"-->
        <!--class="icons draggable"-->
        <!--:class="item.icon"-->
        <!--:_component="item.component"-->
        <!--:_width="item.width"-->
        <!--:_height="item.height"-->
        <!--&gt;</span>-->
        <span>sdfsdfsdfsdf</span>

      </v-card-text>
    </v-card>
  </div>
</template>

<script>
  export default {
    name: 'bpmn-component-changer',
    props: {
      bpmnComponent: Object
    },
    computed: {},
    data: function () {
      return {
        bpmnVue: this.$parent
      };
    },
    watch: {
      /**
       * bpmnComponent = 컴포넌트를 교체할 대상.
       */
      bpmnComponent: function () {
        var canvasEl = $(this.bpmnVue.canvas._CONTAINER);
        var pageX = this.bpmnComponent.x + canvasEl.offset().left - canvasEl[0].scrollLeft;
        var pageY = this.bpmnComponent.y + canvasEl.offset().top - canvasEl[0].scrollTop;

        $(this.$el).css({
          position: 'absolute',
          width: '200px',
          height: '500px',
          top: pageY + 'px',
          left: pageX + 'px'
        })
      }
    },
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


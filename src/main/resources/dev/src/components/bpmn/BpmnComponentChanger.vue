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
  import BpmnVueFinder from './BpmnVueFinder'
  export default {
    mixins: [BpmnVueFinder],
    name: 'bpmn-component-changer',
    props: {
      data: Object
    },
    computed: {},
    data: function () {
      return {
        bpmnComponent: null
      };
    },
    watch: {
      '$props.data': function (newVal, oldVal) {
        console.log('newVal', newVal);
        if (!newVal || !newVal.bpmnComponent) {
          this.bpmnComponent = null;
          return;
        } else {
          this.bpmnComponent = newVal.bpmnComponent;
        }

        $(this.$el).css({
          position: 'absolute',
          width: '180px',
          height: 'auto',
          top: newVal.top + 'px',
          left: newVal.left + 'px'
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


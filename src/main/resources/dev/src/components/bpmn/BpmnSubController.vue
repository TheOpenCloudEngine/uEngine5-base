<template>
  <div>
    <sub-controller
      v-if="clone.intermediate"
      cloneable
      :image="'event_intermediate.png'"
      v-on:clone="intermediateClone">
      <circle-element :width="30" :height="30"></circle-element>
    </sub-controller>

    <sub-controller
      v-if="clone.end"
      cloneable
      :image="'event_end.png'"
      v-on:clone="endClone">
      <circle-element :width="30" :height="30"></circle-element>
    </sub-controller>

    <sub-controller
      v-if="clone.gateway"
      cloneable
      :image="'gateway_exclusive.png'"
      v-on:clone="exclusiveGatewayClone">
      <circle-element :width="30" :height="30"></circle-element>
    </sub-controller>

    <sub-controller
      v-if="clone.task"
      cloneable
      :image="'task.png'"
      v-on:clone="taskClone">
      <circle-element :width="30" :height="30"></circle-element>
    </sub-controller>

    <sub-controller
      v-if="clone.annotaion"
      cloneable
      :image="'annotation.png'"
      v-on:clone="annotaionClone">
      <circle-element :width="30" :height="30"></circle-element>
    </sub-controller>

    <sub-controller
      v-if="clone.wrench"
      :image="'wrench.png'"
      v-on:click="showComponentChange">
    </sub-controller>
  </div>

</template>

<script>
  export default {
    name: 'bpmn-sub-controller',
    props: {
      type: String
    },
    computed: {},
    data: function () {
      return {
        clone: {
          intermediate: true,
          end: true,
          gateway: true,
          task: true,
          annotaion: true,
          wrench: true
        }
      }
    },
    watch: {},
    mounted: function () {

      //종료 이벤트인 경우 어노테이션, 도형바꾸기만 가능.
      if (this.type == 'EndEvent') {
        this.clone.intermediate = false;
        this.clone.end = false;
        this.clone.gateway = false;
        this.clone.task = false;
      }

      //서브프로세스는 도형바꾸기 불가능
      else if (this.type == 'SubProcess') {
        this.clone.wrench = false;
      }

      //데이터나 롤은 어노테이션만 가능
      else if (this.type == 'Data' || this.type == 'Role') {
        this.clone.intermediate = false;
        this.clone.end = false;
        this.clone.gateway = false;
        this.clone.task = false;
        this.clone.wrench = false;
      }
    },
    methods: {
      intermediateClone: function () {

      },
      endClone: function () {

      },
      exclusiveGatewayClone: function () {

      },
      taskClone: function () {

      },
      annotaionClone: function () {

      },
      showComponentChange: function (component) {

      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


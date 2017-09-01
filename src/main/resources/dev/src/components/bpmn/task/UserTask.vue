<template>

</template>

<script>
  /**
   * 유저 타스크
   */
  import Task from './Task'
  export default {
    mixins: [Task],
    name: 'bpmn-user-task',
    props: {},
    computed: {
      className(){
        return 'org.uengine.kernel.HumanActivity'
      },
      shapeId(){
        return 'OG.shape.bpmn.A_HumanTask'
      }
    },
    data: function () {
      return {}
    },
    watch: {},
    mounted: function () {

    },
    methods: {}
  }


  OG.shape.bpmn.A_HumanTask = function (label) {
    OG.shape.bpmn.A_HumanTask.superclass.call(this);

    this.SHAPE_ID = 'OG.shape.bpmn.A_HumanTask';
    this.label = label;
    this.CONNECTABLE = true;
    this.GROUP_COLLAPSIBLE = false;
    this.LoopType = "None";
    this.TaskType = "User";
    this.status = "None";
  };
  OG.shape.bpmn.A_HumanTask.prototype = new OG.shape.bpmn.A_Task();
  OG.shape.bpmn.A_HumanTask.superclass = OG.shape.bpmn.A_Task;
  OG.shape.bpmn.A_HumanTask.prototype.constructor = OG.shape.bpmn.A_HumanTask;
  OG.A_HumanTask = OG.shape.bpmn.A_HumanTask;

</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


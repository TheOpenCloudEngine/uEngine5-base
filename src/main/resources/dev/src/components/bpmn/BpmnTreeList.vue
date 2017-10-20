<template>
    <li>
      <div>
        <a :href="model.id">{{model.name}}</a>
      </div>
      <ul>
        <bpmn-tree-list
          v-for="model in model.children"
          :model="model"
          :id="id">
        </bpmn-tree-list>
      </ul>
    </li>
</template>

<script>
  export default {
    name: 'bpmn-tree-list',
    props: {
      model: Object,
      id : ""
    },
    data: function () {
      return {}
    },
    mounted() {
    },
    computed: {},
    methods: {
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


<template>
    <md-list-item>
      <md-list>
        <!--md-icon>folder</md-icon -->
        <a :href="model.id">- {{model.name}}</a>
        <bpmn-tree-list
          v-for="model in model.children"
          :model="model"
          :id="id">
        </bpmn-tree-list>
      </md-list>
    </md-list-item>
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


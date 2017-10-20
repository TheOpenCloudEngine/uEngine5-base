<template>
    <md-list>
      <div
        :class="{bold: isFolder}"
        @click="toggle">
        {{model.name}}
        <span v-if="isFolder">[{{open ? '-' : '+'}}]</span>
      </div>
      <md-list-item v-show="open" v-if="isFolder">
        <bpmn-tree-list
          v-for="model in model.children"
          :model="model">
        </bpmn-tree-list>
      </md-list-item>
    </md-list>
</template>

<script>
  export default {
    name: 'bpmn-tree-list',
    props: {
      model: Object
    },
    data: function () {
      return {
        tree: {
          name: '',
          children: []
        },
        open: false
      }
    },
    computed: {
      isFolder: function () {
        return this.tree.children != null
      }
    },
    methods: {
      toggle: function () {
        if (this.isFolder) {
          this.open = !this.open
        }
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


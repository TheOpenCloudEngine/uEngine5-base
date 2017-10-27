<template>
    <md-list-item>
        {{model.name}}
        <md-button v-if="model.package" class="md-icon-button md-raised md-dense" @click="nameChange"><md-icon>edit</md-icon></md-button>
        <!--md-icon>folder</md-icon -->
        <md-list-expand v-if="model.children.length > 0">
          <md-list>
          <package-list
            v-for="model in model.children"
            :model="model">
          </package-list>
          </md-list>
        </md-list-expand>
    </md-list-item>
</template>

<script>
  import MdButton from "vue-material/src/components/mdButton/mdButton";

  export default {
    components: {MdButton},
    name: 'bpmn-tree-list',
    props: {
      model: Object,
    },
    data: function () {
      return {
        open: false
      }
    },
    mounted() {
    },
    computed: {
      isFolder: function () {
        return this.model.children &&
          this.model.children.length
      }
    },
    methods: {
      nameChange() {
        console.log("TEST", this.model.name);
      },
      toggle: function () {
        if (this.isFolder) {
          this.open = !this.open
        }
      },
      changeType: function () {
        if (!this.isFolder) {
          Vue.set(this.model, 'children', [])
          this.addChild()
          this.open = true
        }
      },
      addChild: function () {
        this.model.children.push({
          name: 'new stuff'
        })
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


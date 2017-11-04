<template>
    <md-list-item>
        {{model.name}}
        <md-button v-if="model.package" class="md-icon-button md-raised md-dense" id="renamePackage" @click="renamePackage"><md-icon>edit</md-icon></md-button>
        <rename-package
        ref="renamePackage"
        :packageName="model.name"
        style="min-width: 70%;"></rename-package>
      <md-button v-if="model.package" class="md-icon-button md-raised md-dense" id="deletePackage" @click="deletePackage"><md-icon>delete</md-icon></md-button>
      <delete-package
        ref="deletePackage"
        :packageName="model.name"
        style="min-width: 70%;"></delete-package>

        <!--md-icon>folder</md-icon -->
        <md-list-expand v-if="model.children.length > 0">
          <md-list>
          <list-package
            v-for="model in model.children"
            :model="model">
          </list-package>
          </md-list>
        </md-list-expand>
    </md-list-item>
</template>

<script>
  import MdButton from "vue-material/src/components/mdButton/mdButton";

  export default {
    components: {MdButton},
    name: 'list-package',
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
      renamePackage(ref) {
        this.$refs['renamePackage'].openPackage();
      },
      deletePackage(ref) {
        this.$refs['deletePackage'].openPackage();
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


<template>
  <md-dialog
    md-open-from="#renamePackage" md-close-to="#renamePackage" ref="renamePackage">
    <md-dialog-title>Rename Package</md-dialog-title>

    <md-dialog-content>
      <div>
        <md-input-container>
          <label>Package Name</label>
          <md-input v-model="packageNewName" type="text"></md-input>
        </md-input-container>
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="renamePackage">Save</md-button>
      <md-button class="md-primary" @click="closePackage">Close</md-button>
    </md-dialog-actions>
  </md-dialog>
</template>

<script>
  export default {
    props: {
      packageName: ""
    },

    created: function () {

    },

    data: function () {
      return {
        packageNewName: ""
      };
    },
    mounted: function () {},
    methods: {
      closePackage(ref) {
        this.$refs['renamePackage'].close();
      },
      openPackage(ref) {
        this.$refs['renamePackage'].open();
      },
      renamePackage: function () {
        var me = this;
        var src = 'definitions/packages/' + me.packageName + '/' + me.packageNewName;

        this.$root.codi(src).save()
          .then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
        this.$refs['renamePackage'].close();
      }
    }
  }
</script>


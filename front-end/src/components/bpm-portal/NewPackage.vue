<template>
  <md-dialog
    md-open-from="#newPackage" md-close-to="#newPackage" ref="newPackage">
    <md-dialog-title>New Package</md-dialog-title>

    <md-dialog-content>
      <div>
        <md-input-container>
          <label>Package Name</label>
          <md-input v-model="packageName" type="text"></md-input>
        </md-input-container>
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="createPackage">Save</md-button>
      <md-button class="md-primary" @click="closePackage">Close</md-button>
    </md-dialog-actions>
  </md-dialog>
</template>

<script>
  export default {
    props: {
      currentPath : String
    },

    created: function () {

    },

    data: function () {
      return {
        packageName: ""
      };
    },
    mounted: function () {},
    methods: {
      closePackage(ref) {
        this.$refs['newPackage'].close();
      },
      openPackage(ref) {
        this.$refs['newPackage'].open();
      },
      createPackage: function () {
        var me = this;
        var path = me.currentPath;
        if(path !== "") path += "/";
        this.$root.codi('definition').save({"name" : path + me.packageName,
          "directory" : true
        }).then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
        this.$refs['newPackage'].close();
      }
    }
  }
</script>


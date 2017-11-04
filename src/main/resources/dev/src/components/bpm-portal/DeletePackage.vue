<template>
  <md-dialog
    md-open-from="#deletePackage" md-close-to="#deletePackage" ref="deletePackage">
    <md-dialog-title>Delete Package</md-dialog-title>

    <md-dialog-content>
      <div>
          패키지를 삭제하면 <br />
          패키지에 속한 프로세스도 함께 삭제됩니다.<br />
          패지키를 삭제하시겠습니까?
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="deletePackage">Delete</md-button>
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
      return {};
    },
    mounted: function () {},
    methods: {
      closePackage(ref) {
        this.$refs['deletePackage'].close();
      },
      openPackage(ref) {
        this.$refs['deletePackage'].open();
      },
      deletePackage: function () {
        var me = this;
        var src = 'definitions/packages/' + me.packageName;
        this.$root.codi(src).delete({})
          .then(
            function (response) {
              me.$root.$children[0].success('삭제되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('삭제할 수 없습니다.');
            }
          );
        this.$refs['deletePackage'].close();
      }
    }
  }
</script>


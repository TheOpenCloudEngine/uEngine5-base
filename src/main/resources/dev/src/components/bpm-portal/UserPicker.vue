<template>
  <div>
    <md-button class="md-raised" id="userPicker" @click="openUserPicker">담당자 변경
    </md-button>
    <md-dialog
      md-open-from="#userPicker" md-close-to="#userPicker" ref="userPicker">
      <md-dialog-title>담담자 변경</md-dialog-title>

      <md-dialog-content>
        {{userlist}}
      </md-dialog-content>

      <md-dialog-actions>
        <md-button class="md-primary" @click="closeUserPicker">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
  </div>
</template>

<script>
  export default {
    props: {
      iam: Object
    },

    created: function () {

    },

    data: function () {
      return {
        userlist: null
      };
    },
    mounted: function () {
      var me = this;
      me.iam.getUserSearch(null, 0, 10)
        .then(function (response) {
          me.userlist = response;
        });
    },

    methods: {
      openUserPicker(ref) {
        this.$refs['userPicker'].open();
      },
      closeUserPicker(ref) {
        this.$refs['userPicker'].close();
      },

    }
  }
</script>


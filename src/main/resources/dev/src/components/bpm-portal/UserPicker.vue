<template>
  <md-dialog
    md-open-from="#userPicker" md-close-to="#userPicker" ref="userPicker">
    <md-dialog-title>사용자 선택</md-dialog-title>

    <md-dialog-content>
      <div v-for="(role,index) in roles" :key="role.name">
        <md-layout md-gutter >
          <md-layout md-flex="25" md-align="center">
            <span>{{role.name}}</span>
          </md-layout>
          <md-layout md-flex="75">
            <user-autocomplete
              :role="role.name"
              ></user-autocomplete>
          </md-layout>
        </md-layout>
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="confirmUser">Confirm</md-button>
      <md-button class="md-primary" @click="closeUserPicker">Close</md-button>
    </md-dialog-actions>
  </md-dialog>
</template>

<script>
  export default {
    props: {
      roles: Array,
      id: String
    },

    created: function () {

    },

    data: function () {
      return {
        users: {}
      };
    },
    mounted: function () {
      var me = this;
      console.log(me.roles);
    },
    methods: {
      closeUserPicker(ref) {
        this.$refs['userPicker'].close();
      },
      openUserPicker(ref) {
        this.$refs['userPicker'].open();
      },
      confirmUser: function () {
        var me = this;
        $.each(me.roles, function (index, arg) {
          if (me.users[arg.name] != undefined) {
            var data = {_type: "org.uengine.kernel.RoleMapping", endpoint: me.users[arg.name]};
            me.$root.codi('instance{/id}/role-mapping{/roleName}').save({id: me.id, roleName: arg.name}, data)
              .then(function (response) {
              })
          }
        })
        this.$refs['userPicker'].close();
      }
    }
  }
</script>


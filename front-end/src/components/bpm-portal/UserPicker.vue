<template>
  <md-dialog
    md-open-from="#userPicker" md-close-to="#userPicker" ref="userPicker">
    <md-dialog-title>Role Mappings</md-dialog-title>

    <md-dialog-content>
      <div v-for="(role,index) in roles" :key="role.name">
          <md-input-container>
              <label>{{role.name}}</label>
              <user-autocomplete
                v-model="users[role.name]"
                ></user-autocomplete>
          </md-input-container>
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
      this.loadUsers()
    },

    data: function () {
      return {
        users: {}
      };
    },
    mounted: function () {
      var me = this;
    },
    methods: {
      closeUserPicker(ref) {
        this.$refs['userPicker'].close();
      },
      openUserPicker(ref) {
        this.loadUsers();
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
      },

      loadUsers: function(){
        var me = this;
        $.each(me.roles, function (index, arg) {
            me.$root.codi('instance{/id}/role-mapping{/roleName}').get({id: me.id, roleName: arg.name})
              .then(function (response) {
                //me.users[arg.name] = response.data.endpoint;  ---- X
                Vue.set(me.users, arg.name, response.data.endpoint) //----O
              })
        })
      }
    }
  }
</script>


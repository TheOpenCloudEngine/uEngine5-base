<template>
  <div style="min-width: 70%;">
    <md-button class="md-raised" id="userPicker" @click="openUserPicker">담당자 변경
    </md-button>
    <md-dialog
      md-open-from="#userPicker" md-close-to="#userPicker" ref="userPicker">
      <md-dialog-title>담당자 변경</md-dialog-title>

      <md-dialog-content>
        <div v-for="(role,index) in roles" :key="role.name">
          <md-layout>
            <md-layout md-flex="25">
              <span>{{role.name}}</span>
            </md-layout>
            <md-layout md-flex="75">
              <user-autocomplete
                        :iam="iam"
                        :role="role"
                        :id="id"
                        v-if="iam"></user-autocomplete>
            </md-layout>
          </md-layout>
        </div>
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
      iam: Object,
      definition: Object,
      id:String
    },

    created: function () {

    },

    data: function () {
      return {
        roles: ""
      };
    },
    mounted: function () {
      var me = this;
      me.roles = me.definition.roles;
//      let split = me.definition.id.split('/');
      console.log(me.definition.id);
//      console.log(split[split.length -3]);
    },
    methods: {
      openUserPicker(ref) {
        this.$refs['userPicker'].open();
      },
      closeUserPicker(ref) {
        this.$refs['userPicker'].close();
      },
      confirmUser: function () {
        this.$refs['userPicker'].close();
      }
    }
  }
</script>


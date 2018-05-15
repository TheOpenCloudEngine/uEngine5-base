<template>
  <md-input-container>
    <label>Find User</label>
    <user-autocomplete
      role="role.name"
      @userSelected:user:role="userSelected"
      ></user-autocomplete>
  </md-input-container>
</template>

<script>
  export default {
    props: {
      value: Object,
    },

    created: function () {
    },

    data: function () {
      return {
        users: {}
      };
    },

    methods: {
      userSelected: function (item,role) {
        this.value = {
          _type: 'org.uengine.kernel.RoleMapping',
          endpoint: item
        };

        this.$emit("input", this.value);
      }
    }
  }
</script>

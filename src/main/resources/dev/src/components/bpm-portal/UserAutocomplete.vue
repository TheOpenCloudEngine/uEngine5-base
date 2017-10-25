<template>
  <md-input-container>
    <label>전체 {{filtered}} 중 {{total}} 선택됨</label>
    <md-autocomplete v-model="searchKeyword"
                     :list="items"
                     print-attribute="userName"
                     :filter-list="getFilterList"
                     :min-chars="0"
                     :max-height="6"
                     @selected="selectCallback"
                     :debounce="1000">
    </md-autocomplete>
    <!--<md-button class="md-icon-button md-raised md-primary">-->
    <!--<md-icon style="color: #FFFFFF">search</md-icon>-->
    <!--</md-button>-->
  </md-input-container>
  <!--<md-chips md-input-type="button">-->
  <!--<template></template>-->
  <!--</md-chips>-->
</template>

<script>
  export default {
    props: {
      iam: Object,
      role: Object,
      id: String
    },

    created: function () {
    },

    data: function () {
      return {
        searchKeyword: "",
        total: "",
        filtered: "",
        selectedName: "",
        users: {},
        items: [
          {
            _rev: "",
            level: 0,
            docType: "",
            name: "",
            _id: "",
            email: "",
            regDate: "",
            userPassword: "",
            enable: false,
            updDate: "",
            userName: "",
            managementId: "",
          }
        ],
      };
    },
    mounted: function () {
      var me = this;
      me.iam.getUserSearch("", 0, 10)
        .then(function (response) {
          me.items = response.data;
          me.filtered = response.filtered;
          me.total = response.total;
        });
    },
    methods: {
      getFilterList: function (list, param) {
        var me = this;
        me.iam.getUserSearch(param, 0, 10)
          .then(function (response) {
            me.items = response.data;
            me.filtered = response.filtered;
            me.total = response.total;
            1
          });
        return me.items;
      },
      selectCallback: function (item) {
        var me = this;
        me.selectedName = item.name;
        me.total = 1;
        me.$set(me.users, me.role.name, item.userName);
        var data = {_type:"org.uengine.kernel.RoleMapping",endpoint:item.userName};
        console.log(me.id);
        me.$root.codi('instance{/id}/role-mapping{/roleName}').save({id: me.id,roleName:me.role.name },data)
          .then(function (response) {
            console.log(response);
          })
      }
    }
  }

</script>


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
      role: String,
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
        iam:{},
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
      me.$root.$children[0].$children[3].iam.getUserSearch("", 0, 10)
        .then(function (response) {
          me.items = response.data;
          me.filtered = response.filtered;
          me.total = response.total;
        });
    },
    methods: {
      getFilterList: function (list, param) {
        var me = this;
        me.$root.$children[0].$children[3].iam.getUserSearch(param, 0, 10)
          .then(function (response) {
            me.items = response.data;
            me.filtered = response.filtered;
            me.total = response.total;
          });
        return me.items;
      },
      selectCallback: function (item) {
        var me = this;
        me.selectedName = item.name;
        me.total = 1;
        me.$set(me.$parent.$parent.$parent.$parent.$parent.users, me.role.name, item.userName);
//        me.$set(me.users, me.role.name, item.userName);
      }
    }
  }

</script>


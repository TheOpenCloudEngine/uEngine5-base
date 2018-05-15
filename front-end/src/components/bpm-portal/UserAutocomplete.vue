<template>
    <md-autocomplete v-model="value"
                     :list="items"
                     print-attribute="endpoint"
                     :filter-list="getFilterList"
                     :min-chars="0"
                     :max-height="6"
                     @selected="selectCallback"
                     :debounce="500">
    </md-autocomplete>
</template>

<script>
  export default {
    props: {
      id: String,
      value: Object
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
//            console.log(response.data);
            if (response.data.length > 0 ) {
              me.items = response.data;
              me.filtered = response.filtered;
              me.total = response.total;

              me.items.forEach(function(item){
                item.endpoint = item.userName;
              })
            } else {
              me.total = "0";
              me.items =[{userName:""}];
            }
          });
        return me.items;
      },
      selectCallback: function (item) {
        var me = this;
        me.selectedName = item.name;
        me.total = 1;
        me.$emit('userSelected:user:role',item.userName,me.role);

        me.value = item.endpoint;
        this.$emit("input", me.value);
      }
    }
  }

</script>


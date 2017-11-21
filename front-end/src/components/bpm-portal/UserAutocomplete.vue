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
                     :debounce="500">
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
//            console.log(response.data);
            if (response.data.length > 0 ) {
              me.items = response.data;
              me.filtered = response.filtered;
              me.total = response.total;
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
        //$emit은 자식컴포넌트가 부모컴포넌트에게 값을 전달하기 위한 이벤트 리스너이며 부모 컴포넌트의 v-on이벤트와 매핑된다.
        // $emit은 부모 Vue의 method를 실행한다.
//        me.$set(me.$emit('userSelected').users, me.$emit('userSelected').role, item.userName);
        me.$emit('userSelected:user:role',item.userName,me.role);
      }
    }
  }

</script>


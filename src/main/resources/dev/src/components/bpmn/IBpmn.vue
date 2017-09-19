<template>

</template>

<script>
  export default {
    name: 'bpmn-base',
    props: {
      activity: Object,
      role: Object,
      relation: Object,
      status: String,
      loopType: String
    },
    created: function () {

    },
    data: function () {
      return {
        _id: null,
        bpmnVue: null,
        drawer: false
      }
    },
    computed: {
      type(){
        return ''
      },
      style: {
        get: function () {
          var style;
          //스타일이 없다면 디폴트 스타일을 사용한다.
          if (this.activity) {
            style = this.activity.elementView.style;
          } else if (this.role) {
            style = this.role.elementView.style;
          } else if (this.relation) {
            style = this.relation.relationView.style;
          }
          if (style) {
            var jsonStyle = JSON.parse(style);
            if ($.isEmptyObject(jsonStyle)) {
              return this.defaultStyle;
            } else {
              return jsonStyle;
            }
          } else {
            return this.defaultStyle;
          }
        },
        set: function (val) {
          if (this.activity) {
            this.activity.elementView.style = JSON.stringify(val);
          } else if (this.role) {
            this.role.elementView.style = JSON.stringify(val);
          } else if (this.relation) {
            this.relation.relationView.style = JSON.stringify(val);
          }
        }
      }
    },
    watch: {},
    mounted: function () {
      //이 bpmn 요소(액티비티,롤,릴레이션)의 BpmnVue 를 등록한다.
      var bpmnVue = null;
      var parent;
      var getParent = function (component) {
        parent = component.$parent;
        if (parent) {
          if (parent.bpmnRole == 'bpmn-vue') {
            bpmnVue = parent;
          } else {
            getParent(parent);
          }
        }
      }
      getParent(this);
      this.bpmnVue = bpmnVue;
    },
    methods: {
      showProperty: function (component) {
        this.drawer = true;
      },
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>

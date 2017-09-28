<template>

</template>

<script>
  import BpmnVueFinder from './BpmnVueFinder'
  export default {
    mixins: [BpmnVueFinder],
    name: 'bpmn-base',
    props: {
      activity: Object,
      definition: Object,
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
        drawer: false
      }
    },
    computed: {
      type(){
        return ''
      },
      bpmnRole: function () {
        return 'bpmn-component';
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
    watch: {
//      activity: {
//        handler: function (after, before) {
//          console.log('activity changed');
//          this.$emit('activity', after);
//        },
//        deep: true
//      },
//      role: {
//        handler: function (after, before) {
//          console.log('role changed');
//          this.$emit('role', after);
//        },
//        deep: true
//      },
//      relation: {
//        handler: function (after, before) {
//          console.log('relation changed');
//          this.$emit('relation', after);
//        },
//        deep: true
//      }
    },
    mounted: function () {
    },
    methods: {
      closeComponentChanger: function () {
        console.log('closeComponentChanger');
        this.bpmnVue.componentChangerData = {};
      },
      openComponentChanger: function (top, left) {
        var me = this;
        //TODO 저 프로퍼티 패널이 deselect 되었을 때 닫히지 않게 하려면...?


        console.log(top, left);
        //opengraphComponent 에서 x.y 뽑기.
        console.log('this.bpmnVue.componentChangerData', this.bpmnVue.componentChangerData);
        this.bpmnVue.componentChangerData = {
          bpmnComponent: me,
          top: top,
          left: left
        };
      },
      showProperty: function (event, component) {
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

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
//          //console.log(JSON.stringify(after));
//
////          console.log('activity changed');
////          this.$emit('activity', after);
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
      selectedActivity: function () {
          if(this.activity)
            this.activity._selected = true;
      },
      deSelectedActivity: function () {
          if(this.activity)
            this.activity._selected = false;
      },
      selectedFlow: function () {
          this.relation.selected = true;
      },
      deSelectedFlow: function () {
          this.relation.selected = false;
      },
      closeComponentChanger: function () {
        console.log('closeComponentChanger');
        this.bpmnVue.componentChangerData = {};
      },
      openComponentChanger: function (top, left) {
        var me = this;
        //TODO 저 프로퍼티 패널이 deselect 되었을 때 닫히지 않게 하려면...?

        console.log(top, left);
        this.bpmnVue.componentChangerData = {
          bpmnComponent: me,
          top: top,
          left: left
        };
      },
      showProperty: function (event, component) {
        this.drawer = true;
      },
      /**
       * 도형이 그룹 속으로 이동했을 때 데피니션의 모델을 이동시킨다.
       * @param groupOpengraphComponent 오픈그래프 그룹 컴포넌트
       * @param opengraphComponent 이동된 오픈그래프 컴포넌트
       * @param eventOffset
       */
      onAddedToGroup: function (groupOpengraphComponent, opengraphComponent, eventOffset) {
        console.log('onAddedToGroup!!');
        var me = this;

        //액티비티가 아닐 경우 스킵.
        if (!me.activity) {
          return;
        }

        //아래 작업이 수행되기 전 데피니션 히스토리 업데이트 금지.
        me.bpmnVue.preventEvent = true;

        me.$nextTick(function () {
          //서브프로세스 안에는 lane 들어갈 수 없으니 안심하자!!! 고려하지 말자!!!
          me.bpmnVue.preventEvent = false;
          me.bpmnVue.enableHistoryAdd = true;

          //내 자신의 트래이싱 태그
          var myTracingTag = me.activity.tracingTag;

          //신규 서브 프로세스
          var newSubProcess = me.bpmnVue.getActAndRelByOpengraphId(groupOpengraphComponent.id);

          //기존 서브 프로세스
          var currentSubProcess = me.bpmnVue.getParentActByOpengraphId(myTracingTag);

          //신규 서브프로세스가 없을때, 기존 서브프로세스가 있다면 데피니션으로 이동.
          if (!newSubProcess) {
            if (currentSubProcess) {
              me.bpmnVue.moveActivity(myTracingTag, null);
            }
          }

          //신규 서브프로세스가 있을때
          //기존 서브프로세스가 있고, 기존 서브프로세스와 트레이싱 태그가 같다면 스킵. (같은 서브프로세스 내부의 이동이다.)
          //신규 서브프로세스가 서브프로세스면 신규그룹 속으로 이동.
          //신규 서브프로세스가 서브프로세스가 아니면 데피니션으로 이동.
          else {
            if (currentSubProcess && newSubProcess.tracingTag == currentSubProcess.tracingTag) {
              console.log('currentSubProcess == newSubProcess', newSubProcess.tracingTag);
              return;
            }
            if (newSubProcess._type == 'org.uengine.kernel.bpmn.SubProcess') {
              me.bpmnVue.moveActivity(myTracingTag, newSubProcess.tracingTag);
            } else {
              me.bpmnVue.moveActivity(myTracingTag, null);
            }
          }
        });
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

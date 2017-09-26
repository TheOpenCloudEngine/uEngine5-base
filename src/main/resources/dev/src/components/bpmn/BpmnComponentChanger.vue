<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div>
    <md-card v-if="bpmnComponent">
      <md-card-content class="no-padding">
        <div v-if="bpmnComponent.type == 'Task'">
          <span class="icons bpmn-icon-parallel-mi-marker"></span>
          <span class="icons bpmn-icon-sequential-mi-marker"></span>
          <span class="icons bpmn-icon-loop-marker"></span>

          <hr class="separator">
        </div>

        <div v-if="bpmnComponent.type == 'StartEvent'">
          <p class="icons bpmn-icon-start-event-none">
            <span class="icon-text">Start Event</span>
          </p>
          <p v-on:click="chage('bpmn-terminate-end-event')" class="icons bpmn-icon-start-event-message">
            <span class="icon-text">Message Start Event</span>
          </p>
          <p class="icons bpmn-icon-start-event-timer">
            <span class="icon-text">Timer Start Event</span>
          </p>
          <p class="icons bpmn-icon-start-event-condition">
            <span class="icon-text">Conditional Start Event</span>
          </p>
          <p class="icons bpmn-icon-start-event-signal">
            <span class="icon-text">Signal Start Event</span>
          </p>
        </div>

        <div v-if="bpmnComponent.type == 'IntermediateEvent'">
          <p class="icons bpmn-icon-intermediate-event-none">
            <span class="icon-text">Intermediate Throw Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-catch-message">
            <span class="icon-text">Message Intermediate Catch Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-throw-message">
            <span class="icon-text">Message Intermediate Throw Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-catch-timer">
            <span class="icon-text">Timer Intermediate Catch Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-throw-escalation">
            <span class="icon-text">Escalation Intermediate Throw Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-catch-condition">
            <span class="icon-text">Conditional Intermediate Catch Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-catch-link">
            <span class="icon-text">Link Intermediate Catch Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-throw-link">
            <span class="icon-text">Link Intermediate Throw Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-throw-compensation">
            <span class="icon-text">Compensation Intermediate Throw Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-catch-signal">
            <span class="icon-text">Signal Intermediate Catch Event</span>
          </p>
          <p class="icons bpmn-icon-intermediate-event-throw-signal">
            <span class="icon-text">Signal Intermediate Throw Event</span>
          </p>
        </div>
      </md-card-content>
    </md-card>
  </div>
</template>

<script>
  import BpmnVueFinder from './BpmnVueFinder'
  export default {
    mixins: [BpmnVueFinder],
    name: 'bpmn-component-changer',
    props: {
      data: Object
    },
    computed: {},
    data: function () {
      return {
        bpmnComponent: null
      };
    },
    watch: {
      '$props.data': function (newVal, oldVal) {
        if (!newVal || !newVal.bpmnComponent) {
          this.bpmnComponent = null;
          return;
        } else {
          this.bpmnComponent = newVal.bpmnComponent;
        }

        $(this.$el).css({
          position: 'absolute',
          width: '260px',
          height: 'auto',
          top: newVal.top + 'px',
          left: newVal.left + 'px'
        })
      }
    },
    mounted: function () {

    },
    methods: {
      chage: function (componentName) {
//        this.bpmnComponent.activity.elementView.component = componentName;
//
//        //스타일 초기화.
//        this.bpmnComponent.activity.elementView.style = JSON.stringify({});

        //여기서, 기존 컴포넌트를 삭제하고 새로운 컴포넌트를 생성할 필요가 있음.

        var newActivity = JSON.parse(JSON.stringify(this.bpmnComponent.activity));
        //newActivity.elementView.component = componentName;

        var component = this.bpmnVue.getComponentByName(componentName);
        newActivity._type = component.computed.className();
        newActivity.elementView.style = JSON.stringify({});

        console.log('newActivity', newActivity);
        this.bpmnVue.enableHistoryAdd = true;
        this.bpmnVue.removeComponentById(this.bpmnComponent.id);
        this.bpmnVue.data.definition.childActivities[1].push(newActivity);

        //여기서 변환한 것을 다시 change 하면, 먹히지 않는이유?

        //콘솔찍어보기
        setTimeout(function () {
          console.log('newActivity', newActivity);
        }, 1000);

      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">
  .md-card .md-card-content {
    padding: 5px 0 0 0;
  }

  .md-card .md-card-content:last-child {
    padding-bottom: 0px;
  }

  .icons {
    font-size: 25px;
    margin-left: 10px;
  }

  p.icons {
    margin-bottom: 0px;
    .icon-text {
      font-size: 11px;
    }
  }

</style>


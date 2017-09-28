<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div>
    <md-card v-if="bpmnComponent">
      <md-card-content class="no-padding">
        <div v-if="bpmnComponent.type == 'Task' || bpmnComponent.type == 'SubProcess'">
          <span class="icons bpmn-icon-parallel-mi-marker"></span>
          <span class="icons bpmn-icon-sequential-mi-marker"></span>
          <span class="icons bpmn-icon-loop-marker"></span>

          <hr class="separator">
        </div>

        <p v-for="item in items"
           class="icons"
           :class="item.icon"
           v-on:click="change(item.component)"
        >
          <span class="icon-text">{{item.label}}</span>
        </p>
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
        bpmnComponent: null,
        items: []
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

        this.getComponentChangeItems();
      }
    },
    mounted: function () {

    },
    methods: {
      getComponentChangeItems: function () {
        if (this.bpmnComponent.type == 'StartEvent') {
          this.items = [
            {
              component: 'bpmn-start-event',
              label: 'Start Event',
              icon: 'bpmn-icon-start-event-none'
            },
            {
              component: 'bpmn-message-start-event',
              label: 'Message Start Event',
              icon: 'bpmn-icon-start-event-message'
            },
            {
              component: 'bpmn-timer-start-event',
              label: 'Timer Start Event',
              icon: 'bpmn-icon-start-event-timer'
            },
            {
              component: 'bpmn-conditional-start-event',
              label: 'Conditional Start Event',
              icon: 'bpmn-icon-start-event-condition'
            },
            {
              component: 'bpmn-signal-start-event',
              label: 'Signal Start Event',
              icon: 'bpmn-icon-start-event-signal'
            }
          ]
        }
        else if (this.bpmnComponent.type == 'IntermediateEvent') {
          this.items = [
            {
              component: 'bpmn-intermediate-event',
              label: 'Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-none'
            },
            {
              component: 'bpmn-message-intermediate-catch-event',
              label: 'Message Intermediate Catch Event',
              icon: 'bpmn-icon-intermediate-event-catch-message'
            },
            {
              component: 'bpmn-message-intermediate-throw-event',
              label: 'Message Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-throw-message'
            },
            {
              component: 'bpmn-timer-intermediate-catch-event',
              label: 'Timer Intermediate Catch Event',
              icon: 'bpmn-icon-intermediate-event-catch-timer'
            },
            {
              component: 'bpmn-escalation-intermediate-throw-event',
              label: 'Escalation Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-throw-escalation'
            },
            {
              component: 'bpmn-conditional-intermediate-catch-event',
              label: 'Conditional Intermediate Catch Event',
              icon: 'bpmn-icon-intermediate-event-catch-condition'
            },
            {
              component: 'bpmn-link-intermediate-catch-event',
              label: 'Link Intermediate Catch Event',
              icon: 'bpmn-icon-intermediate-event-catch-link'
            },
            {
              component: 'bpmn-link-intermediate-throw-event',
              label: 'Link Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-throw-link'
            },
            {
              component: 'bpmn-compensation-intermediate-throw-event',
              label: 'Compensation Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-throw-compensation'
            },
            {
              component: 'bpmn-signal-intermediate-catch-event',
              label: 'Signal Intermediate Catch Event',
              icon: 'bpmn-icon-intermediate-event-catch-signal'
            },
            {
              component: 'bpmn-signal-intermediate-throw-event',
              label: 'Signal Intermediate Throw Event',
              icon: 'bpmn-icon-intermediate-event-throw-signal'
            }
          ]
        }
        else if (this.bpmnComponent.type == 'EndEvent') {
          this.items = [
            {
              component: 'bpmn-end-event',
              label: 'End Event',
              icon: 'bpmn-icon-end-event-none'
            },
            {
              component: 'bpmn-escalation-end-event',
              label: 'Escalation End Event',
              icon: 'bpmn-icon-end-event-escalation'
            },
            {
              component: 'bpmn-error-end-event',
              label: 'Error End Event',
              icon: 'bpmn-icon-end-event-error'
            },
            {
              component: 'bpmn-compensation-end-event',
              label: 'Compensation End Event',
              icon: 'bpmn-icon-end-event-compensation'
            },
            {
              component: 'bpmn-signal-end-event',
              label: 'Signal End Event',
              icon: 'bpmn-icon-end-event-signal'
            },
            {
              component: 'bpmn-terminate-end-event',
              label: 'Terminate End Event',
              icon: 'bpmn-icon-end-event-terminate'
            }
          ]
        }
        else if (this.bpmnComponent.type == 'Gateway') {
          this.items = [
            {
              component: 'bpmn-gateway',
              label: 'Gateway',
              icon: 'bpmn-icon-gateway-none'
            },
            {
              component: 'bpmn-parallel-gateway',
              label: 'Parallel Gateway',
              icon: 'bpmn-icon-gateway-parallel'
            },
            {
              component: 'bpmn-exclusive-gateway',
              label: 'Exclusive Gateway',
              icon: 'bpmn-icon-gateway-xor'
            },
            {
              component: 'bpmn-inclusive-gateway',
              label: 'Inclusive Gateway',
              icon: 'bpmn-icon-gateway-or'
            },
            {
              component: 'bpmn-complex-gateway',
              label: 'Complex Gateway',
              icon: 'bpmn-icon-gateway-complex'
            },
            {
              component: 'bpmn-eventbased-gateway',
              label: 'EventBased Gateway',
              icon: 'bpmn-icon-gateway-eventbased'
            }
          ]
        }
        else if (this.bpmnComponent.type == 'Task') {
          this.items = [
            {
              component: 'bpmn-task',
              label: 'Task',
              icon: 'bpmn-icon-task-none'
            },
            {
              component: 'bpmn-send-task',
              label: 'Send Task',
              icon: 'bpmn-icon-send-task'
            },
            {
              component: 'bpmn-receive-task',
              label: 'Receive Task',
              icon: 'bpmn-icon-receive-task'
            },
            {
              component: 'bpmn-user-task',
              label: 'User Task',
              icon: 'bpmn-icon-user-task'
            },
            {
              component: 'bpmn-manual-task',
              label: 'Manual Task',
              icon: 'bpmn-icon-manual-task'
            },
            {
              component: 'bpmn-business-task',
              label: 'Business Task',
              icon: 'bpmn-icon-business-rule-task'
            },
            {
              component: 'bpmn-service-task',
              label: 'Service Task',
              icon: 'bpmn-icon-service-task'
            },
            {
              component: 'bpmn-script-task',
              label: 'Script Task',
              icon: 'bpmn-icon-script-task'
            },
            {
              component: 'bpmn-call-activity',
              label: 'Call Activity',
              icon: 'bpmn-icon-call-activity'
            }
          ]
        }
        else if (this.bpmnComponent.type == 'SubProcess') {
          this.items = [
            {
              component: 'bpmn-subprocess',
              label: 'Sub Process',
              icon: 'bpmn-icon-subprocess-collapsed'
            },
            {
              component: 'bpmn-transaction',
              label: 'Transaction',
              icon: 'bpmn-icon-transaction'
            },
            {
              component: 'bpmn-event-subprocess',
              label: 'Event Sub Process',
              icon: 'bpmn-icon-event-subprocess-expanded'
            }
          ]
        }
        //그외 데이터, Pool, Lane 은 바꿀 메뉴 없음.
      },
      change: function (componentName) {
        var newActivity = JSON.parse(JSON.stringify(this.bpmnComponent.activity));
        //newActivity.elementView.component = componentName;

        var component = this.bpmnVue.getComponentByName(componentName); //TODO :  getComponentByName 은 공통
        newActivity._type = component.computed.className(); //change the type
        newActivity.elementView.style = JSON.stringify({});

        //기존 액티비티를 삭제하고 신규 액티비티를 인서트한다.
        var tracingTag = this.bpmnComponent.activity.tracingTag;
        var definition = this.bpmnVue.data.definition;
        $.each(definition.childActivities[1], function (i, activity) {
          if (activity && activity.tracingTag == tracingTag) {
            console.log('** remove activitiy by component change', tracingTag);
            definition.childActivities[1][i] = undefined;
          }
        });
        definition.childActivities[1].push(newActivity);

        this.$nextTick(function () {
          //오픈그래프 컴포넌트 중 from, to 인 것을 redraw 시킨다.
          var opengraphComponent;
          for (var id in this.bpmnVue.$refs['opengraph'].elements) {
            opengraphComponent = this.bpmnVue.$refs['opengraph'].elements[id];
            if (opengraphComponent.to == tracingTag || opengraphComponent.from == tracingTag) {
              console.log('redraw relation by component change.', opengraphComponent.id);
              opengraphComponent.updateShape();
            }
          }

          //히스토리를 갱신한다.
          this.bpmnVue.onUserAction();
        });
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
    margin-top: 0px;
  }

  p.icons {
    margin-bottom: 0px;
    .icon-text {
      font-size: 11px;
    }
  }

</style>


<template>

  <md-card v-if="workItem">
    <md-card-area>
      <md-card-header>
        <div class="md-title">{{workItem.activity.name}}</div>
      </md-card-header>

      <md-card-content>
        {{workItem.activity.description ? workItem.activity.description.text : ""}}
      </md-card-content>
    </md-card-area>

    <md-card-actions>
      <md-button @click.native="complete">완료</md-button>
      <md-button>저장</md-button>
      <md-button>건너뛰기</md-button>
    </md-card-actions>
  </md-card>

</template>


<script>
  export default {
    props: {
      taskId: String,
    },

    watch: {
      taskId: function (val) {
        this.load();
      }
    },

    data: function () {
      return {
        workItem: null,
      };
    },

    methods: {
      load: function () {
        var me = this;
        var serviceLocator = this.$root.$children[0].$refs["backend"]; //TODO hardcoded
        serviceLocator.invoke({
          path: this.taskId.replace("worklist", "work-item"),
          success: function (value) {
            me.workItem = value;
            console.log('value', value);
          }
        });

      },
      complete: function () {
        var serviceLocator = this.$root.$children[0].$refs["backend"]; //TODO hardcoded

        var me = this;

        serviceLocator.invoke({
          path: this.taskId.replace("worklist", "work-item"),
          method: 'POST',
          data: {
            worklist: {
              status: 'COMPLETED'
            }
          },
          success: function (value) {
            me.workItem = null;

            me.$root.$children[0].success('작업을 완료했습니다.');
            me.$emit('update:reload', true);
          }
        });

      }
    }
  }
</script>


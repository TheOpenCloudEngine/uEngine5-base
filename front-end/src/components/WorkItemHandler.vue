<template>

  <md-card v-if="workItem">
    <md-card-area>
      <md-card-header>
        <div class="md-title">{{workItem.activity.name}}</div>
      </md-card-header>

      <md-card-content>
        {{workItem.activity.description ? workItem.activity.description.text : ""}}
      </md-card-content>

      <div v-if="parameterValueDefinition">
        <p>파라미터</p>
        <object-form
          :classDefinition="parameterValueDefinition"
          :data="workItem.parameterValues"
        ></object-form>
      </div>

    </md-card-area>

    <md-card-actions>
      <md-button @click.native="complete('COMPLETED')">완료</md-button>
      <md-button @click.native="complete('SAVED')">저장</md-button>
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
        parameterValueDefinition: null,
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

            if(!me.workItem.parameterValues)
              me.workItem.parameterValues = {};

            var parameterValueDefinition = {fieldDescriptors:[]};

            if(me.workItem.activity.parameters) {
              for (var idx in me.workItem.activity.parameters) {
                var parameter = me.workItem.activity.parameters[idx];

                if (parameter.argument && parameter.argument.text && parameter.variable) {

                    if(!parameter.variable.typeClassName) //TODO: it looks hard code
                        parameter.variable.typeClassName = "java.lang.String";

                    if(parameter.multipleInput){

                      parameterValueDefinition.fieldDescriptors.push({
                        name: parameter.argument.text,
                        displayName: parameter.argument.text,
                        collectionClass: parameter.variable.typeClassName,
                      });
                    }else
                    {

                      parameterValueDefinition.fieldDescriptors.push({
                        name: parameter.argument.text,
                        displayName: parameter.argument.text,
                        className: parameter.variable.typeClassName
                      });
                    }

                }
              }

              me.parameterValueDefinition = parameterValueDefinition;
            }
          }
        });

      },
      complete: function (desiredState) {
        var serviceLocator = this.$root.$children[0].$refs["backend"]; //TODO hardcoded

        var me = this;

        serviceLocator.invoke({
          path: this.taskId.replace("worklist", "work-item"),
          method: 'POST',
          data: {
            worklist: {
              status: desiredState // 'COMPLETED' or 'SAVED'
            },
            parameterValues: this.workItem.parameterValues
          },
          success: function (value) {
            //me.workItem = null;
            me.load();

            me.$root.$children[0].success('작업을 완료했습니다.');
            me.$emit('update:reload', true);
          }
        });

      }
    }
  }
</script>


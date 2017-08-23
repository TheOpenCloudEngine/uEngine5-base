<template>

  <div v-if="workItem">

    <v-card>
      <v-card-title primary-title>
        <div>
          <h3 class="headline mb-0">{{workItem.activity.name}}</h3>
          <div>{{workItem.activity.description ? workItem.activity.description.text : ""}}</div>
        </div>
      </v-card-title>
      <v-card-actions>
        <v-btn flat class="orange--text" @click.native="complete">완료</v-btn>
        <v-btn flat class="orange--text">저장</v-btn>
        <v-btn flat class="orange--text">건너뛰기</v-btn>
      </v-card-actions>
    </v-card>

  </div>

</template>



<script>
  export default {
    props: {
      taskId: String,
    },

    watch: {
      taskId: function(val){
          this.load();
      }
    },

    data: function(){

        return {
            workItem: null,
        };

    },

    methods: {
      load: function(){
        var me = this;

        var serviceLocator = this.$parent.$refs["backend"]; //TODO hardcoded

        serviceLocator.invoke({
          path: this.taskId.replace("worklist", "work-item"),
          success: function(value){
              me.workItem = value;
          }
        });

      },
      complete: function() {
        var serviceLocator = this.$parent.$refs["backend"]; //TODO hardcoded

        var me = this;

        serviceLocator.invoke({
          path: this.taskId.replace("worklist", "work-item"),
          method: 'POST',
          data: {
            worklist: {
                status: 'COMPLETED'
            }
          },
          success: function(value){
            me.workItem = null;

            //TODO : shows some message
          }
        });

      }
    }
  }
</script>


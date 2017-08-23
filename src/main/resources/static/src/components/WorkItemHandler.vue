<template>

  <div>

    <md-card>
      <md-card-header>
        <div class="md-title">{{activity.name.text}}</div>
        <div class="md-subhead">{{activity.description.text}}</div>
      </md-card-header>

      <md-card-actions>
        <md-button v-on:click.native="complete">완료</md-button>
        <md-button>저장</md-button>
        <md-button>건너뛰기</md-button>
      </md-card-actions>

      <md-card-content>
        <md-tabs>
          <md-tab id="movies" md-label="입력값">

          </md-tab>

          <md-tab id="music" md-label="출력값">
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Deserunt dolorum quas amet cum vitae, omnis! Illum quas voluptatem, expedita iste, dicta ipsum ea veniam dolore in, quod saepe reiciendis nihil.</p>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Deserunt dolorum quas amet cum vitae, omnis! Illum quas voluptatem, expedita iste, dicta ipsum ea veniam dolore in, quod saepe reiciendis nihil.</p>
          </md-tab>
        </md-tabs>
      </md-card-content>
    </md-card>

  </div>

</template>



<script>
  export default {
    props: {
      taskId: String,
      serviceLocator: Object
    },

    data: function(){

        return {
            definition: null,
          activity: null
        };

    },

    created: function(){
        this.load();
    },

    methods: {
      load: function(){
        var me = this;

        var serviceLocator = this.$root.$children[0].$refs[this.serviceLocator];

        serviceLocator.invoke({
          path: "worklist/" + this.taskId,
          success: function(value){
              var defId = value.defId;
              var tracingTag = value.tracingTag;

              if(defId.indexOf("codi/")==0){
                  defId = defId.substring("codi/".length);
              }

              this.invoke({
                  path: "/definition/" + defId,
                  success: function (definition) {
                    me.definition = definition;

                    for(var idx in definition.definition.childActivities[1]){
                        var thatActivity = definition.definition.childActivities[1];
                        if(thatActivity.tracingTag == tracingTag){
                            me.activity = thatActivity;
                            break;
                        }
                    }


                  }

              })
          }
        });

      },
      complete: function() {

      }
    }
  }
</script>


<template>

  <div>
    <form novalidate @submit.stop.prevent="submit">
      <md-layout md-gutter>
        <md-layout md-column md-gutter>
          <md-input-container>
            <label>프로세스 정의 명</label>
            <md-input v-model="definitionName"></md-input>
          </md-input-container>
        </md-layout>
        <md-layout md-column>
          <div>
          <md-button v-on:click.native="load">열기</md-button>
            <md-button v-on:click.native="save">저장</md-button>
            <md-button v-on:click.native="addActivity">액티비티 추가</md-button>

            <md-button :disabled="!canUndo" v-on:click.native="undo">되돌리기</md-button>
            <md-button :disabled="!canRedo" v-on:click.native="redo">다시하기</md-button>

          <md-button v-on:click.native="initiate">시작</md-button>
          </div>
        </md-layout>
      </md-layout>


      <md-tabs>
        <md-tab id="modeling" md-label="모델링">


          <md-layout md-gutter>
            <md-layout md-flex="70">
              <div style="overflow: scroll; width:500" v-if="definition && definition.childActivities[1]">
                <svg height="1448" version="1.1" width="1704" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" style="overflow: hidden; position: relative; background-color: rgb(247, 247, 247); user-select: none; background-image: url(&quot;/static/image/grid.gif&quot;);" id="OG_5264">

                  <component :is="getSVGComponentName(child)" v-for="child in definition.childActivities[1]" :activity="child" :x="child.x" :y="child.y" v-on:click.native="showProperties(child)"></component>
                </svg>
              </div>
            </md-layout>

            <md-layout md-column md-gutter>
              <component :is="propertyType" :data="properties" :definition="definition"></component>
            </md-layout>
          </md-layout>



        </md-tab>

        <md-tab id="data" md-label="프로세스 변수" v-if="definition">
          <object-grid java="org.uengine.kernel.ProcessVariable" :online="false" :data="definition.processVariableDescriptors" :full-fledged="true">
          </object-grid>
        </md-tab>

      </md-tabs>




    </form>

  </div>

</template>



<script>
  export default {
    props: {
      definition: Object,
      definitionName: String,
      serviceLocator: String
    },

    data: function(){


        return {
          propertyType: null,
          properties: {
              name: {}
          },
          history: [],
          historyIndex: 0,
          undoing: false,
          undoed: false,
        };

    },

    watch:{

      definition:{

          handler: function(after, before){
              if(!this.undoing) {

                  if(this.undoed){ //if undoed just before, clear the history from the current historyIndex
                      this.history.splice(this.historyIndex, this.history.length - this.historyIndex);
                      this.undoed = false;
                  }

                this.history.push(JSON.parse(JSON.stringify(after))); //heavy
                this.historyIndex = this.history.length;
              }else{
                  this.undoing = false;
              }
          },
        deep: true
      }

   },

    computed: {
      canUndo: function() {
        return this.historyIndex > 0
      },
      canRedo: function() {
        return this.history.length - 1 - this.historyIndex > 0
      }
    },

    methods: {
      load: function(){
        var me = this;

        this.$root.$children[0].$refs[this.serviceLocator].invoke({
          path: "definition/" + this.definitionName + ".json",
          success: function(value){
            me.definition = value.definition;
          }
        });

      },
      save: function(){
        console.log(this.definition);


        this.$root.$children[0].$refs[this.serviceLocator].invoke({
          path: "definition/" + this.definitionName + ".json",
          method: 'POST',
          data: {
            definition : this.definition
          }
        });

      },
      initiate: function(){
        console.log(this.definition);


        this.$root.$children[0].$refs[this.serviceLocator].invoke({
          path: "definition/" + this.definitionName + ".json" + "/instance",
          method: 'POST',
          data: {
          }
        });

      },

      getSVGComponentName(activity){
        var typeName = activity._type.toLowerCase().split('.').join('-');

        return "svg-" + typeName;
      },

      showProperties(activity){
          var tracingTag = activity.tracingTag;

          this.definition.childActivities[1].forEach(function(act){
              if(act.tracingTag == tracingTag) {
                activity = act;

                console.log('found: ' + act)

                return false;
              }
          });

          this.propertyType = activity._type.toLowerCase().split('.').join('-');
          this.properties = activity;
          console.log(this.propertyType, this.properties);
      },

      addActivity(){
          this.definition.childActivities[1].push({

              _type:"org.uengine.kernel.HumanActivity",
            elementView:{
                  _type: "org.uengine.kernel.view.HumanActivityView",
                  x: 100,
              y: 100,
              height: 80,
              width: 100
            },
            name: {
                  text: '이름없으'
            },
            tracingTag: 'new_0001'

          })

      },

      undo: function() {
        if (this.canUndo) {
          this.historyIndex -= 1
          this.undoing = true;
          this.undoed = true;
          this.definition = this.history[this.historyIndex];

          this.showProperties(this.properties);
        }
      },
      redo: function() {
        if (this.canRedo) {
          this.historyIndex += 1
          this.undoing = true;
          this.undoed = true;
          this.definition = this.history[this.historyIndex]

          this.showProperties(this.properties);
        }
      }
    }
  }
</script>


<template>

  <div>
    <form novalidate @submit.stop.prevent="submit">
      <md-input-container>
        <label>프로세스 정의 명</label>
        <md-input v-model="definitionName"></md-input>
      </md-input-container>

      <md-button v-on:click.native="load">열기</md-button>
      <md-button v-on:click.native="save">저장</md-button>
      <md-button v-on:click.native="initiate">시작</md-button>
      <md-button class="md-primary md-raised" id="custom" @click.native="this.$refs['processVariablePanel'].open()">프로세스 변수</md-button>


      <br>
      <hr>

      <md-button v-for="child in activities" class="md-raised md-primary" v-on:click.native="showProperties(child)">{{child.name ? child.name.text : 'No-Name'}}</md-button>

      <component :is="propertyType" :data="properties" :definition="definition"></component>

    </form>

    <object-grid java="org.uengine.kernel.ProcessVariable" :online="false" :data="processVariables" :full-fledged="true">
    </object-grid>
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
          activities: [
          ],
          processVariables: [],
          propertyType: null,
          properties: {
              name: {}
          }
        };

    },

    methods: {
      load: function(){
        var me = this;

        this.$root.$children[0].$refs[this.serviceLocator].invoke({
          path: "definition/" + this.definitionName + ".json",
          success: function(value){
            me.definition = value.definition;
            me.activities = me.definition.childActivities[1];

            if(!me.definition.processVariableDescriptors)
              me.definition.processVariableDescriptors = [];

            me.processVariables = me.definition.processVariableDescriptors;
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

      showProperties(activity){
          this.propertyType = activity._type.toLowerCase().split('.').join('-');
          this.properties = activity;
      }
    }
  }
</script>


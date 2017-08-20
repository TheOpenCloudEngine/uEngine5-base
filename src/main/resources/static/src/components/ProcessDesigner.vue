<template>

  <form novalidate @submit.stop.prevent="submit">
    <md-input-container>
      <label>프로세스 정의 명</label>
      <md-input v-model="definitionName"></md-input>
    </md-input-container>

    <md-button v-on:click.native="load">열기</md-button>
    <md-button v-on:click.native="save">저장</md-button>

    <br>
    <hr>

    <md-button v-for="child in activities" class="md-raised md-primary" v-on:click.native="showProperties(child)">{{child.name ? child.name.text : 'No-Name'}}</md-button>

    <component :is="propertyType" :data="properties"></component>

  </form>

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
          }
        });

      },
      save: function(){
        this.$root.$children[0].$refs[this.serviceLocator].invoke({
          path: "definition/" + this.definitionName + ".json",
          method: 'POST',
          data: {
            definition : this.definition
          }
        });

//        console.log(this.definition);

      },

        showProperties(activity){
            this.propertyType = activity._type.toLowerCase().split('.').join('-');
            this.properties = activity;
        }
    }
  }
</script>


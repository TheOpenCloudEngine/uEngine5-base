<template>

  <div>
      <div v-for="parameterContext in parameterContexts">

       aaaa

      </div>

      <md-button v-on:click.native="add">매핑 추가</md-button>
  </div>
</template>

<script>

  export default {
    name: "bpmn-parameter-contexts",

    computed: {
        calleeDefinition: function(){

            //TODO: load the definition by AJAX
            return {

                processVariableDescriptors: [
                  {name: 'a'},
                  {name: 'b'}
                ]

            }
        }
    },

    props: {

        parameterContexts: Array,
        definition: Object,
        calleeDefinitionId: String

    },

    methods: {

        add: function(){

            if(!this.parameterContexts)
                this.parameterContexts = [];

            this.parameterContexts.push({
                direction: 'IN-OUT',
                variable: {
                    name: 'name'
                },
                argument: {
                      text: 'arg'
                }
            })
        },

      remove: function(parameterContext){

            //TODO: find and remove
            //this.parameterContexts.splice()
      }
    }
  }

</script>



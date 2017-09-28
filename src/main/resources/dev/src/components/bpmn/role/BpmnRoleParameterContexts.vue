
<template>

  <div>
    <div v-if="parameterContexts">

      <div v-for="parameterContext in parameterContexts">

        <md-select name="input" id="input" multiple v-model="parameterContext.variable.name">
          <md-option v-for="variable in definition.processVariableDescriptors"
                     :key="variable.name"
                     :value="variable.name">
            {{ variable.name }}
          </md-option>
        </md-select>
        <md-select v-model="parameterContext.direction">
          <md-option value="IN-OUT"></md-option>
          <md-option value="IN"></md-option>
          <md-option value="OUT"></md-option>
        </md-select>

        <md-button v-on:click.native="remove(parameterContext)"></md-button>

        <md-checkbox v-model="parameterContext.multipleInput"></md-checkbox>

      </div>
    </div>

    <md-button v-on:click.native="add">매핑 추가</md-button>
  </div>
</template>

<script>

  export default {
    name: "bpmn-role-parameter-contexts",

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



<template>

  <form novalidate @submit.stop.prevent="submit">
    <md-input-container>
      <label>액티비티 ID</label>
      <md-input v-model="data.tracingTag"></md-input>
    </md-input-container>
    <md-input-container>
      <label>액티비티 명</label>
      <md-input v-model="data.name.text"></md-input>
    </md-input-container>
    <md-input-container>
      <label>입력값</label>

      <md-select name="input" id="input" multiple v-model="parametersIn">
        <md-option v-for="variable in definition.processVariableDescriptors"
                   :key="variable.name"
                   :value="variable.name">
          {{ variable.name }}
        </md-option>
      </md-select>
    </md-input-container>

    <md-button class="md-raised md-primary" v-on:click.native="move(-10,0)"><i class="material-icons">keyboard_arrow_left</i></md-button>
    <md-button class="md-raised md-primary" v-on:click.native="move(10,0)"><i class="material-icons">keyboard_arrow_right</i></md-button>
    <md-button class="md-raised md-primary" v-on:click.native="move(0,-10)"><i class="material-icons">keyboard_arrow_up</i></md-button>
    <md-button class="md-raised md-primary" v-on:click.native="move(0,10)"><i class="material-icons">keyboard_arrow_down</i></md-button>
  </form>

</template>


<script>
  export default {
    props: {
      data: Object,
      definition: Object
    },

    data: function(){

        var parametersIn = [];

        if(this.data.parameters){
            for(var i in this.data.parameters){
              var parameter = this.data.parameters[i];

              parametersIn.push(parameter.variable.name);
            }
        }

        return {
          parametersIn: parametersIn,
          parametersOut: []
        }
    },

    watch: {

      parametersIn: {

        handler: function(after, before){

            this.updateParameters();
        },
        deep: true
      },

      parametersOut : function(val){

          this.updateParameters();

        }
    },

    created: function(){
      if(!data.name) data.name = {text: ''};
      if(!data.description) data.description = {text: ''};

    },

    methods: {

        updateParameters : function(){

            var parameters = [];
            for(var i in this.parametersIn){

                var variableName = this.parametersIn[i];

                parameters.push({

                  argument: {
                      text: variableName
                  },
                  variable: {
                      name: variableName
                  }

                });

            }

            this.data.parameters = parameters;

        },

      move: function(x, y){

        this.data.elementView.x += x;
        this.data.elementView.y += y;

      }


    }
  }
</script>

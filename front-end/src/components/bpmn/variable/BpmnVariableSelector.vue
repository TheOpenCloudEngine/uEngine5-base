<template>
  <div style="width: 100%">
    <md-input-container style="width: 25%; float:left; margin-right: 20px;">
      <label>Process Data</label>
      <md-select name="input" id="input" v-model="value.name" @change="onChanged">
        <md-option v-for="variable in definition.processVariableDescriptors"
                   :key="variable.name"
                   :value="variable.name">
          {{ variable.name }}
        </md-option>

        <md-subheader>Roles</md-subheader>

        <md-option v-for="role in definition.roles"
                   :key="role.name"
                   :value="'[roles].'+role.name">
          {{ role.name }}
        </md-option>
      </md-select>
    </md-input-container>

    <md-input-container style="width: 25%; float:left;" v-if="keys.length > 0">
      <label>Property</label>
      <md-select>
        <md-option v-for="key in keys"
                   :value="key">
          {{ key }}
        </md-option>
      </md-select>
    </md-input-container>
  </div>
</template>

<script>

  export default {
    name: "bpmn-variable-selector",

    created: function(){
      if(!this.value)
        this.value = {name:''}
      if(this.value != null) {
        this.metaDataResolver();
      }
    },

    props: {
      value: Object,
      definition: Object
    },
    data: function () {
      return {
        keys: []
      }
    },

    watch: {
        'value': {
          function() {
            console.log("hihi")
          },
          handler: function(){
            this.$emit('input', this.value);
          },
          deep: true
        }

    },
    methods: {

      onChanged(){
          this.$emit('input', this.value);
      },

      metaDataResolver() {
        console.log("meta!")
        var me = this;

        for(var i = 0; i < me.definition.processVariableDescriptors.length; i++) {
          var processVariable = me.definition.processVariableDescriptors[i]
          if(processVariable.name == me.value.name && processVariable.typeClassName.indexOf("#")) {
            var definitionAndClassName = processVariable.typeClassName.split("#");
            var definitionName = definitionAndClassName[0];
            var classNameOnly = definitionAndClassName[1];


              var result;
              var uri = (encodeURI(window.backend.$bind.ref + "/definition/raw/" + definitionName + ".ClassDiagram.json"))

              console.log("try to get class diagram: " + uri);

              var xhr = new XMLHttpRequest();

              xhr.open('GET', uri, false);
              xhr.setRequestHeader("access_token", localStorage['access_token']);
              xhr.onload = function () {
                result = JSON.parse(xhr.responseText)
              };
              xhr.send();

              for(var y = 0; y < result.definition.classDefinitions[1].length; y++) {
                if(classNameOnly == result.definition.classDefinitions[1][y].name) {
                  for(var z = 0; z < result.definition.classDefinitions[1][y].fieldDescriptors.length; z++) {
                    me.keys.push(result.definition.classDefinitions[1][y].fieldDescriptors[z].name);
                  }
                }
              }
          }
        }
      }

    }
  }

</script>



<template>
  <div>

      <div v-for="parameterContext in parameterContexts" style="height: 80px;">
        <div style="width: 15%; float: left;">
          <md-input-container v-if="forSubProcess">
            <label>피호출측 변수</label>
            <md-select v-if="calleeDefinition" name="input" id="input" v-model="parameterContext.argument.text">
              <md-option v-for="variable in calleeDefinition.processVariableDescriptors"
                         :key="variable.name"
                         :value="variable.name">
                {{ variable.name }}
              </md-option>
            </md-select>
            <md-input v-else name="input" id="input" v-model="parameterContext.argument.text">
            </md-input>
          </md-input-container>
          <md-input-container v-else>
            <label>{{labelForArgument ? labelForArgument : '아규먼트'}}</label>
            <md-input name="input" id="input" v-model="parameterContext.argument.text">
            </md-input>
          </md-input-container>
        </div>
        <div style="width: 15%; float: left;">
          <md-input-container v-if="parameterContext.transformerMapping">
            <label>변환</label>
            <md-select v-model="parameterContext.transformerMapping.transformer" style="min-width: 20px;">
              <md-subheader>형태소 추출</md-subheader>
              <md-option :value="{_type:'org.uengine.five.kernel.SemanticTransformer', entityType: 'SN'}">숫자 </md-option>
              <md-option :value="{_type:'org.uengine.five.kernel.SemanticTransformer', entityType: 'NNP'}">이름 </md-option>
              <md-option :value="{_type:'org.uengine.five.kernel.SemanticTransformer', entityType: 'NNG'}">직업 </md-option>
            </md-select>
          </md-input-container>

          <md-input-container>
            <label>연결방향</label>
            <md-select v-model="parameterContext.direction" style="min-width: 20px;" @change="directionChanged(parameterContext)">

              <md-button class="md-icon-button" md-menu-trigger slot="icon">
                <md-icon>{{iconForDirection(parameterContext.direction)}}</md-icon>
              </md-button>

              <md-option value="IN-OUT"><md-icon>{{iconForDirection("IN-OUT")}}</md-icon></md-option>
              <md-option value="IN"><md-icon>{{iconForDirection("IN")}}</md-icon></md-option>
              <md-option value="OUT"><md-icon>{{iconForDirection("OUT")}}</md-icon></md-option>
              <md-option value="OUT "><md-icon>all_inclusive</md-icon><md-icon>{{iconForDirection("OUT")}}</md-icon></md-option>
            </md-select>

          </md-input-container>
        </div>
        <div>
            <bpmn-variable-selector v-model="parameterContext.variable" :definition="definition"></bpmn-variable-selector>
        </div>


        <md-layout style="width: 10%; float: left;">
          <md-checkbox v-model="parameterContext.split" v-if="forSubProcess">Split</md-checkbox>
          <md-checkbox v-model="parameterContext.multipleInput" v-if="multi">Multi</md-checkbox>
        </md-layout>

        <div style="width: 10%; float: left; margin-top: 20px; margin-left: 10px;">
          <md-icon v-on:click.native="remove(parameterContext)"
                   class="md-primary"
                   style="cursor: pointer"
          >delete
          </md-icon>
        </div>
      </div>


    <md-button v-on:click.native="add">매핑 추가</md-button>
  </div>
</template>

<script>

  export default {
    name: "bpmn-parameter-contexts",

//    computed: {
//      calleeDefinition: function () {
//
//        this.$root.codi('definition/' + this.calleeDefinitionId).get()
//          .then(function (response) {
//            me.calleeDefinition = response.data;
//          })
//
//        return {
//
//          processVariableDescriptors: [
//            {name: 'a'},
//            {name: 'b'}
//          ]
//
//        }
//      }
//    },

    props: {
      parameterContexts: Array,
      definition: Object,
      calleeDefinitionId: String,
      forSubProcess: Boolean,
      forCallActivity: Boolean,
      multi: Boolean,
      labelForArgument: String
    },
    data: function () {

      return {
        data: {
          parameterContexts: this.parameterContexts,

        },

        calleeDefinition: null
//        calleeDefinition: {
//          processVariableDescriptors: [
//            {name: '-- not loaded -- '},
//          ]
//
//        }
      };
    },
    watch: {
      data: {
        handler: function (after, before) {
          this.$emit('update:parameterContexts', after);
        },
        deep: true
      },

      calleeDefinitionId  : function(val){
        this.refreshCalleeDefinition();
      }
    },
    methods: {

        iconForDirection: function(direction){

            if(direction == "IN")
                return "arrow_back";
            else if(direction == "OUT" || direction == "OUT ")
                return "arrow_forward";
            else
                return "settings_ethernet";

        },

      refreshCalleeDefinition: function(){

          if(!this.forSubProcess) return;

        if(this.forCallActivity){
          var me = this;
          this.$root.codi('definition/' + this.calleeDefinitionId + ".json").get()
            .then(function (response) {
              me.calleeDefinition = response.data.definition;
            })
         }else{

            me.calleeDefinition = this.definition;
         }

      },

      add: function () {
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

      remove: function (parameterContext) {

          var index = this.parameterContexts.indexOf(parameterContext);

        //TODO: find and remove
        this.parameterContexts.splice(index,1)
      },

      directionChanged: function(parameterContext){
          if(parameterContext.direction == "OUT "){
            parameterContext.direction = "OUT";

            parameterContext.transformerMapping = {
              _type: 'org.uengine.processdesigner.mapper.TransformerMapping',
              transformer: {
                _type: "org.uengine.five.kernel.SemanticTransformer",
                entityType: "SSP"
              }
            }

          }else{
            parameterContext.transformerMapping = null;
          }

      }
    }
  }

</script>



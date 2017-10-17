<template>
  <div>

    <md-layout v-for="parameterContext in parameterContexts">
      <md-layout md-flex="30">
        <md-input-container v-if="forSubProcess">
          <label>피호출측 변수</label>
          <md-select name="input" id="input" v-model="parameterContext.argument.text">
            <md-option v-for="variable in calleeDefinition.processVariableDescriptors"
                       :key="variable.name"
                       :value="variable.name">
              {{ variable.name }}
            </md-option>
          </md-select>
        </md-input-container>
        <md-input-container v-else>
          <label>아규먼트</label>
          <md-input name="input" id="input" v-model="parameterContext.argument.text">
          </md-input>
        </md-input-container>
      </md-layout>
      <md-layout md-flex="30">
        <md-input-container>
          <label>연결 변수</label>
          <md-select name="input" id="input" v-model="parameterContext.variable.name">
            <md-option v-for="variable in definition.processVariableDescriptors"
                       :key="variable.name"
                       :value="variable.name">
              {{ variable.name }}
            </md-option>
          </md-select>
        </md-input-container>
      </md-layout>
      <md-layout md-flex="30">
        <md-input-container>
          <label>연결 변수 방향</label>
          <md-select v-model="parameterContext.direction">
            <md-option value="IN-OUT">IN-OUT</md-option>
            <md-option value="IN">IN</md-option>
            <md-option value="OUT">OUT</md-option>
          </md-select>
        </md-input-container>
      </md-layout>


      <md-layout md-flex="20">
        <md-checkbox v-model="parameterContext.split" v-if="forSubProcess">Split</md-checkbox>
        <md-checkbox v-model="parameterContext.multipleInput" v-else>Multi</md-checkbox>
      </md-layout>

      <md-layout md-flex="20">
        <md-icon v-on:click.native="remove(parameterContext)"
                 class="md-primary"
                 style="cursor: pointer"
        >delete
        </md-icon>
      </md-layout>
    </md-layout>


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
      forSubProcess: Boolean
    },
    data: function () {

      return {
        data: {
          parameterContexts: this.parameterContexts,

        },

        calleeDefinition: {
          processVariableDescriptors: [
            {name: '-- not loaded -- '},
          ]

        }
      };
    },
    watch: {
      data: {
        handler: function (after, before) {
          this.$emit('update:parameterContexts', after);
        },
        deep: true
      },

      calleeDefinitionId: function (val) {
        this.refreshCalleeDefinition();
      }
    },
    methods: {

      refreshCalleeDefinition: function () {

        if (!this.forSubProcess) return;

        var me = this;
        this.$root.codi('definition/' + this.calleeDefinitionId + ".json").get()
          .then(function (response) {
            me.calleeDefinition = response.data.definition;
          })

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

        //TODO: find and remove
        //this.parameterContexts.splice()
      }
    }
  }

</script>



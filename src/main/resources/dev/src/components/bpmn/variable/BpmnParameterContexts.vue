<template>
  <div>
    <md-layout v-for="parameterContext in data.parameterContexts">
      <md-layout md-flex="30">
        <md-input-container>
          <label>연결 변수 이름</label>
          <md-select name="input" id="input" multiple v-model="parameterContext.variable.name">
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
        <md-icon v-on:click.native="remove(parameterContext)"
                 class="md-primary"
                 style="cursor: pointer"
        >delete
        </md-icon>
      </md-layout>
      <md-layout md-flex="20">
        <md-checkbox v-model="parameterContext.multipleInput">Multi</md-checkbox>
      </md-layout>
    </md-layout>

    <md-button v-on:click.native="add">매핑 추가</md-button>
  </div>
</template>

<script>

  export default {
    name: "bpmn-parameter-contexts",

    computed: {
      calleeDefinition: function () {

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
    data: function () {
      return {
        data: {
          parameterContexts: this.parameterContexts
        }
      };
    },
    watch: {
      data: {
        handler: function (after, before) {
          this.$emit('update:parameterContexts', after);
        },
        deep: true
      }
    },
    methods: {
      add: function () {
        this.data.parameterContexts.push({
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



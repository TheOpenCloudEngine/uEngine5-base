<template>
  <div>

    <div v-if="calleeDefinitionId">

      <md-layout v-for="parameterContext in data.parameterContexts">
        <md-layout md-flex="30">
          <md-input-container>
            <label>피호출측 역할</label>
            <md-select name="input" id="input" v-model="parameterContext.argument">
              <md-option v-for="role in calleeDefinition.roles"
                         :key="role.name"
                         :value="role.name">
                {{ role.name }}
              </md-option>
            </md-select>
          </md-input-container>
        </md-layout>
        <md-layout md-flex="30">
          <md-input-container>
            <label>연결 역할</label>
            <md-select name="input" id="input" v-model="parameterContext.role.name">
              <md-option v-for="role in definition.roles"
                         :key="role.name"
                         :value="role.name">
                {{ role.name }}
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
          <md-checkbox v-model="parameterContext.multipleInput">Multi</md-checkbox>
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
  </div>
</template>

<script>

  export default {
    name: "bpmn-role-parameter-contexts",

    props: {
      parameterContexts: Array,
      definition: Object,
      calleeDefinitionId: String
    },
    data: function () {

      return {
        data: {
          parameterContexts: this.parameterContexts,

        },

        calleeDefinition: {
          roles: [
            {name: '-- not loaded -- '},
          ]

        }
      };
    },
    watch: {
      calleeDefinitionId: function () {
        console.log('calleeDefinitionId changed!!');
      },
      data: {
        handler: function (after, before) {
          this.$emit('update:parameterContexts', after);
        },
        deep: true
      },

      calleeDefinitionId: function (val) {

        console.log("========>" + val)
        this.refreshCalleeDefinition();


      }
    },
    methods: {

      refreshCalleeDefinition: function () {
        var me = this;
        this.$root.codi('definition/' + this.calleeDefinitionId + ".json").get()
          .then(function (response) {
            me.calleeDefinition = response.data.definition;
          })

      },

      add: function () {
        this.data.parameterContexts.push({
          direction: 'IN-OUT',
          role: {
            name: ''
          },
          argument: ''  //TODO: object path differ from ParameterContext

        })
      },

      remove: function (parameterContext) {

        //TODO: find and remove
        //this.parameterContexts.splice()
      }
    }
  }

</script>



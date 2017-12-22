<template>

  <div>

    {{myConditionType}} (
    <ul>
      <div v-for="childCondition in data.conditionsVt">

        <component :is="childCondition._type.split('.').join('-')" :data="childCondition"></component>

      </div>
      
      <md-layout md-gutter style="clear: both;">
        <md-layout>
          <md-input-container>
            <md-select v-model="conditionType" md-elevation="2">
              <md-option value="org.uengine.kernel.And">And</md-option>
              <md-option value="org.uengine.kernel.Or">Or</md-option>
              <md-option value="org.uengine.kernel.Evaluate">Evaluate</md-option>
            </md-select>
          </md-input-container>
        </md-layout>
        <md-layout>
          <md-button @click="addCondition" class="md-raised md-primary" style="margin-top: 15px; margin-left: 12px; height: 30px;">Add</md-button>
        </md-layout>
      </md-layout>
      
    </ul>
    )
  </div>


</template>

<script>

  export default {
      name: 'org-uengine-kernel-Or',

      data: function(){

          return {
              conditionType: 'org.uengine.kernel.Or',
            myConditionType:'Or'
          };
      },

      props: {
          data: Object
      },

    methods: {
      addCondition: function(){

        if(!this.data.conditionsVt){
          this.data = {
            _type: 'org.uengine.kernel.Or',
            conditionsVt: []
          }
        }

        this.data.conditionsVt.push({
          _type: this.conditionType
        })

      }

    }

  }

</script>

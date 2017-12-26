<template>

  <div>

    {{myConditionType}} (
    <ul>
      <div v-for="childCondition in data.conditionsVt">

        <component :is="childCondition._type.split('.').join('-')" :data="childCondition"></component>

      </div>

      <md-button @click="addCondition">Add</md-button>
      <md-select v-model="conditionType">
        <md-option value="org.uengine.kernel.And">And</md-option>
        <md-option value="org.uengine.kernel.Or">Or</md-option>
        <md-option value="org.uengine.kernel.Evaluate">Evaluate</md-option>

      </md-select>

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

//        var temp = this.data;
//        this.data = null;
//        this.data = temp;

        this.$emit('update:data');

      }

    }

  }

</script>

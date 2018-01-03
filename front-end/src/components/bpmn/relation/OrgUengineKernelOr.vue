<template>

  <div>
    <md-card @drop.native="drop()"
              draggable="true"
              @click.native="click"
      style="box-shadow: 0 0 0 rgba(0, 0, 0, 0.2), 0 0 0 rgba(0, 0, 0, 0.14), 0 0 0 0 rgba(0, 0, 0, 0.12)">
      <div>
        {{myConditionType}} (
      <ul>
        <div v-for="(childCondition, index) in value.conditionsVt">

          <component :is="childCondition._type.split('.').join('-')" v-model="value.conditionsVt[index]" :definition="definition"></component>
          <md-button @click="remove(index)" style="min-width: 1px; padding: 0px;  padding-top: 10px;">
            <md-icon>clear</md-icon>
          </md-button>


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
    </md-card>
  </div>


</template>

<script>

  export default {
      name: 'org-uengine-kernel-Or',
      props: {
        definition: Object,
        value: Object
      },
      data: function(){
          return {
            conditionType: 'org.uengine.kernel.Or',
            myConditionType:'Or',
            thisConditionType: ''
          };
      },
    methods: {
      addCondition: function(){

        if(!this.value.conditionsVt){
          this.value = {
            _type: 'org.uengine.kernel.Or',
            conditionsVt: []
          }
        }

        this.value.conditionsVt.push({
          _type: this.conditionType,

        })

        var temp = this.value;
        this.value = null;
        this.value = temp;

        this.$emit('input', this.value);

      },

      remove: function(index) {
        this.value.conditionsVt.splice(index, 1)
        this.$emit('input', this.value);
      }

    }

  }

</script>



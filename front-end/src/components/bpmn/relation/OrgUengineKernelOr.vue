<template>

  <div>

      <div>
        <div>
        <md-input-container style="width: 11%">
          <md-select v-model="value._type" md-elevation="2" style="min-width: 1px">
            <md-option value="org.uengine.kernel.And">And</md-option>
            <md-option value="org.uengine.kernel.Or">Or</md-option>
          </md-select> (
        </md-input-container>



        </div>
      <ul>

          <table width="100%" v-for="(childCondition, index) in value.conditionsVt">
            <td width="90%">
              <component :is="childCondition._type.split('.').join('-')" v-model="value.conditionsVt[index]" :definition="definition"></component>
            </td>
            <td width="10%">
              <md-button @click="remove(index)" style="margin-top: 15px; height: 30px; position: absolute; align: right;">
                <md-icon>clear</md-icon>
              </md-button>
            </td>
          </table>
          <div>
            <div style="float: left; ">
              <md-input-container>
                <md-select v-model="conditionType" md-elevation="2">
                  <md-option value="org.uengine.kernel.And">And</md-option>
                  <md-option value="org.uengine.kernel.Or">Or</md-option>
                  <md-option value="org.uengine.kernel.Evaluate">Evaluate</md-option>
                  <md-option value="org.uengine.kernel.LoopCount">LoopCount</md-option>
                </md-select>
              </md-input-container>
            </div>
            <div>
              <md-button @click="addCondition" class="md-raised md-primary" style="margin-top: 15px; margin-left: 12px; height: 30px;">Add</md-button>
            </div>
          </div>
      </ul>
        <div style="clear: both">
          )
        </div>
    </div>

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
          };
      },
      watch: {
        'value': {
          deep: true,
          handler: function(){
            this.$emit('input', this.value);
          }
        }
      },
    methods: {
      addCondition: function(){

        if(!this.value.conditionsVt){
          this.value = {
            _type: 'org.uengine.kernel.Or',
            conditionsVt: []
          }
        }
        
        var pushCondition = {
          _type: this.conditionType  
        };
        if (this.conditionType == "org.uengine.kernel.LoopCount") {
          pushCondition._type = "org.uengine.kernel.Evaluate";
          pushCondition.type = "loopCount";
        }

        this.value.conditionsVt.push(pushCondition);

        var temp = this.value;
        this.value = null;
        this.value = temp;

        this.$emit('input', this.value);

      },
      click: function () {
        console.log(this.conditionTypeAndOr);
        console.log(this.value.conditionsVt);
      },
      remove: function(index) {
        this.value.conditionsVt.splice(index, 1)

        var temp = this.value;
        this.value = null;
        this.value = temp;

        this.$emit('input', this.value);
      }

    }

  }

</script>



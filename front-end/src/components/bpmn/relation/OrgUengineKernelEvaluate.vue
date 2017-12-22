<template>

  <div>
    <div id="processVal">
      <md-input-container>
        <label>프로세스 변수</label>
        <md-select id="input" v-model="data.pv.name">
          <md-option v-for="variable in definition.processVariableDescriptors"
                     :key="variable.name"
                     :value="variable.name">
            {{ variable.name }}
          </md-option>
        </md-select>
      </md-input-container>
    </div>
    <div id="dataCon">
      <md-input-container>
        <label>조건 연산자</label>
        <md-select type="text"
                   v-model="data.condition"
                   style="min-width: 20px;">
          <md-option value="==">=</md-option>
          <md-option value=">">&gt;</md-option>
          <md-option value="<">&lt;</md-option>
        </md-select>
      </md-input-container>
  </div>
  <div id="dataVal">
      <md-input-container>
        <label>비교 값</label>
        <md-input v-model="data.val"></md-input>
      </md-input-container>
  </div>
  </div>

</template>

<script>

  export default {
      name: 'org-uengine-kernel-Evaluate',

      props: {
          definition: Object,
          data: Object,
      },

    created: function(){
          if(this.data)
              this.data
            =
            {
                _type: 'org.uengine.kernel.Evaluate',
                pv: {

                },
                condition: '==',
                val: ''
            };

          if(!this.data.pv) this.data.pv = {};

          if(!this.definition){
              var conditionEditor;
              var parent;
              parent = this.$parent;
              while(parent.$vnode.tag.indexOf('bpmn-relation') == -1) parent = parent.$parent;
              this.definition = parent.definition;

          }

    }

  }

</script>
<style>
  div#processVal {
    float: left;
    width: 35%;
  } 
  div#dataCon {
    float: left;
    width: 20%;
    margin-left: 13px;
    margin-right: 13px;
  }
  div#dataVal {
    float: left;
    width: 35%;
  }
</style>
<template>

  <div>
    <md-input-container>
      <label>프로세스 변수</label>

      <md-select name="input" id="input" v-model="relation.condition.pv.name">
        <md-option v-for="variable in definition.processVariableDescriptors"
                   :key="variable.name"
                   :value="variable.name">
          {{ variable.name }}
        </md-option>
      </md-select>

    </md-input-container>
    <md-input-container>
      <label>조건 연산자</label>
      <md-select type="text"
                 v-model="relation.condition.condition" v-if="!otherwise">
        <md-option value="==">=</md-option>
        <md-option value=">">&gt;</md-option>
        <md-option value="<">&lt;</md-option>
      </md-select>
    </md-input-container>
    <md-input-container>
      <label>비교 값</label>
      <md-input v-model="relation.condition.val"></md-input>
    </md-input-container>


  </div>


</template>

<script>

  export default {
      name: 'org-uengine-kernel-Evaluate',

      props: {

          data: Object
      }

  }

</script>

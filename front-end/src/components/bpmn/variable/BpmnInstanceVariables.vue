<template>
  <div>
    <md-dialog
      md-open-from="#instanceVariables" md-close-to="#instanceVariables" ref="instanceVariables">
      <md-dialog-title>Instance Variables</md-dialog-title>
      <md-dialog-content>
        <md-table-card>
          <md-table>
            <md-table-header>
              <md-table-row>
                <md-table-head>Name</md-table-head>
                <md-table-head>Display Name</md-table-head>
                <md-table-head>Default Value In String</md-table-head>
                <md-table-head>변수 유형</md-table-head>
              </md-table-row>
            </md-table-header>
            <md-table-body>
              <md-table-row v-for="(item, idx) in definition.processVariableDescriptors">
                <md-table-cell>{{item.name}}</md-table-cell>
                <md-table-cell>{{item.displayName.text}}</md-table-cell>
                <md-table-cell v-if="processVariables[idx].variables != ''">{{processVariables[idx].variables}}</md-table-cell>
                <md-table-cell v-else>{{item.defaultValueInString}}</md-table-cell>
                <md-table-cell>{{item.typeClassName}}</md-table-cell>
              </md-table-row>
            </md-table-body>
          </md-table>
        </md-table-card>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="md-primary" @click="$refs['instanceVariables'].close();">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
  </div>
</template>

<script>

  export default {
    name: "bpmn-instance-variables",

    props: {
      id: String,
      definition: Object
    },
    data() {
      return {
        processVariables: [],
      };
    },
    methods: {
      closeInstanceVariables(ref) {
        this.$refs['instanceVariables'].close();
      },
      openInstanceVariables(ref) {
        var me = this;
        me.instanceVariables(me.definition.processVariableDescriptors);
        this.$refs['instanceVariables'].open();
      },
      //인스턴스 변수를 불러온다.
      instanceVariables: function(processVariableDescriptors) {
        var me = this;
        var variables = [];
        for(var idx in processVariableDescriptors) {
          //하이바인드 적용시 데이터가 내려오지 않는 문제로 인해
          //기존의 방법을 사용하여 데이터를 받아옴
          me.$root.codi('instance{/id}/variable{/variable}/').get({id: me.id, variable: processVariableDescriptors[idx].name})
            .then(function (response) {
              variables.push({ variables: response.data });
              me.processVariables = variables;
            })
        }
      },
    }
  }

</script>



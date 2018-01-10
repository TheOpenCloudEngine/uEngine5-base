<template>
  <div>
    <md-dialog
      md-open-from="#instanceVariables" md-close-to="#instanceVariables" ref="instanceVariables" @change="getInstanceVariables">
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
              <md-table-row v-for="(item, idx) in instanceVariables" @dblclick.native="onDoubleClick(item, idx)">
                <md-table-cell>{{item.name}}</md-table-cell>
                <md-table-cell>{{item.displayName.text}}</md-table-cell>
                <md-table-cell>{{item.defaultValueInString}}</md-table-cell>
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

    <md-dialog md-open-from="#fab" md-close-to="#fab" ref="variableChange">
      <md-dialog-title>Variable Change</md-dialog-title>
      <md-dialog-content>
        <md-input-container>
          <label>Default Value In String</label>
          <md-input v-model="selected.data" type="text"></md-input>
        </md-input-container>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="md-primary" @click.native="changeVariable(); $refs['variableChange'].close()">Change</md-button>
        <md-button class="md-primary" @click.native="$refs['variableChange'].close()">Close</md-button>
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
        instanceVariables: [],
        selected: {
            name: null,
            data: null
        },
      };
    },
    mounted() {
      var me = this;
      me.getInstanceVariables();
    },
    methods: {
      openInstanceVariables(ref) {
        this.$refs['instanceVariables'].open();
      },
      //인스턴스 변수를 불러온다.
      getInstanceVariables: function() {
        var me = this;

        me.instanceVariables = me.definition.processVariableDescriptors;
        for(var i in me.instanceVariables) {
          //아래의 API에서 hateoas를 지원하지 않아 하이바인드 적용이 불가하여
          //기존의 방법을 사용하여 데이터를 받아옴
          me.$root.codi('instance{/id}/variable{/variable}/').get({id: me.id, variable: me.instanceVariables[i].name})
            .then(function (response) {
              me.replaceVariable(response.request.params.variable, response.data);
            })
        }

        this.$emit('input', this.instanceVariables);
      },
      replaceVariable: function(variableName, instanceVariable) {
        var me = this;
          for(var y in me.instanceVariables) {
            var variableName2 = me.instanceVariables[y].name

            if(variableName2 == variableName) {
              me.instanceVariables[y].defaultValueInString = instanceVariable;
            }
          }
        var temp = me.instanceVariables;
        me.instanceVariables = null;
        me.instanceVariables = temp;
      },
      //선택된 항목을 selected 변수에 담은 후에 모달창을 오픈한다.
      onDoubleClick: function (item, idx) {
        var me = this;
        me.selected.data = item.defaultValueInString;
        me.selected.name = item.name;
        me.selected.idx = idx;
        this.$refs['variableChange'].open();
      },
      //변수 변경을 눌렀을 시
      changeVariable: function () {
        var me = this;
        var instance = {};
        var url = "instance/" + me.id + "/variable/" + me.selected.name + "?varValue=" + me.selected.data;
        //아래의 API에서 hateoas를 지원하지 않아 하이바인드 적용이 불가하여
        //기존의 방법을 사용하여 데이터를 받아옴
        me.$root.codi(url).save({})
          .then(function (response) {
              me.$root.$children[0].success('변경되었습니다.');
              //qusrud
              me.instanceVariables[me.selected.idx].defaultValueInString = me.selected.data;
            },
            function (response) {
              me.$root.$children[0].error('변경할 수 없습니다.');
            })
      },
    }
  }

</script>



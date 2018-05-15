<template>

  <div>
    <md-card draggable="true"
            @dragstart.native="dragover"
            @dragleave.native="onDragLeave"
            @dragenter.native="onDragenter"
            _drag="drag"
             style="box-shadow: 0 0 0 rgba(0, 0, 0, 0.2), 0 0 0 rgba(0, 0, 0, 0.14), 0 0 0 0 rgba(0, 0, 0, 0.12)">
        <div>
          <div id="processVal" v-if="value.type == 'loopCount' || value._type == 'org.uengine.five.kernel.LoopCountEvaluate'">
            <md-input-container v-if="definition && definition.childActivities"  style="min-width: 1px;">
              <label>{{ $t("loopCountOf") }}</label>
              <md-select id="input" v-model="value.key"  style="min-width: 1px;">
                <md-option v-for="activity in definition.childActivities[1]"
                           v-if="activity && activity.name && activity.name.text"
                           :key="activity.tracingTag"
                           :value="activity.tracingTag">
                  {{ activity.name.text }}
                </md-option>
              </md-select>
            </md-input-container>
          </div>
          <div id="processVal" v-else>
            <md-input-container v-if="definition && definition.processVariableDescriptors"  style="min-width: 1px;">
              <label>{{ $t("variable") }}</label>
              <md-select id="input" v-model="value.pv.name"  style="min-width: 1px;">
                <md-option v-for="variable in definition.processVariableDescriptors"
                           :key="variable.name"
                           :value="variable.name">
                  {{ variable.name }}
                </md-option>
              </md-select>
            </md-input-container>
          </div>
          <div id="dataCon">
            <md-input-container  style="min-width: 1px;">
              <label>Comparator</label>
              <md-select type="text"
                         v-model="value.condition"
                         style="min-width: 1px;"

                  @change="conditionChanged"
              >
                <md-option value="==">=</md-option>
                <md-option value=">">&gt;</md-option>
                <md-option value="<">&lt;</md-option>
                <md-option value="means">means</md-option>
                <md-option value="loopCount">loop count</md-option>
              </md-select>
            </md-input-container>
          </div>
          <div id="dataVal">
              <md-input-container  style="min-width: 1px;">
                <label>Value</label>
                <md-input v-model="value.val"  style="min-width: 1px;"></md-input>
              </md-input-container>
          </div>
        </div>
    </md-card>
  </div>

</template>

<script>

  export default {
      name: 'org-uengine-kernel-Evaluate',
      props: ['definition', 'value'],

      created: function(){
          if(!this.value)
              this.value
            =
            {
                _type: 'org.uengine.kernel.Evaluate',
                pv: {
                    name: ''
                },
                condition: '==',
                val: ''
            };
          console.log("def: " + this.definition);
          if(!this.value.pv) this.value.pv = {};

          if(!this.definition){
              var conditionEditor;
              var parent;
              parent = this.$parent;
              while(parent.$vnode.tag.indexOf('bpmn-relation') == -1) parent = parent.$parent;
              this.definition = parent.definition;

          }
    },

    watch:{
      'value': {
        handler: function(){
          this.$emit('input', this.value);
        },
        deep: true
      }

    },

    methods: {
      dragover: function() {
        window._dragItem = this;
      },
      // drop: function(item){
      //   var parent;
      //   parent = this.$parent;
      //   while(parent.$vnode.tag.indexOf('org-uengine-kernel') == -1) parent = parent.$parent;
      //   var myIdx = parent.data.conditionsVt.indexOf(this.data);
      //   parent.data.conditionsVt.splice(myIdx, 1);
      //   item.data.conditionsVt.push(this.data);

      //   var temp1 = parent.data;
      //   parent.data = null;
      //   parent.data = temp1;

      //   var temp2 = item.data;
      //   item.data = null;
      //   item.data = temp2;
      // },

      drag: function(){
        var parent;
        parent = this.$parent;
        while(parent.$vnode.tag.indexOf('org-uengine-kernel') == -1) parent = parent.$parent;
        var myIdx = this.$parent.value.conditionsVt.indexOf(this.value);
        this.$parent.value.conditionsVt.splice(myIdx, 1);

      },
      onDragenter: function () {
        var me = this;
        me.folderName = this.value.conditionsVt;
        me.navigationName = this.value.conditionsVt;
      },
      onDragLeave: function () {
        var me = this;
        console.log("드래그 끝");
      },

      conditionChanged: function(){
          if(this.value.condition == "means"){
              this.value._type = "org.uengine.five.kernel.SemanticEvaluate"
              this.value.condition = ">"
          }else
            //once LoopCountEvaluate, forever
          if(this.value.condition == "loopCount" || this.value._type == "org.uengine.five.kernel.LoopCountEvaluate"){
              this.value._type = "org.uengine.five.kernel.LoopCountEvaluate"
          }else{
            this.value._type = "org.uengine.kernel.Evaluate"
          }

          this.$emit('input', this.value);
      }
    }


  }

</script>
<style>
    div#processVal {
        float: left;
        width: 30%;
    }
    div#dataCon {
        float: left;
        width: 20%;
        margin-left: 10px;
        margin-right: 10px;
        padding: 0px;
    }
    div#dataVal {
        float: left;
        width: 30%;
        padding: 0px;
    }
    div#button {
        float: left;
        width: 5%;
        padding: 0px;

    }
</style>

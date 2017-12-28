<template>

  <div>
    <md-card @drop.native="drop()"
              draggable="true"
      style="box-shadow: 0 0 0 rgba(0, 0, 0, 0.2), 0 0 0 rgba(0, 0, 0, 0.14), 0 0 0 0 rgba(0, 0, 0, 0.12)">
      <div>
        {{myConditionType}} (
      <ul>
        <div v-for="childCondition in data.conditionsVt">

          <component :is="childCondition._type.split('.').join('-')" :data="childCondition" :definition="definition"></component>

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
      props: ['definition', 'data'],
      data: function(){
          return {
            conditionType: 'org.uengine.kernel.Or',
            myConditionType:'Or',
            thisConditionType: ''
          };
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
          _type: this.conditionType,

        })

        var temp = this.data;
        this.data = null;
        this.data = temp;
      },

      drop: function(){
        console.log("1");
        var item = window._dragItem;
        console.log("2");
        var parent;
        console.log("3");
        parent = item.$parent;
        console.log("4");
        while(parent.$vnode.tag.indexOf('org-uengine-kernel') == -1) parent = parent.$parent;
        console.log("5");
        var myIdx = parent.data.conditionsVt.indexOf(item.data);
        console.log("6");
        parent.data.conditionsVt.splice(myIdx, 1);

        var copy = JSON.parse(JSON.stringify(item.data));

        this.data.conditionsVt.push(copy);

        var temp1 = parent.data;
        parent.data = null;
        parent.data = temp1;

        var temp2 = this.data;
        this.data = null;
        this.data = temp2;
      },

    }

  }

</script>



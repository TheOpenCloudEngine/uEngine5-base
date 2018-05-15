<template>

  <div>

    <class-designer v-if="value && value._type=='org.uengine.uml.ClassDiagram'" :value="value" :id="id" :backend="backend"></class-designer>
    <process-designer v-if="value && value._type=='org.uengine.kernel.ProcessDefinition'" :value="value" :id="id" :backend="backend"></process-designer>
  </div>

</template>
<script>
  export default {
    props: {
      backend: Object,
      id: String,
      path: String,
      type: String
    },
    data() {
      return {

        value: null
      }
    },
    computed: {},

    created(){
      //alert(this.id + ":" + this.path)
      if(this.id && "new"!=this.id){
        this.load();
      }else{ //kind of new definition

        if(this.type){
          this.value = {
            _type: this.type
          }

          this.id = null;
        }
      }
    },

    watch:{

      //id: function(val){alert(val)}
    },

    //컴포넌트가 Dom 에 등록되었을 떄(실제 렌더링 되기 위해 활성화 되었을 때.)
    mounted() {
    },

    methods: {

      load: function(){

        var me = this;
        var def = {};
        var id = me.id;
        me.backend.$bind("definition/raw/" + me.id + ".json", def);
        def.$load().then(function (definition) {

          if(definition){
            me.value = definition.definition;

            console.log(definition.definition)

            me.value.id = id;
            me.id = id;
          }

        });


      }

    }
  }
</script>


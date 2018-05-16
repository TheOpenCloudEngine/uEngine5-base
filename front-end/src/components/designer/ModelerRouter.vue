<template>

  <div>
    <class-designer v-if="value && value._type=='org.uengine.uml.ClassDiagram'" :value="value" :id="path"
                    :backend="backend"></class-designer>
    <process-designer v-if="value && value._type=='org.uengine.kernel.ProcessDefinition'" :value="value" :id="path"
                      :backend="backend"></process-designer>
  </div>

</template>
<script>
  export default {
    props: {
      backend: Object,
      path: String
    },
    data() {
      return {
        value: null
      }
    },
    computed: {},
    created() {
      this.load();
    },
    //컴포넌트가 Dom 에 등록되었을 떄(실제 렌더링 되기 위해 활성화 되었을 때.)
    mounted() {
    },

    methods: {
      load: function () {
        var me = this;
        var def = {};
        me.backend.$bind('definition/raw/' + me.path + '.json', def);
        def.$load().then(function (definition) {
          if (definition) {
            me.value = definition.definition;
          }
        });
      }
    }
  }
</script>


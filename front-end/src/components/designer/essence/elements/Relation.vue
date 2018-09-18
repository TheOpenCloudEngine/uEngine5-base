<template>
  <div>
    <edge-element
      selectable
      connectable
      deletable
      :id="uuid"
      :vertices.sync="vertices"
      :from.sync="relation.sourceElement.relationView.id"
      :to.sync="relation.targetElement.relationView.id"
      :_style.sync="style"
      :label.sync="relation.name"
    >
    </edge-element>
  </div>
</template>

<script>
  import Element from '../../modeling/Element'
  export default {
    mixins: [Element],
    name: 'essence-relation',
    props: {},
    computed: {
      defaultStyle(){
        return {}
      },
      type(){
        return 'Relation'
      },
      createNew(from, to, vertices){
        return {
          sourceElement:from,
          targetElement:to,
          selected: false,
          relationView: {
            style: JSON.stringify({}),
            value: vertices,
          }
        }
      },
      vertices: {
        get: function () {
          var style;
          try {
            return JSON.parse(this.relation.relationView.value);
          } catch (e) {
            return null;
          }
        },
        set: function (val) {
          this.relation.relationView.value = JSON.stringify(val);
        }
      }
    },
    data: function () {
      return {
        otherwise: false
      };
    },
    watch: {

    },
    mounted: function () {
    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


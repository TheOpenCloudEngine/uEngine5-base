<template>

</template>

<script>
  import IBpmn from '../IBpmn'
  export default {
    mixins: [IBpmn],
    name: 'bpmn-relation',
    props: {},
    computed: {
      className(){
        return ''
      },
      shapeId(){
        return 'OG.shape.EdgeShape'
      }
    },
    data: function () {
      return {}
    },
    watch: {},
    mounted: function () {

    },
    methods: {}
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


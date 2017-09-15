<template>

</template>

<script>
  export default {
    name: 'bpmn-base',
    props: {
      activity: Object,
      role: Object,
      relation: Object,
      status: String,
      loopType: String
    },
    created: function () {

    },
    data: function () {
      return {
        drawer: false
      }
    },
    computed: {
      type(){
        return ''
      },
      style: {
        get: function () {
          var style;
          //스타일이 없다면 디폴트 스타일을 사용한다.
          if (this.activity) {
            return this.activity.elementView.style ? JSON.parse(this.activity.elementView.style) : this.defaultStyle;
          } else if (this.role) {
            return this.role.elementView.style ? JSON.parse(this.role.elementView.style) : this.defaultStyle;
          } else if (this.relation) {
            return this.relation.relationView.style ? JSON.parse(this.relation.relationView.style) : this.defaultStyle;
          }
        },
        set: function (val) {
          if (this.activity) {
            this.activity.elementView.style = JSON.stringify(val);
          } else if (this.role) {
            this.role.elementView.style = JSON.stringify(val);
          } else if (this.relation) {
            this.relation.relationView.style = JSON.stringify(val);
          }
        }
      }
    },
    watch: {},
    mounted: function () {

    },
    methods: {
      showProperty: function (component) {
        this.drawer = true;
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>

<template>

</template>

<script>
  export default {
    name: 'geometry',
    props: {},
    computed: {},
    data: function () {
      return {
        props: JSON.parse(JSON.stringify(this._props)),
        id: this.uuid(),
        element: null
      }
    },
    watch: {
      _props: {
        handler: function (newVal, oldVal) {
          this.props = JSON.parse(JSON.stringify(newVal))
          this.registToElement();
        },
        deep: true
      },
      props: {
        handler: function (newVal, oldVal) {
          var needToWatch = false;
          for (var key in newVal) {
            if (typeof newVal[key] == 'object') {
              if (!oldVal[key] || JSON.stringify(newVal[key]) != JSON.stringify(oldVal[key])) {
                //console.log('gemetry', key, newVal[key], oldVal[key]);
                needToWatch = true;
              }
            } else {
              if (newVal[key] != oldVal[key]) {
                needToWatch = true;
                //console.log('gemetry', key, newVal[key], oldVal[key]);
              }
            }
          }
          if (!needToWatch) {
            return;
          }
          this.registToElement();
        }
        ,
        deep: true
      }
    },
    mounted: function () {
      this.registToElement();
    },
    /**
     * 부모 컴포넌트에서 지오메트리를 삭제한다.
     */
    destroyed: function () {
      var me = this;
      if (me.element) {
        me.element.removeGeometry(me.id);
      }
    },
    methods: {
      /**
       * 부모 컴포넌트에 지오메트리를 등록한다.
       */
      registToElement: function () {
        var me = this;
        if (!me.element) {
          me.element = me.getClosestElement();
        }
        if (!me.element) {
          throw new Error('Not found parent element for geomery.');
        }
        me.element.addGeometry(this, me.id);
      },
      getClosestElement: function () {
        //console.log(this.$parent);
        var parentElementComponent = null;
        var parent;
        var getParent = function (component) {
          parent = component.$parent;
          if (parent) {
            if (parent.opengraphRole == 'opengraph-element') {
              parentElementComponent = parent;
            } else {
              getParent(parent);
            }
          }
        }
        getParent(this);
        return parentElementComponent;
      },
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


<template>
  <v-navigation-drawer class="property-panel"
                       temporary
                       v-model="drawer"
                       right
                       light
                       overflow
                       absolute
  >
    <v-tabs light v-model="active"
            :scrollable="false"
            centered>
      <v-tabs-bar class="cyan">
        <slot name="tabs">
        </slot>
        <v-tabs-item
          ripple
          :href="'#visual' + _uid">
          Visual
        </v-tabs-item>
        <v-tabs-slider class="primary"></v-tabs-slider>
      </v-tabs-bar>
      <v-tabs-items>
        <slot name="tabs-contents">

        </slot>
        <v-tabs-content :id="'visual' + _uid">
          <v-layout row wrap class="pa-3">
            <v-flex xs6>
              <v-text-field
                type="number"
                label="x"
                v-model="x"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="y"
                v-model="y"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="width"
                v-model="width"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="height"
                v-model="height"
              ></v-text-field>
            </v-flex>
            <v-flex xs12 v-for="(item, index) in style"
                    :item="item"
                    :index="index">
              <v-text-field
                type="text"
                :label="item.key"
                v-model="style[index].value"
              ></v-text-field>
            </v-flex>
          </v-layout>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </v-navigation-drawer>
</template>

<script>
  export default {
    name: 'bpmn-property-panel',
    props: {
      parentId: Number
    },
    computed: {},
    data: function () {
      return {
        preventWatch: false,
        element: null,
        canvas: null,
        drawer: false,
        id: null,
        value: null,
        x: null,
        y: null,
        width: null,
        height: null,
        label: null,
        style: [],
        text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
        active: null,
        //drawer: this.opened
      }
    },
    created: function () {
      var me = this;
      window.Vue.bpmnBus.$on('element-dblclick', function (component, element) {
        if (component._uid == me.parentId) {
          me.preventWatch = true;
          let boundary = component.canvas.getBoundary(element);
          if (!element || !boundary) {
            return;
          }
          me.width = boundary.getWidth();
          me.height = boundary.getHeight();
          me.x = boundary.getCentroid().x;
          me.y = boundary.getCentroid().y;
          me.label = element.shape.label;

          var parent = component.canvas.getRenderer().getParent(element);
          me.parent = parent ? parent.id : null;
          me.id = element.id;

          if (element.shape.geom.vertices) {
            me.value = element.shape.geom.vertices.toString();
          }

          var style = [];
          var createdStyle = element.shape.geom.style.map;
          for (var key in createdStyle) {
            style.push({
              key: key,
              value: createdStyle[key]
            });
          }
          me.style = style;
          me.drawer = true;
          if (!me.canvas) {
            me.canvas = component.canvas;
          }
          me.element = element;
        }
      });
    },
    watch: {
      x: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            var value = this.toNumber(newVal);
            if (value) {
              this.canvas.moveCentroid(this.element, [value, this.y]);
              $(this.canvas.getRootElement()).trigger('addHistory');
            }
          }
        },
        deep: true
      },
      y: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            var value = this.toNumber(newVal);
            if (value && value != 0) {
              this.canvas.moveCentroid(this.element, [this.x, value]);
              $(this.canvas.getRootElement()).trigger('addHistory');
            }
          }
        },
        deep: true
      },
      width: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            var value = this.toNumber(newVal);
            if (value && value != 0) {
              this.canvas.resizeBox(this.element, [value, this.height]);
              $(this.canvas.getRootElement()).trigger('addHistory');
            }
          }
        },
        deep: true
      },
      height: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            var value = this.toNumber(newVal);
            if (value && value != 0) {
              this.canvas.resizeBox(this.element, [this.width, value]);
              $(this.canvas.getRootElement()).trigger('addHistory');
            }
          }
        },
        deep: true
      },
      style: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            var style = {};
            if (newVal && newVal.length) {
              $.each(newVal, function (i, item) {
                style[item.key] = item.value;
              });
            }
            this.canvas.setShapeStyle(this.element, style);
            $(this.canvas.getRootElement()).trigger('addHistory');
          }
          this.preventWatch = false;
        },
        deep: true
      }
    },
    mounted: function () {

    },
    methods: {
      toNumber: function (val) {
        var number;
        if (isNaN(val)) {
          try {
            number = parseFloat(val);
          } catch (e) {

          }
        } else {
          number = val;
        }
        return number;
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


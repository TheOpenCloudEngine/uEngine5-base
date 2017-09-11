<template>
  <div class="canvas-panel">
    <opengraph>
      <template>
        <geometry-element
          :x="600"
          :y="300"
          :width="1000"
          :height="500"
          :_style="{pattern: cloudPattern,'fill-opacity': 1}"
          :redraw.sync="cloudRedraw"
        >
          <geometry-rect :upper-left="[0,0]" :width="1000" :height="1000" :_style="{stroke: 'black'}"></geometry-rect>
          <sub-elements>
            <div v-for="cloud in clouds">
              <geometry-element
                :sub-width="cloud.width + 'px'"
                :sub-height="cloud.height + 'px'"
                :sub-top="cloud.y + 'px'"
                :sub-left="cloud.x + 'px'"
                :sub-style="cloud.style"
              >
                <geometry-curve :controlPoints="[[50,200],[0,150],[50,0],[100,50],[140,0],[160,150],[110,200],[50,200]]"
                                :_style="{stroke: 'orange'}"></geometry-curve>
              </geometry-element>
            </div>
          </sub-elements>
        </geometry-element>
      </template>
    </opengraph>
  </div>
</template>
<script>
  export default {
    props: {},
    data () {
      return {
        cloudPattern: null,
        cloudRedraw: false,
        clouds: [],
      }
    },
    computed: {},
    mounted() {
      var me = this;
      var count = 0;

      function getRandomColor() {
        var letters = '0123456789ABCDEF';
        var color = '#';
        for (var i = 0; i < 6; i++) {
          color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
      }

      var drawCloud = function () {
        count = count + 10;
        var value = count % 1000;
        for (var i = 0; i < 5; i++) {
          var x = value + (i * 200);
          if (x > 1000) {
            x = x - 1000;
          }
          me.clouds[i] = {
            width: 100 + Math.floor((Math.random() * 20) + 1),
            height: 40 + Math.floor((Math.random() * 20) + 1),
            x: x,
            y: 100 + Math.floor((Math.random() * 20) + 1),
            style: {
              'fill-opacity': 1,
              'fill': getRandomColor()
            }
          }
        }
        //pattern:
        me.cloudPattern = {
          'id': 'OG.pattern.HatchedPattern',
          'unit-width': 18,
          'unit-height': 18,
          'pattern-width': value % 100,
          'pattern-height': value % 100,
          'style': {
            'stroke': 'grey'
          }
        };
        //me.cloudRedraw = true;
        //console.log(me.clouds);
      }
      setInterval(drawCloud, 100);

    },
    watch: {},
    methods: {}
  }
</script>

<style scoped lang="scss" rel="stylesheet/scss">
  .canvas-panel {
    top: 0px;
    bottom: 0px;
    left: 0px;
    right: 0px;
    position: absolute;
    overflow: hidden;
  }
</style>


<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <opengraph focus-canvas-on-select>
      <template>

        <!--entityA-->
        <geometry-element
          selectable
          movable
          resizable
          connectable
          deletable
          :id="entityA.id"
          :x.sync="entityA.x"
          :y.sync="entityA.y"
          :width.sync="entityA.width"
          :height="entityA.titleH + (entityA.one.length + entityA.two.length) * entityA.itemH">

          <geometry-rect
            :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 1.2,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '10'
        }"
          >
          </geometry-rect>

          <sub-elements>

            <!--title-->
            <text-element
              :sub-width="'100%'"
              :sub-height="entityA.titleH"
              :sub-top="0"
              :sub-left="0"
              :text="entityA.title">
            </text-element>


            <!--entityA.one-->
            <edge-element
              :vertices="[[0,entityA.titleH],['100%',entityA.titleH]]"
              :label="''"
              :sub-style="{'arrow-start': 'none', 'arrow-end': 'none'}"
            >
            </edge-element>

            <text-element v-for="(item, index) in entityA.one"
                          :sub-width="'80%'"
                          :sub-height="entityA.itemH"
                          :sub-top="entityA.titleH + (index * entityA.itemH)"
                          :sub-left="'10%'"
                          :sub-style="{'text-anchor': 'start'}"
                          :text="item">
            </text-element>


            <!--entityA.two-->
            <edge-element
              :vertices="[
            [0, entityA.titleH + (entityA.one.length * entityA.itemH)],
            ['100%',entityA.titleH + (entityA.one.length * entityA.itemH)]
            ]"
              :label="''"
              :sub-style="{'arrow-start': 'none', 'arrow-end': 'none'}"
            >
            </edge-element>

            <text-element v-for="(item, index) in entityA.two"
                          :sub-width="'80%'"
                          :sub-height="entityA.itemH"
                          :sub-top="entityA.titleH + ((entityA.one.length + index) * entityA.itemH)"
                          :sub-left="'10%'"
                          :sub-style="{'text-anchor': 'start'}"
                          :text="item">
            </text-element>

          </sub-elements>
        </geometry-element>


        <!--entityB-->
        <geometry-element
          selectable
          movable
          resizable
          connectable
          deletable
          :id="entityB.id"
          :x.sync="entityB.x"
          :y.sync="entityB.y"
          :width.sync="entityB.width"
          :height="entityB.titleH + (entityB.one.length) * entityB.itemH">

          <geometry-rect
            :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 1.2,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '10'
        }"
          >
          </geometry-rect>

          <sub-elements>

            <!--title-->
            <text-element
              :sub-width="'100%'"
              :sub-height="entityB.titleH"
              :sub-top="0"
              :sub-left="0"
              :text="entityB.title">
            </text-element>


            <!--entityB.one-->
            <edge-element
              :vertices="[[0,entityB.titleH],['100%',entityB.titleH]]"
              :label="''"
              :sub-style="{'arrow-start': 'none', 'arrow-end': 'none'}"
            >
            </edge-element>

            <text-element v-for="(item, index) in entityB.one"
                          :sub-width="'80%'"
                          :sub-height="entityB.itemH"
                          :sub-top="entityB.titleH + (index * entityB.itemH)"
                          :sub-left="'10%'"
                          :sub-style="{'text-anchor': 'start'}"
                          :text="item">
            </text-element>

          </sub-elements>
        </geometry-element>


        <edge-element
          selectable
          connectable
          deletable
          :from="'TV'"
          :to="'SEASON'"
          :label="'TV-SEASON'"
          :_style="{
             'marker': {
                  'start': {
                      'id': 'OG.marker.MyMarker',
                      'size': [20, 20]
                  },
                  'end': {
                      'id': 'OG.marker.MyMarker',
                      'size': [20, 20]
                  }
              }
          }"
        ></edge-element>

      </template>
    </opengraph>
  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean
    },
    data () {
      return {
        entityA: {
          id: 'TV',
          x: 300,
          y: 400,
          width: 300,
          title: 'TV Show',
          titleH: 50,
          itemH: 30,
          one: [
            'TV Show Title',
            'TV Show Genre',
            'TV Show Description',
            'TV Show Runtime',
            'TV Show Time Slots'
          ],
          two: [
            'TV Show Creator',
            'TV Show Country of Origin',
            'TV Show Language',
            'Television Network Name',
            'TV Show Stars',
            'TV Show Release Date',
            'TV Show End Date'
          ]
        },
        entityB: {
          id: 'SEASON',
          x: 800,
          y: 400,
          width: 300,
          title: 'Seasons',
          titleH: 50,
          itemH: 30,
          one: [
            'Season Number',
            'Season Number of Episode',
            'Season Start Date',
            'Season End Date'
          ]
        }
      }
    },
    created: function () {
      /**
       * 마커 도큐먼트 : http://cloud.opencloudengine.org:22582/opengraph/public/marker.html
       * @constructor
       */
      OG.marker.MyMarker = function () {
        OG.marker.MyMarker.superclass.call(this);
        this.MARKER_ID = 'OG.marker.MyMarker';
      };
      OG.marker.MyMarker.prototype = new OG.marker.IMarker();
      OG.marker.MyMarker.superclass = OG.marker.IMarker;
      OG.marker.MyMarker.prototype.constructor = OG.marker.MyMarker;
      OG.MyMarker = OG.marker.MyMarker;
      OG.marker.MyMarker.prototype.createMarker = function () {
        var geom1, geom2, geomCollection = [];
        if (this.geom) {
          return this.geom;
        }
        geom1 = new OG.geometry.Rectangle([0, 0], 100, 100);
        geom1.style = new OG.geometry.Style({
          'stroke-width': 1.2,
          fill: '#acffff',
          'fill-opacity': 1
        });
        geom2 = new OG.geometry.Polygon([
          [20, 75],
          [40, 30],
          [60, 60],
          [80, 20],
          [60, 75],
          [40, 50]
        ]);
        geom2.style = new OG.geometry.Style({
          "fill": "black",
          "fill-opacity": 1
        });
        geomCollection.push(geom1);
        geomCollection.push(geom2);
        this.geom = new OG.geometry.GeometryCollection(geomCollection);
        return this.geom;
      };
    },
    computed: {},
    mounted() {
      var me = this;
      function getRandomColor() {
        var letters = '0123456789ABCDEF';
        var color = '#';
        for (var i = 0; i < 6; i++) {
          color += letters[Math.floor(Math.random() * 16)];
        }
        me.randomColor = color;
        me.redraw = true;
        me.shoeSize = 310;
      }
      //setInterval(getRandomColor, 200);
//      setTimeout(function () {
//        getRandomColor();
//      }, 2000);
    },
    watch: {},
    methods: {
      onClone: function (a, b, c) {
        console.log('onClone', a, b, c);
      },
      beforeConnectShape: function (a, b, c) {
        //console.log(a,b,c);
      },
      sayHello: function (cloneComponent) {
        console.log('sayHello', cloneComponent);
      }
    }
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

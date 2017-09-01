<template>

  <g v-bind:x="activity.x" v-bind:y="activity.y" fill="none" stroke="#000">
    <path fill="url(#364r_.1__.1__FFFFFF-_FFFFCC)"
          stroke="#000000"
          v-bind:d="path"
          fill-r="1"
          fill-cx="0.1"
          fill-cy="0.1"
          opacity="1"
          fill-opacity="1"
          stroke-width="1.2"
          r="10"
          name="ORIGINAL_NODE"
          style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); opacity: 1; fill-opacity: 1; cursor: move;">
    </path>
    <g x="0" y="0" fill="none" stroke="#000" id="Task_1_LABEL" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);">
      <text v-bind:x="x+50" v-bind:y="y+50" text-anchor="middle" font="10px 'serif'" stroke="none" fill="#000000"
            style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); text-anchor: middle; font-style: normal; font-variant: normal; font-weight: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: serif; cursor: move; background-color: rgb(255, 255, 255);"
            fill-r="1" fill-cx="0.1" fill-cy="0.1" fill-opacity="1" stroke-width="1.2" r="10" font-size="12px"
            id="Task_1_LABELFO">
        <tspan dy="3.75" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);">
          {{ activity.name.text }}
        </tspan>
      </text>
    </g>
    <image v-bind:x="x" v-bind:y="y" width="20" height="20" preserveAspectRatio="none" title="undefined"
           xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="/static/image/symbol/User.png" text-anchor="middle"
           id="OG_1239_281" _index="0"
           style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); text-anchor: middle;"></image>
    {{path}}
  </g>

</template>

<script>

  export default{

    name: 'SVGActivity',
    props: {
      activity: Object
    },

    watch: {
      activity: {

        handler: function move() {

        },
        deep: true
      }

    },
//
//      data: function(){
//
//// var path = "M232,262Q232,252,242,252L322,252Q332,252,332,262L332,322Q332,332,322,332L242,332Q232,332,232,322Z";
//
//
//        var x =
//        var y = this.activity.elementView.y;
//
//        var path =
//
//        return {
//          path: path,
//          x: x,
//          y: y
//        }
//
//      },
//
    computed: {
      path: function () {

        var x = this.activity.elementView.x;
        var y = this.activity.elementView.y;


        return "M" + x + "," + y +
          "Q" + x + "," + (y - 10) + "," + (x + 10) + "," + (y - 10) +
          "L" + (x + 100) + "," + (y - 10) +
          "Q" + (x + 100) + "," + (y - 10) + "," + (x + 100) + "," + (y) +
          "L" + (x + 100) + "," + (y + 60) +
          "Q" + (x + 100) + "," + (y + 70) + "," + (x + 90) + "," + (y + 70) +
          "L" + (x + 10) + "," + (y + 70) +
          "Q" + (x) + "," + (y + 70) + "," + x + "," + (y + 60)
          + "Z";

      },

      x: function () {
        return this.activity.elementView.x;
      },

      y: function () {
        return this.activity.elementView.y;
      }

    }


  }

</script>

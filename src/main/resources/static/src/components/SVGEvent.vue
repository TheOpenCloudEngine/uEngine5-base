<template>

  <g x="0" y="0" fill="none" stroke="#000" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); cursor: move; position: relative;" _to="Task_1" _toedge="StartEvent_1-Task_1">
    <circle v-bind:cx="x" v-bind:cy="y" r="15" fill="#ffffff" stroke="#000000" fill-r=".5" fill-cx=".5" fill-cy=".5" fill-opacity="0" stroke-width="1.5" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); cursor: move;">
    </circle>
    <g x="0" y="0" fill="none" stroke="#000" id="StartEvent_1_LABEL" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);">
      <text v-bind:x="x" v-bind:y="y+40" text-anchor="middle" font="10px 'serif'" stroke="none" fill="#000000" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); text-anchor: middle; font-style: normal; font-variant: normal; font-weight: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: serif; cursor: move; background-color: rgb(255, 255, 255);" fill-r=".5" fill-cx=".5" fill-cy=".5" fill-opacity="1" stroke-width="1.5" font-size="12px" id="StartEvent_1_LABELFO">
        <tspan dy="-3.5859375" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);">{{activity.name.text}}</tspan>
      </text>
    </g>
  </g>
</template>

<script>

  export default{

      props: {
          activity: Object
      },

    computed: {

      x: function(){
          return  this.activity.elementView.x;
      },

      y: function(){
          return  this.activity.elementView.y;
      }

    }

  }

</script>

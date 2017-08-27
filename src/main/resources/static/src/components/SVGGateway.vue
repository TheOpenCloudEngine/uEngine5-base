<template>

  <g x="0" y="0" fill="none" stroke="#000" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); cursor: move; position: relative;">
    <path fill="#ffffff" stroke="#000000" v-bind:d="path" fill-r=".5" fill-cx=".5" fill-cy=".5" fill-opacity="0" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0); cursor: move;">
    </path>
  </g>
</template>

<script>

  export default{

      props: {
          activity: Object
      },

    computed: {
      path: function(){

        var x = this.activity.elementView.x;
        var y = this.activity.elementView.y;


        return "M" + (x) + "," + (y-25) +
          "L" + (x+25) + "," + (y) +
          "L" + (x) + "," + (y+25) +
          "L" + (x-25) + "," + (y) +
          "L" + (x) +"," + (y-25);

      },

      x: function(){
          return  this.activity.elementView.x;
      },

      y: function(){
          return  this.activity.elementView.y;
      },



    }

  }

</script>

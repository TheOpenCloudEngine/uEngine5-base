<template>
  <div>
    <router-view></router-view>

    <!--글로벌 알림 컴포넌트-->
    <v-snackbar ref="snackbar"
                :timeout="snackbar.timeout"
                :top="snackbar.top === true"
                :right="snackbar.right === true"
                v-model="snackbar.trigger"
                :success="snackbar.context === 'success'"
                :info="snackbar.context === 'info'"
                :warning="snackbar.context === 'warning'"
                :error="snackbar.context === 'error'"
                :primary="snackbar.context === 'primary'"
                :secondary="snackbar.context === 'secondary'"
                :multi-line="snackbar.mode === 'multi-line'"
    >
      {{ snackbar.text }}
      <v-btn dark flat @click.native="snackbar.trigger = false">Close</v-btn>
    </v-snackbar>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        snackbar: {
          top: true,
          right: true,
          timeout: 6000,
          trigger: false,
          mode: 'multi-line',
          context: 'info',
          text: ''
        },
      }
    },
    mounted() {

    },
    methods: {
      info: function (msg) {
        this.snackbar.context = 'info';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      },
      error: function (msg) {
        this.snackbar.context = 'error';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      },
      warning: function(msg){
        this.snackbar.context = 'warning';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      },
      success: function(msg){
        this.snackbar.context = 'success';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">

  /*Golbal css*/
  .content-wrap {
    position: absolute;
    /*overflow: hidden;*/

    .scroll-inner {
      position: relative;
      width: 100%;
      height: 100%;
      padding-right: 16px;
    }
  }

  .content-wrap.left {
    top: 0px;
    bottom: 0px;
    left: 0px;
    width: 300px;
  }

  .content-wrap.center {
    top: 0px;
    bottom: 0px;
    left: 300px;
    right: 0px;
  }

  .content-wrap.top {
    top: 0px;
    height: 130px;
    left: 0px;
    width: 100%;
    padding-left: 16px;
    padding-right: 16px;
  }

  .content-wrap.bottom {
    top: 130px;
    bottom: 0px;
    left: 0px;
    width: 100%;
  }

  .container.fluid {
    position: absolute;

  }

  .full-toggle {
    width: 100%;

    .btn {
      width: 25%;
    }
  }
</style>

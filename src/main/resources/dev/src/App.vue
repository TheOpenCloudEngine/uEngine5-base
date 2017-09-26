<template>
  <div>
    <router-view></router-view>

    <!--서비스 로케이터 리스트-->
    <service-locator :host="location.protocol + '//' + location.hostname + ':8080'" path="/"
                     resource-name="codi"></service-locator>

    <service-locator ref="backend" :host="location.protocol + '//' + location.hostname + ':8080'"></service-locator>

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
        location: window.location,
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
      warning: function (msg) {
        this.snackbar.context = 'warning';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      },
      success: function (msg) {
        this.snackbar.context = 'success';
        this.snackbar.text = msg;
        this.snackbar.trigger = true;
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">

</style>

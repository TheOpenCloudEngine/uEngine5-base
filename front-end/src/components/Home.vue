<template>
  <div>
    <md-toolbar class="fixed-toolbar">
      <md-button class="md-icon-button" @click="toggleLeftSidenav" style="z-index: 2;">
        <md-icon>menu</md-icon>
      </md-button>

      <img src="http://uengine.org/assets/img/uengine/logo_bright.png" style="height:25px">

      <span style="margin-left: 15px">
        <breadcrumbs style="width: 100%"></breadcrumbs>
      </span>

      <md-layout md-align="end">
        <md-button class="md-raised md-primary" @click="logout">{{ $t("message['common.logout']") }}
        </md-button>
      </md-layout>
    </md-toolbar>

    <md-sidenav class="md-left" ref="leftSidenav">
      <md-toolbar class="md-account-header">
        <iam-avatar :iam="iam"></iam-avatar>
      </md-toolbar>

      <md-list>
        <md-list-item v-for="item in items" v-on:click="move(item.routerPath)" class="md-primary">
          <md-icon>{{ item.icon }}</md-icon>
          <span>{{ item.title }}</span>
        </md-list-item>
      </md-list>
    </md-sidenav>

    <div class="fluid">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
  export default {
    props: {
      iam: Object
    },
    data() {
      return {
        drawer: null,
        items: [
          {title: 'Workspace', icon: 'supervisor_account', routerPath: '/workspace'},
          {title: 'Designer', icon: 'crop', routerPath: '/designer'},
          {title: 'Service', icon: 'cloud_queue', routerPath: '/services'},
          {title: 'Instances', icon: 'list', routerPath: '/instance'}
        ],
        mini: false,
      }
    },
    mounted() {
      this.updateActive();

    },
    watch: {
      '$route'(to, from) {
        this.updateActive();
      },
    },
    methods: {
      toggleLeftSidenav() {
        this.$refs.leftSidenav.toggle();
      },
      logout: function () {
        var me = this;
        this.iam.logout();

        //Additional access_token storage
        localStorage.removeItem('access_token');

        this.$router.push({
          path: '/auth/login'
        })
      },
      updateActive: function () {
        var me = this;
        var routers = me.$route.matched;
        $.each(me.items, function (i, item) {
          var isActive = false;
          $.each(routers, function (r, router) {
            if (router.name == item.routerPath) {
              isActive = true;
            }
          });
          item.isActive = isActive;
        })
      },
      move(routerPath) {
        this.$router.push(routerPath)
      },

    },

  }
</script>

<style scoped lang="scss" rel="stylesheet/scss">
  .fixed-toolbar {
    height: 64px;
    overflow: hidden;
  }

  .fluid {
    position: relative;
    height: calc(100vh - 64px);
    overflow-y: auto;
    overflow-x: hidden;
    width: 100%;
  }
</style>

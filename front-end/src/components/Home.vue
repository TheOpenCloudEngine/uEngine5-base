<template>
  <div>
    <md-toolbar class="fixed-toolbar">
      <md-button class="md-icon-button" @click="toggleLeftSidenav">
        <md-icon>menu</md-icon>
      </md-button>
      <h2 class="md-title">uEngine5</h2>
      <md-button class="md-raised md-primary" @click="logout">로그 아웃</md-button>
      <avatar-uploader :iam="iam"></avatar-uploader>
    </md-toolbar>

    <md-sidenav class="md-left" ref="leftSidenav">
      <md-toolbar class="md-account-header">
          <iam-avatar :iam="iam"></iam-avatar>
        <!--<md-list class="md-transparent">-->
          <!--<md-list-item class="md-avatar-list">-->
            <!--&lt;!&ndash;avatar iam 으로 변경&ndash;&gt;-->
            <!--<md-avatar class="md-large">-->
              <!--&lt;!&ndash;<img src="https://placeimg.com/64/64/people/8" alt="People">&ndash;&gt;-->
              <!--<img-->
                  <!--:src="'http://iam.uengine.io:8080/rest/v1/avatar?userName='+ user.username"-->
                  <!--v-if="user.username"-->
                  <!--alt="People">-->
            <!--</md-avatar>-->

            <!--<span style="flex: 1"></span>-->
          <!--</md-list-item>-->

          <!--<md-list-item>-->
            <!--<div class="md-list-text-container">-->
              <!--<span v-if="user.name">{{user.name}}</span>-->
              <!--<span v-if="user.email">{{user.email}}</span>-->
            <!--</div>-->

            <!--<md-button class="md-icon-button md-list-action">-->
              <!--<md-icon>arrow_drop_down</md-icon>-->
            <!--</md-button>-->
          <!--</md-list-item>-->
        <!--</md-list>-->
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
          {title: 'Workspace', icon: 'question_answer', routerPath: '/sns'},
          {title: 'Designer', icon: 'question_answer', routerPath: '/definition'},
          {title: 'Instances', icon: 'question_answer', routerPath: '/instance'}
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
      }
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
    }
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
    overflow-y: scroll;
    overflow-x: hidden;
    width: 100%;
  }
</style>

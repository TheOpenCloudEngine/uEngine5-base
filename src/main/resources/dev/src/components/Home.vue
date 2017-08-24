<template>
  <v-app light toolbar fixed-footer>
    <v-navigation-drawer
      persistent
      v-model="drawer"
      :mini-variant="mini"
      overflow
    >
      <v-list class="pa-0">
        <v-list-tile v-if="mini" @click.native.stop="mini = !mini">
          <v-list-tile-action>
            <v-icon light>chevron_right</v-icon>
          </v-list-tile-action>
        </v-list-tile>
        <v-list-tile avatar tag="div">
          <v-list-tile-avatar>
            <img src="https://randomuser.me/api/portraits/men/85.jpg"/>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title>John Leider</v-list-tile-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon @click.native.stop="mini = !mini">
              <v-icon>chevron_left</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
      <v-list class="pt-0" dense>
        <v-divider light></v-divider>
        <v-list-tile v-for="item in items">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title v-on:click="move(item.routerPath)" :class="{ 'blue--text': item.isActive }">{{ item.title
              }}
            </v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

    <v-toolbar fixed>
      <v-toolbar-side-icon @click.native.stop="drawer = !drawer">
      </v-toolbar-side-icon>
      <v-toolbar-title>Process Codi</v-toolbar-title>

      <v-btn v-on:click="logout" flat>로그 아웃</v-btn>
      <avatar-uploader :iam="iam"></avatar-uploader>

    </v-toolbar>

    <main>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </main>

  </v-app>
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
          {title: 'Sns', icon: 'question_answer', routerPath: '/sns'},
          {title: 'Designer', icon: 'question_answer', routerPath: '/definition'}
        ],
        mini: false
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
      profile: function(){

      }
    }
  }
</script>

<style scoped lang="scss" rel="stylesheet/scss">

</style>

<template>
  <v-app dark toolbar fixed-footer>
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
            <v-list-tile-title v-on:click="move(item.routerName)" :class="{ 'blue--text': item.isActive }">{{ item.title
              }}
            </v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

    <v-toolbar fixed>
      <v-toolbar-side-icon @click.native.stop="drawer = !drawer">
      </v-toolbar-side-icon>
      <v-toolbar-title>Vuetify</v-toolbar-title>
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

    data() {
      return {
        drawer: null,
        items: [
          {title: 'Sns', icon: 'question_answer', routerName: 'sns'},
          {title: 'Designer', icon: 'question_answer', routerName: 'designer'}
        ],
        mini: false
      }
    },
    mounted() {

    },
    watch: {
      '$route'(to, from) {
        console.log('update!');
        // 경로 변경에 반응하여...
      }
    },
    methods: {
      move(routerName) {
        console.log(this.items);
        this.$router.push(routerName)
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
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

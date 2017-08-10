<template>
  <div>
    <div class="content-wrap left">
      <div class="content-wrap top">
        <v-layout row wrap>
          <v-flex>
            <v-text-field
              name="input-1-3"
              label="Search"
              single-line
              append-icon="search"
            ></v-text-field>
          </v-flex>
          <v-flex>
            <v-btn fab dark small class="indigo">
              <v-icon dark>add</v-icon>
            </v-btn>
          </v-flex>
        </v-layout>

        <v-layout row>
          <v-btn-toggle class="full-toggle" mandatory v-bind:items="toggle_text" v-model="text"></v-btn-toggle>
        </v-layout>
      </div>
      <div class="content-wrap bottom">
        <div class="scroll-inner">
          <v-list two-line>
            <template v-for="(item, index) in items">
              <v-list-tile>
                <v-list-tile-avatar>
                  <img :src="item.avatar"/>
                </v-list-tile-avatar>
                <v-list-tile-content>
                  <v-list-tile-title v-html="item.title"></v-list-tile-title>
                  <v-list-tile-sub-title v-html="item.subtitle"></v-list-tile-sub-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider></v-divider>
            </template>
          </v-list>
        </div>
      </div>
    </div>

    <div class="content-wrap center">
      <div class="scroll-inner">
        start
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        end
        start
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        end
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        drawer: null,
        items: [
          {title: 'Home', icon: 'dashboard'},
          {title: 'About', icon: 'question_answer'}
        ],
        mini: false,
        toggle_text: [
          {text: '새소식', value: 1},
          {text: '참여중', value: 2},
          {text: '할일', value: 3},
          {text: '달력', value: 4},
        ],
        text: 1,

        items: [
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
          {
            avatar: '/static/image/1.jpg',
            title: 'Brunch this weekend?',
            subtitle: "<span class='grey--text text--darken-2'>Ali Connors</span> — I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
          },
        ]
      }
    },
    mounted() {
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
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

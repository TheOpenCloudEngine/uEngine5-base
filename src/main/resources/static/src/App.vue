<template>
  <v-app>


    <iam-login iam-server="http://iam.uengine.io:8080" ref="login" :listener="this" v-if="!loggedIn" id="jyjang@uengine.org" password="test"></iam-login>

    <service-locator host="http://localhost:8080" ref="backend" path="instances"></service-locator>

    <div v-if="loggedIn">




      <!-- Left Navigator -->
      <v-navigation-drawer
              class="grey lighten-4 pb-0"
              absolute
              height="100%"
              light
              persistent
              clipped
              v-model="drawer"
      >
        <v-list dense>
          <template>
            <v-list-item>
              <v-list-tile to="#/all" style="text-decoration:none" @click.native='search("findAllICanSee")'>
                <v-list-tile-action>
                  <v-icon>description</v-icon>
                </v-list-tile-action>
                <v-list-tile-content>
                  <v-list-tile-title>
                    News Feed
                  </v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-list-tile to="#/started-by-me" style="text-decoration:none" @click.native='search("findStartByMe")'>
                <v-list-tile-action>
                  <v-icon>work</v-icon>
                </v-list-tile-action>
                <v-list-tile-content>
                  <v-list-tile-title>
                    Started By Me
                  </v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-list-tile to="#/process-designer" style="text-decoration:none">
                <v-list-tile-action>
                  <v-icon>work</v-icon>
                </v-list-tile-action>
                <v-list-tile-content>
                  <v-list-tile-title>
                    Class Editor
                  </v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-list-tile to="#/process-designer" style="text-decoration:none">
                <v-list-tile-action>
                  <v-icon>work</v-icon>
                </v-list-tile-action>
                <v-list-tile-content>
                  <v-list-tile-title>
                    Process Designer
                  </v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
            </v-list-item>
          </template>
        </v-list>
      </v-navigation-drawer>
      <!-- toolbar -->
      <v-toolbar>
        <v-toolbar-side-icon @click.native.stop="drawer = !drawer" light></v-toolbar-side-icon>
        <v-toolbar-title style="color: #ffffff">Process Codi</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>
      <!-- Contents -->
      <main>
        <v-container fluid>
          <router-view></router-view>

          <form novalidate @submit.stop.prevent="submit" v-if="true">
            <md-input-container>
              <label>새로운 주제</label>
              <md-input v-model="title"></md-input>
            </md-input-container>
            <md-button @click.native="send">게시</md-button>
          </form>

          <md-list class="custom-list md-triple-line">
            <md-list-item v-for="item in feed">
              <md-avatar>
                <img src="https://placeimg.com/40/40/people/1" alt="People">
              </md-avatar>

              <div class="md-list-text-container">
                <span>{{item.name}}</span>
                <p>{{item.lastCmnt}}</p>
                <p>{{item.lastCmnt2}}</p>

                <instance-view :instance-id="item.instId"></instance-view>
              </div>

              <md-button class="md-icon-button md-list-action">
                <md-icon class="md-primary">star</md-icon>
              </md-button>

              <md-divider class="md-inset"></md-divider>
            </md-list-item>
          </md-list>
        </v-container>
      </main>




    </div>

  </v-app>
</template>
<script>
    export default {

        props: {
            loggedIn: Boolean
        },

        data () {

          return {
            feed: [],
            title: '',
            clipped: false,
            drawer: true,
            fixed: true,
            url1: 'http://www.metaworks4.io',
            url2: 'https://www.github.com/theopencloudengine'
          }

        },
        methods: {

            onLoggedIn: function(){
                this.loggedIn = true;

                this.search("findAllICanSee");

            },

            search: function(filter){
              var me = this;

              this.$refs['backend'].invoke({
                path: "instances/search/" + filter,
                query: {"sort": "moddate,desc"},
                success: function(data){
                  me.feed = data._embedded.instances;
                }
              });

            },

            send: function(){

                this.$refs['backend'].invoke({
                    method: "POST",
                    data: {
                        name: this.title,
                        lastCmnt: this.title,

//                        roleMappings: [{
//                            name: 'initiator',
//                            endpoint: 'jyjang'
//                        }],
//                        workLists: [{
//
//                        }]

                    }
                });

            },
            setTitle: function (name) {
                $('.toolbar__title').text(name);
            }
        }

    }
</script>

<style lang="stylus">
  @import './stylus/main'
</style>

<template>
  <div>

    <service-locator ref="backend" host="http://localhost:8080"></service-locator>

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
            <v-btn fab light small class="indigo">
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
              <v-list-tile @click.native="selectWorkItem(item._links.self.href)">
                <v-list-tile-avatar>
                  <img :src="'http://localhost:8080/iam/rest/v1/avatar?userName=' + item.endpoint"/>
                </v-list-tile-avatar>
                <v-list-tile-content>
                  <v-list-tile-title v-html="item.title"></v-list-tile-title>
                  <v-list-tile-sub-title>{{item.startedDate}}</v-list-tile-sub-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider></v-divider>
            </template>
          </v-list>
        </div>
      </div>
    </div>

    <div class="content-wrap center">
      <work-item :task-id="selectedTaskId" :reload.sync="reload"></work-item>
    </div>
  </div>
</template>
<script>
  export default {

    data () {
      return {
        reload: false,
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

        items: [],
        selectedTaskId: null
      }
    },

    methods: {
      selectWorkItem: function (taskId) {
        this.selectedTaskId = taskId;
      },
      load: function () {
        var serviceLocator = this.$refs['backend'];
        var me = this;
        serviceLocator.invoke({
          path: 'worklist/search/findToDo',
          success: function (data) {
            me.items = data._embedded.worklist;
            console.log(me.items);
          }
        });
      }
    },
    watch: {
      reload: function () {
        this.load();
        this.reload = false;
      }
    },

    mounted() {
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
      this.load();
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">


</style>

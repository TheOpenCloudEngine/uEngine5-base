<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <md-layout style="height: 100%">

    <md-layout md-flex="20" class="bg-white" style="border-right: 1px solid #DFE3E6;padding: 0px">
      <div style="width: 100%;height: 100%;overflow-x: hidden;overflow-y: auto">

        <div style="padding: 16px;">
          <md-input-container>
            <md-icon>search</md-icon>
            <label>Search</label>
            <md-input type="text"></md-input>
          </md-input-container>
        </div>

        <md-list class="md-double-line md-dense">
          <md-list-item>
            <md-icon>move_to_inbox</md-icon>
            <span>Inbox
              <md-chip>{{items.length}}</md-chip>
            </span>
            <md-list-expand>
              <md-list>
                <md-list-item v-for="(item, index) in items">
                  <md-avatar>
                    <img :src="'http://iam.pas-mini.io/rest/v1/avatar?userName=' + item.endpoint"
                         alt="People">
                  </md-avatar>

                  <div class="md-list-text-container cursor" v-on:click="selectWorkItem(item._links.self.href)">
                    <span>{{item.title}}</span>
                    <span class="md-caption">{{getTimeText(item.startDate, 'start')}}</span>
                  </div>

                  <md-button v-if="getIdFromUri(item._links.self.href) == id" class="md-icon-button md-list-action">
                    <md-icon>check</md-icon>
                  </md-button>
                </md-list-item>
              </md-list>
            </md-list-expand>
          </md-list-item>

          <md-list-item>
            <md-icon>send</md-icon>
            <span>Participating
              <md-chip>{{participating.length}}</md-chip>
            </span>
            <md-list-expand>
              <md-list>
                <md-list-item v-for="(item, index) in participating">
                  <div class="md-list-text-container cursor" v-on:click="selectInstance(item._links.self.href)">
                    <span>{{item.defName}}</span>
                    <span>{{getTimeText(item.startedDate, 'start')}}</span>
                  </div>
                </md-list-item>
              </md-list>
            </md-list-expand>
          </md-list-item>

          <md-list-item>
            <md-icon>delete</md-icon>
            <span>Trash</span>
          </md-list-item>

          <md-list-item>
            <md-icon>error</md-icon>
            <span>Error</span>

            <md-divider class="md-inset"></md-divider>
          </md-list-item>

        </md-list>
      </div>
    </md-layout>
    <md-layout md-flex="80">
      <div style="width: 100%;height: 100%;padding: 16px;overflow-x: hidden;overflow-y: auto">
        <instance-handler v-if="submenu == 'instance'&& id" :id="id"></instance-handler>
        <work-item-handler v-if="submenu == 'worklist'&& id" :id="id"></work-item-handler>
      </div>
    </md-layout>
  </md-layout>
</template>
<script>
  export default {
    props: {
      id: String,
      submenu: String
    },
    data() {
      return {
        location: window.location,
        items: [],
        participating: []
      }
    },
    mounted() {
      this.load();
    },
    computed: {},
    methods: {
      getTimeText: function (value, diffType) {
        var timeText = '';
        var diffTime;
        if (diffType == 'start') {
          diffTime = (new Date().getTime() - new Date(value)) / 1000;
        } else {
          diffTime = (new Date(value) - new Date().getTime()) / 1000;
        }
        if (diffTime < 60) {
          timeText = "방금";
        } else if ((diffTime /= 60) < 60) {
          timeText = Math.floor(diffTime) + "분";
        } else if ((diffTime /= 60) < 24) {
          timeText = Math.floor(diffTime) + "시간";
        } else if ((diffTime /= 24) < 30) {
          timeText = Math.floor(diffTime) + "일";
        } else if ((diffTime /= 30) < 12) {
          timeText = Math.floor(diffTime) + "달";
        } else {
          timeText = Math.floor(diffTime) + "년";
        }
        if (diffType == 'start') {
          timeText = timeText + ' 전';
        } else {
          timeText = timeText + ' 남음';
        }
        return timeText;
      },
      getIdFromUri: function (uri) {
        var split = uri.split('/');
        return split[split.length - 1];
      },
      selectWorkItem: function (uri) {
        this.$router.push({
          path: '/workspace/worklist/' + this.getIdFromUri(uri)
        })
      },
      selectInstance: function (uri) {
        this.$router.push({
          path: '/workspace/instance/' + this.getIdFromUri(uri)
        })
      },
      load: function () {
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var me = this;
        serviceLocator.invoke({
          path: 'worklist/search/findToDo',
          success: function (data) {
            me.items = data._embedded.worklist;
            console.log(me.items);
          }
        });

        serviceLocator.invoke({
          path: 'instances/search/findFilterICanSee',
          success: function (data) {
            me.participating = data._embedded.instances;
          }
        });

      },


    },
    watch: {}
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .cursor {
    cursor: pointer;
  }

  .md-chip {
    margin-left: 15px;
  }

</style>

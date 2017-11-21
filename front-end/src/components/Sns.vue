<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div>
    <md-layout :md-gutter="16">
      <md-layout md-flex="25">
        <md-input-container>
          <md-icon>search</md-icon>
          <label>Search</label>
          <md-input type="text"></md-input>
        </md-input-container>

        <md-list class="md-double-line md-dense">
          <md-list-item v-for="(item, index) in items">
            <md-avatar>
              <img
                :src="'http://iam.uengine.io:8080/rest/v1/avatar?userName=' + item.endpoint"
                alt="People">
            </md-avatar>

            <div class="md-list-text-container">
              <span v-on:click="selectWorkItem(item._links.self.href)" class="cursor">{{item.title}}</span>
              <span>{{item.startedDate}}</span>
            </div>

            <md-button class="md-icon-button md-list-action">
              <md-icon>sms</md-icon>
            </md-button>
          </md-list-item>
        </md-list>


      </md-layout>
      <md-layout>
        <work-item :task-id="selectedTaskId" :reload.sync="reload"></work-item>
      </md-layout>
    </md-layout>
  </div>
</template>
<script>
  export default {

    data () {
      return {
        location: window.location,
        reload: false,
        items: [],
        selectedTaskId: null
      }
    },

    methods: {
      selectWorkItem: function (taskId) {
        this.selectedTaskId = taskId;
        console.log('this.selectedTaskId', this.selectedTaskId);
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
      }
    },
    watch: {
      reload: function () {
        this.load();
        this.reload = false;
      }
    },

    mounted() {
      this.load();
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .cursor {
    cursor: pointer;
  }

</style>

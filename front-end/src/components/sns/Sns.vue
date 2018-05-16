<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="component-docs">

      <div class="usage-content">

          <md-input-container>
            <md-icon>search</md-icon>
            <label>Search</label>
            <md-input type="text"></md-input>
          </md-input-container>



          <md-list class="md-double-line md-dense">

            <md-list-item>
              <md-icon>move_to_inbox</md-icon> <span>Inbox <md-chip>{{items.length}}</md-chip></span>
              <md-list-expand>
                <md-list>
                  <md-list-item v-for="(item, index) in items">
                    <md-avatar>
                      <img
                        :src="'http://iam.pas-mini.io/rest/v1/avatar?userName=' + item.endpoint"
                        alt="People">
                    </md-avatar>

                    <div class="md-list-text-container">
                      <span v-on:click="select(item._links.self.href)" class="cursor">{{item.title}}</span>
                      <span>{{item.startedDate}}</span>
                    </div>

                    <md-button class="md-icon-button md-list-action">
                      <md-icon>sms</md-icon>
                    </md-button>
                  </md-list-item>
                 </md-list>
              </md-list-expand>

            </md-list-item>

            <md-list-item>
              <md-icon>send</md-icon> <span>Participating <md-chip>{{participating.length}}</md-chip></span>
              <md-list-expand>
                <md-list>
                  <md-list-item v-for="(item, index) in participating">

                    <div class="md-list-text-container">
                      <span v-on:click="select(item._links.self.href)" class="cursor">{{item.name}}</span>
                      <span>{{item.startedDate}}</span>
                    </div>

                  </md-list-item>
                 </md-list>
              </md-list-expand>
            </md-list-item>

            <md-list-item>
              <md-icon>delete</md-icon> <span>Trash</span>
            </md-list-item>

            <md-list-item>
              <md-icon>error</md-icon> <span>Error</span>

              <md-divider class="md-inset"></md-divider>
            </md-list-item>

          </md-list>
      </div>

      <div class="example-content">

        <instance-handler v-if="selectedUri && selectedUri.indexOf('instance') > -1" :uri="selectedUri" :reload.sync="reload"></instance-handler>
        <work-item-handler v-else :uri="selectedUri" :reload.sync="reload"></work-item-handler>
      </div>
    </div>
</template>
<script>
  export default {

    data () {
      return {
        location: window.location,
        reload: false,
        items: [],
        selectedUri: null,
        participating: []
      }
    },

    computed: {

    },

    methods: {
      select: function (uri) {
        this.selectedUri = uri;
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
    watch: {selectedUri: function(val){

            //alert(this.selectedUri)
                    },
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

  .component-docs {
    position: relative;
    z-index: 1;
    display: flex;

    @media (max-width: 1024px) {
      flex-direction: column;
    }
  }

  .usage-content {
    padding-right: 8px;
    flex: 1 1 20%;

    @media (max-width: 1024px) {
      padding: 0;
      flex: none;
    }
  }

  .example-content {
    padding-left: 8px;
    flex: 1 1 80%;

    @media (max-width: 1024px) {
      padding: 0;
      flex: none;
      order: 2;
    }
  }

</style>

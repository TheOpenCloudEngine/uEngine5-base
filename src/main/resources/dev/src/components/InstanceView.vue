<template>

  <div>
    <md-list class="custom-list md-triple-line md-dense">
      <md-list-item v-for="item in worklist">
        <md-avatar>
          <img src="https://placeimg.com/40/40/people/1" alt="People">
        </md-avatar>

        <div class="md-list-text-container">
          <span>{{item.title}}</span>
        </div>
      </md-list-item>
    </md-list>

    <form novalidate @submit.stop.prevent="submit">
      <md-input-container>
        <label>댓글</label>
        <md-input v-model="newTitle"></md-input>
      </md-input-container>
      <md-button @click.native="send">게시</md-button>
    </form>
  </div>

</template>

<script>
    export default {
        name: "InstanceView",

        props: {
          instanceId: String,
          serviceLocator: String
        },

        created: function(){
            loadWorklist();
        },

        data: {
          worklist:[],
            newTitle: ''
        },

        methods: {
          loadWorklist: function(){
              var me = this;

            this.$root.$refs[this.serviceLocator].invoke({
              path: "worklist/search/findByInstId" + this.instanceId,
              query: {"sort": "moddate,asc"},
              success: function(data){
                me.worklist= data._embedded.worklists;
              }
            });
          },

          send: function(){
            var me = this;

            this.$root.$refs[this.serviceLocator].invoke({
              path: "worklist",
              method: "POST",
              data: {
                instId: me.instanceId,
                title: me.newTitle

              }
            });

            loadWorkList();
          }


        }
    }
</script>

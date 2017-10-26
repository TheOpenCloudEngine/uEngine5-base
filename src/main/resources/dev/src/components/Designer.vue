<template>
  <div>
    <md-button class="md-raised md-primary" v-on:click="newProcess">New Process</md-button>

    <md-layout md-gutter="24">
      <md-layout md-flex-xsmall="100" md-flex-small="50" md-flex-medium="25" md-flex="25"
                 v-for="card in cards"
                 :key="card.name"
      >
        <md-card md-with-hover>
          <md-card-area>
            <md-card-media>
              <img :src="card.src">
            </md-card-media>

            <md-card-header>
              <div class="md-title">{{card.name}}</div>
            </md-card-header>

            <md-card-content>
              {{card.desc}}
            </md-card-content>
          </md-card-area>

          <md-card-actions>
            <md-button v-on:click="initiateProcess(card.name)">Activate</md-button>
            <md-button v-on:click="move(card.name)">Edit</md-button>
          </md-card-actions>
        </md-card>
      </md-layout>
    </md-layout>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        cards: []
      }
    },
    mounted() {
      var me = this;
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
      this.getDefinitionList();
    },
    methods: {
      getDefinitionList: function () {
        var me = this;
        this.$root.codi('definitions').get()
          .then(function (response) {
            console.log(response);
            var cards = [];
            $.each(response.data, function (i, name) {
              name = name.replace('/', '');
              cards.push({
                name: name,
                desc: name + '...',
                src: '/static/image/sample.png'
              })
            });
            me.cards = cards;
          })
      },
      newProcess: function () {
        this.$router.push({
          path: 'definition/new-process-definition'
        })
      },
      move: function (name) {
        this.$router.push({
          path: 'definition/' + name.replace('.json', '')
        })
      },
      initiateProcess: function (name) {
        var me = this;
        this.$root.codi('definition{/id}/instance').save({id: name}, {})
          .then(
            function (response) {
              var instanceId = response.data;
              me.$root.$children[0].success('프로세스가 시작되었습니다.');
              me.$router.push({
                path: '/instance/' + instanceId
              })
            },
            function (response) {
              me.$root.$children[0].error('프로세스를 시작할 수 없습니다.');
            }
          );
      },
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }
</style>

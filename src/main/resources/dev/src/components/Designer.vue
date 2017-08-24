<template>
  <v-layout row wrap>
    <v-flex
      xs3
      v-for="card in cards"
      :key="card.name"
    >
      <v-card>
        <v-card-media
          :src="card.src" height="150px">
        </v-card-media>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{card.name}}</h3>
            <div>{{card.desc}}</div>
          </div>
        </v-card-title>
        <v-card-actions>
          <v-btn v-on:click="initiateProcess(card.name)" flat class="orange--text">Activate</v-btn>
          <v-btn v-on:click="move(card.name)" flat class="orange--text">Edit</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>

    <v-btn
      absolute
      dark
      fab
      top
      right
      class="pink mt-100"
      v-on:click="newProcess"
    >
      <v-icon class="margin-top:100px">add</v-icon>
    </v-btn>
  </v-layout>
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
    methods: {
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
                path: '/sns'
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

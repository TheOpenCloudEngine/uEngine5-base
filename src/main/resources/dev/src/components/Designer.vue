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
          <v-btn flat class="orange--text">Share</v-btn>
          <v-btn v-on:click="move(card.name)" flat class="orange--text">Explore</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
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
      move: function (name) {
        this.$router.push({
          path: 'definition/' + name.replace('.json','')
        })
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">

</style>

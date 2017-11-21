<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <div class="canvas-panel">
    <opengraph>
      <template>
        <div v-for="(data, index) in chart">
          <rectangle-element
            :x="index * 100 + 100"
            :y="500"
            :width="50"
            :height="200"
            :_style="{
            animation: [
                {
                    start: {

                    },
                    to: {
                        transform: 't0,'+(data.value * -0.5)+'s1,' + data.value / 200
                    },
                    ms: 500
                },
                {
                    start: {
                        fill: 'white'
                    },
                    to: {
                        fill: 'red'
                    },
                    ms: 1000
                },
                {
                    start: {
                        fill: 'red'
                    },
                    to: {
                        fill: 'white'
                    },
                    ms: 1000,
                    delay: 1000
                }
            ],
            'animation-repeat':1,
            'fill-opacity': 1
        }"
          >
            <sub-elements>
              <text-element
                :text="data.name"
                :sub-width="'100%'"
                :sub-height="'30px'"
                :sub-algin="'center'"
                :sub-bottom="'30px'"
                :sub-style="{
                 'font-color': 'grey'
              }"
              ></text-element>
            </sub-elements>
          </rectangle-element>
        </div>
      </template>
    </opengraph>
    <md-layout>
      <md-layout></md-layout>
      <md-layout>
        <md-layout md-flex="100" v-for="(data, index) in chart">
          <md-layout md-flex="50">
            <md-input-container>
              <label>{{ index + '번 값' }}</label>
              <md-input v-model="data.value"
                        single-line></md-input>
            </md-input-container>
          </md-layout>
          <md-layout md-flex="50">
            <md-input-container>
              <label>{{ index + '번 이름' }}</label>
              <md-input v-model="data.name"
                        single-line></md-input>
            </md-input-container>
          </md-layout>
        </md-layout>
      </md-layout>
    </md-layout>
  </div>
</template>
<script>
  export default {
    props: {
      monitor: Boolean
    },
    data () {
      return {
        chart: [
          {value: 100, name: '1번'},
          {value: 300, name: '2번'},
          {value: 200, name: '3번'},
          {value: 400, name: '4번'},
          {value: 150, name: '5번'},
        ]
      }
    },
    computed: {},
    mounted() {

    },
    watch: {},
    methods: {

    }
  }
</script>

<style scoped lang="scss" rel="stylesheet/scss">
  .canvas-panel {
    top: 0px;
    bottom: 0px;
    left: 0px;
    right: 0px;
    position: absolute;
    overflow: hidden;
  }
</style>


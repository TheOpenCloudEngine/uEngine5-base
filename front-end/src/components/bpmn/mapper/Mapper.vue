<template>

  <div class="canvas-panel">
    <opengraph>
      <template>

        <div v-for="(item, index) in left">
          <text-element
            :text="item"
            :sub-width="'100%'"
            :sub-height="'30px'"
            :sub-algin="'center'"
            :sub-bottom="'30px'"
            :sub-style="{
                 'font-color': 'grey'
              }"
            :x="50"
            :y="100 + (index*20)"

          ></text-element>

          <circle-element :x="100" :y="100 + (index*20)" :width="7" :height="7"
            selectable connectable draggable movable
          >
          </circle-element>

        </div>

        <rectangle-element v-for="transformer in transformers"
                           :x="250" :y="300" :width="100"  :height="20"

          selectable connectable draggable movable resizable deletable
          v-on:click="showProperty(transformer)"

        >
          <sub-elements>

            <text-element
              :text="transformer.name"
              :sub-width="'100%'"
              :sub-height="'30px'"
              :sub-algin="'center'"
              :sub-bottom="'30px'"
              :sub-style="{
                 'font-color': 'grey'
              }"
              :x="0"
              :y="20"

            ></text-element>


          </sub-elements>


        </rectangle-element>


        <div v-for="(item, index) in right">
          <text-element
            :text="item"
            :sub-width="'100%'"
            :sub-height="'30px'"
            :sub-algin="'center'"
            :sub-bottom="'30px'"
            :sub-style="{
                 'font-color': 'grey'
              }"
            :x="550"
            :y="100 + (index*20)"

          ></text-element>

          <circle-element
            selectable connectable draggable movable
            :x="500" :y="100 + (index*20)" :width="7" :height="7"></circle-element>

        </div>

      </template>
    </opengraph>

    <md-button @click="addTransformer">ADD TRANSFORMER</md-button>

    <div v-if="editingTransformer">

      <md-input-container>
        <label>트랜스포머 유형</label>
        <md-input type="text"
                  v-model="editingTransformer.name"></md-input>
      </md-input-container>


    </div>
  </div>

</template>

<script>

  export default {

      name: 'bpmn-mapper',

      data: function(){

          return {
            left: ['a', 'b', 'c'],
            right: ['a', 'b'],
            transformers: [],
            editingTransformer: null
          }

      },

    methods: {

      addTransformer: function(){
        this.transformers.push(

          {
              name: 'SUM'

          }

        )
      },

      showProperty: function(transformer){
          this.editingTransformer = transformer;
      }

    }

  }

</script>

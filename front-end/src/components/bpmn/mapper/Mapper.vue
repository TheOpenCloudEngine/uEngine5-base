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
                             :x.sync="transformer.location.x" :y.sync="transformer.location.y" :width="100"  :height="20"

            selectable connectable draggable movable resizable deletable
            v-on:click="showProperty(transformer)"

            :text="transformer.name"
          >
            <sub-elements>

              <text-element
                :text="transformer.name"
                :sub-width="'100%'"
                :sub-height="'30px'"
                :sub-align="'center'"
                :sub-vertical-align="'middle'"
                :sub-style="{
                   'font-color': 'grey'
                }"

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

      <div style="height:300px"></div>

      <md-button @click="addTransformer">ADD TRANSFORMER</md-button>

      <div v-if="editingTransformer">

          <md-input-container>
            <label>트랜스포머 유형</label>
            <md-select
                      v-model="editingTransformer._type" @change="changeName">

              <md-subheader>Math</md-subheader>
              <md-option value="org.uengine.processdesigner.mapper.transformers.MaxTransformer">Max</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.RoundTransformer">Round</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.SumTransformer">Sum</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.FloorTransformer">Floor</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.CeilTransformer">Ceil</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.MinTransformer">Min</md-option>

              <md-subheader>String</md-subheader>
              <md-option value="org.uengine.processdesigner.mapper.transformers.ConcatTransformer">Concat</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.NumberTransformer">To Number</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.NumberFormatTransformer">String to Number Format</md-option>

              <md-subheader>Data</md-subheader>
              <md-option value="org.uengine.processdesigner.mapper.transformers.DirectValueTransformer">Direct Value</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.ReplaceTransformer">Replace</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.BeanValueTransformer">Bean Property Value</md-option>

              <md-subheader>Validation</md-subheader>
              <md-option value="org.uengine.processdesigner.mapper.transformers.NotNullValidatorTransformer">Is Not Null</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.RegularExpValidatorTransformer">RegExp Validation</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.SizeValidatorTransformer">Size Validation</md-option>
              <md-option value="org.uengine.processdesigner.mapper.transformers.SequenceGeneratorTransformer">Create Sequence</md-option>

            </md-select>

          </md-input-container>
          <md-input-container>
            <label>이름</label>
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
            transformers: [],
            editingTransformer: null
          }

      },

    props:{
      definition: Object
    },

    computed: {
      left: function(){
        var items = [];

        this.definition.processVariableDescriptors.forEach(function(variable){
            items.push(variable.name);
        });

        return items;
      },
      right: function(){
        return this.left;
      }


    },

    methods: {

      addTransformer: function(){
        this.transformers.push(

          {
              name: 'SUM',
              _type: 'org.uengine.kernel.transformers.SumTransformer',
              location:{x: 300, y: 200}

          }

        )
      },

      showProperty: function(transformer){
          this.editingTransformer = transformer;
      },

      changeName: function(){
        this.editingTransformer.name = this.editingTransformer._type;
      }

    }

  }

</script>

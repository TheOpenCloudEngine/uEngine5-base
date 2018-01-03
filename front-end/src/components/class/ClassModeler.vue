<template>

  <div class="canvas-panel">

    <md-layout>
      <md-layout md-flex="80">
        <opengraph>
          <template>

            <div v-for="theClass in classes">

              <rectangle-element
                                 :x="250" :y="300" :width="100"  :height="20"

                                 selectable connectable draggable movable resizable deletable
                                 v-on:click="showProperty(theClass)"

              >
                <sub-elements>
                  <text-element
                    :text="theClass.name"
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

              <text-element
                v-for="(attribute, index) in theClass.attributes"

                :text="'+' + attribute.name + ':' + attribute.type"
                :sub-width="'100%'"
                :sub-height="'30px'"
                :sub-algin="'center'"
                :sub-bottom="'30px'"
                :sub-style="{
                     'font-color': 'grey'
                  }"
                :x="250"
                :y="300 + index * 20"

              ></text-element>

            </div>

          </template>
        </opengraph>
      </md-layout>


      <md-layout md-flex="20">
        <md-button @click="addClass">ADD CLASS</md-button>

        <div v-if="editingClass">

          <md-input-container>
            <label>Class Name</label>
            <md-input type="text"
                      v-model="editingClass.name"></md-input>
          </md-input-container>

          <div v-for="attribute in editingClass.attributes">

            <md-input-container>
              <label>Attribute Name</label>
              <md-input type="text"
                        v-model="attribute.name"></md-input>
            </md-input-container>
            <md-input-container>
              <label>Type</label>
              <md-select type="text"
                         v-model="attribute.type">

                <md-option value="java.lang.String">String</md-option>
                <md-option value="java.lang.Long">Long</md-option>
                <md-option value="java.lang.Double">Double</md-option>
                <md-option value="java.util.Date">Date</md-option>
                <md-option value="java.lang.Boolean">Boolean</md-option>

              </md-select>
            </md-input-container>

          </div>

          <md-button @click="addAttribute">ADD ATTRIBUTE</md-button>

        </div>

      </md-layout>
    </md-layout>


  </div>

</template>

<script>

  export default {

    name: 'class-modeler',

    data: function(){

      return {
        classes: [],
        editingClass: null
      }

    },

    methods: {

      addClass: function(){
        this.classes.push(

          {
            name: 'Class',
            attributes: []
          }

        )
      },

      showProperty: function(theClass){
        this.editingClass = theClass;
      },

      addAttribute: function(){
          this.editingClass.attributes.push({
             name: 'attribute',
            type: 'java.lang.String'

          });
      }

    }

  }

</script>

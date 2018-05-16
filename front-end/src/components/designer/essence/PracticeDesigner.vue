<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <modeling-designer
     :id = "id"
     :backend = "backend"
     :elementTypes = "elementTypes"
     v-model = "value"
     elementListBeanPath = "elementList"
     relationListBeanPath = "relationList"
     relationVueComponentName = "essence-relation"
     @addElement = "addElement"
  >

    <!--template slot="canvas-contents">

      <component v-for="(element, index) in value.elementList[1]" v-if="element != null" :is="getComponentByClassName(element._type)"
                 v-model="value.elementList[1][index]" :definition="value"
      ></component>

      <div v-for="(relation, index) in value.relationList">
        <bpmn-relation v-if="relation != null" v-model="value.relationList[index]" :definition="value"></bpmn-relation>
      </div>

    </template-->

    <template slot="toolbar-contents">
      <md-button>ahahaha</md-button>

    </template>
  </modeling-designer>
</template>

<script>
  export default {
    name: 'essence-designer',
    props: {
      backend: Object,
      id: String,
    },
    data() {
      return {

        value: {
          _type: 'org.uengine.essencia.model.PracticeDefinition',
          elementList: ['java.util.ArrayList',[]],
          relationList: ['java.util.ArrayList',[]],
          name: 'New Practice',
          _selectedLocale: 'ko'
        },
        elementTypes: [
          {
            'icon': 'bpmn-icon-start-event-none',//'OG.shape.essencia.Alpha',
            'component': 'essence-alpha',
            'label': 'Alpha',
            'width': '100',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-start-event-none',//'OG.shape.essencia.Activity',
            'component': 'essence-activity',
            'label': 'Activity',
            'width': '100',
            'height': '30'
          },
          {
            'icon': 'bpmn-icon-start-event-none',//'OG.shape.essencia.Competency',
            'component': 'essence-competency',
            'label': 'Competency',
            'width': '70',
            'height': '70'
          },
        ],
      }
    },

    methods: {
      addElement : function(element){

        //this.value.elementList[1].push(element);

      }
    }

  }
</script>



<template>
  <div>
    <md-input-container>
      <label>Category</label>
      <md-select v-model="category">
        <md-option v-if="!modelingClassesOnly" value="primitive">Primitive Types</md-option>
        <md-option v-for="entry in categories" :value="entry">{{entry}}</md-option>
      </md-select>
    </md-input-container>

    <md-input-container>
      <label>Class</label>
      <md-select v-model="value">
        <md-option v-for="entry in classTypes" :value="entry.className">{{entry.displayName}}</md-option>
      </md-select>
    </md-input-container>
  </div>
</template>


<script>

  export default{

    props: {
      value: String,
      modelingClassesOnly: false
    },

    created: function(){

      var definitions = [];
      var me = this;

      window.backend.$bind("definition", definitions);

      definitions.$load().then(function(definitions) {

        if (definitions) {
          definitions.forEach(function(definition){

            if(definition.name && definition.name.indexOf(".ClassDiagram.xml") > -1){
              me.categories.push(definition.name.split(".")[0]);
            }
          });

        }

      });


    },

    watch: {
      value: function(){
          this.$emit('input', this.value);
      }

    },

    asyncComputed: {

      classTypes: function(){
        if(!this.category) return [];

        if(this.category=="primitive")
          return this.primitiveTypes
        else{

          var classDiagram = {};
          var classes = [];

          window.backend.$bind("definition/raw/" + this.category + ".ClassDiagram.json", classDiagram);
          classDiagram.$load().then(function(classDiagram){

            if(classDiagram.definition.classDefinitions[1]){
              classDiagram.definition.classDefinitions[1].forEach(function(classDefinition){
                classes.push(
                   {
                     displayName: classDefinition.name,
                     className: classDiagram.definition.name.split(".")[0]+ "#" + classDefinition.name
                   },
                )
              })

              return classes;
            }

          });

          return classes;
//          return [
//                           {
//                             displayName: 'Order',
//                             className: 'Order Management#Order'
//                           },
//                           {
//                             displayName: 'Item',
//                             className: 'Order Management#Item'
//                           }]

        }

      }

    },

    data:function(){

      return {
        category: null,
        categories: [
//          'shop',
//          'e-learning',
//          'production'
        ],
        primitiveTypes: [
          {
            displayName: '객체(미정)',
            className: 'java.lang.Object'
          },
          {
            displayName: '문자열',
            className: 'java.lang.String'
          },
          {
            displayName: '정수형',
            className: 'java.lang.Integer'
          },
          {
            displayName: '긴 정수형(Long)',
            className: 'java.lang.Long'
          },
          {
            displayName: '실수형',
            className: 'java.lang.Double'
          },
          {
            displayName: '예 아니오',
            className: 'java.lang.Boolean'
          },
          {
            displayName: '날짜',
            className: 'java.util.Date'
          },
          {
            displayName: '사용자',
            className: 'org.uengine.kernel.RoleMapping'
          },
        ]
      };
    },

  }

</script>

<template>
  <object-form ref="object-form"
               java="org.uengine.uml.model.ClassDefinition"
               :data="metadata"
               :online="true"
               :service-locator="serviceLocator"
  ></object-form>
</template>

<script>
  export default {
    name: 'ServiceLocator',

    props: {
      java: String,
      serviceLocator: Object
    },

    watch:{
      java: function(){
        var initVars = this.initForm();

        this.metadata = initVars.metadata;
      }
    },
    data: function(){

      return this.initForm();

    },

    methods: {
      getServiceHost: function(){
        if(this.serviceLocator){
          if(this.serviceLocator.host){
            return this.serviceLocator.host;
          }else if(this.$root.$refs[this.serviceLocator]){
            return this.$root.$refs[this.serviceLocator].host;
          }else{
            return this.serviceLocator;
          }

        }else{
          return "http://127.0.0.1:8080"
        }
      },

      initForm: function(){

        var classDefinition;
        {

          var xhr = new XMLHttpRequest()
          xhr.open('GET', this.getServiceHost() + "/classdefinition?className=" + this.java, false);
          xhr.setRequestHeader("access_token", localStorage['access_token']);
          xhr.onload = function () {
            var jsonData = JSON.parse(xhr.responseText)
            classDefinition = jsonData;
          }
          xhr.send();
        }


        return {
          metadata: classDefinition
        }

      },

    }

  }
</script>



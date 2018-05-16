<template>

<md-card md-with-hover  v-if="workItem">
  <md-card-header>
    <!--<md-avatar>-->
        <!--<img-->
          <!--:src="'http://iam.pas-mini.io/rest/v1/avatar?userName=' + workItem.worklist.endpoint"-->
          <!--alt="People">-->
    <!--</md-avatar>-->
    <iam-avatar :workItem="workItem"></iam-avatar>

    <div class="md-title">{{workItem.activity.name}}</div>
    <div class="md-subhead">{{workItem.activity.instruction.text}}</div>
  </md-card-header>

      <md-card-content>

        <md-tabs md-right :md-dynamic-height="false" class="md-transparent example-tabs" style="margin-top: -48px;" @change="onChangeTab">
          <md-tab class="example-content" md-label="Todo" md-active>
              {{workItem.activity.description ? workItem.activity.description.text : ""}}
            <div v-if="parameterValueDefinition">
              <object-form
                :classDefinition="parameterValueDefinition"
                :value="workItem.parameterValues"
                :metadataResolver="metadataResolver"
              ></object-form>
            </div>
          </md-tab>

          <md-tab class="code-content" md-label="Info" id="info">

            <div style="height:1000px" v-if="infoEnabled">
             <process-designer
                 :backend="backend"
                 :id="''+workItem.worklist.instId"
                 :monitor="true"
                 :rootId="''+workItem.worklist.instId">
             </process-designer>
            </div>
          </md-tab>
        </md-tabs>


      </md-card-content>


    </md-card-area>

    <md-card-actions style="margin-top: -48px;">
      <md-button @click.native="complete('COMPLETED')">완료</md-button>
      <md-button @click.native="complete('SAVED')">저장</md-button>
      <md-button>건너뛰기</md-button>
    </md-card-actions>
  </md-card>

</template>


<script>
  export default {
    props: {
      uri: String,
    },

    watch: {
      uri: function (val) {
        this.load();
      }
    },

    data: function () {

      return {
        parameterValueDefinition: null,
        workItem: null,
        classDiagram: null,
        backend: window.backend,
        infoEnabled: false
      };
    },

    created: function(){
    },

    mounted: function(){
    },

    methods: {
      load: function () {
        var me = this;
        var worklist = {}

        //alert('x')

        window.backend.$bind(this.uri, worklist);
        worklist.$load().then(function(worklist){
          worklist.workItem.$load().then(function(workItem){

            me.workItem = workItem;

            if(!me.workItem.parameterValues)
              me.workItem.parameterValues = {};

            var parameterValueDefinition = {fieldDescriptors:[]};

            if(me.workItem.activity.parameters) {
              for (var idx in me.workItem.activity.parameters) { //convert parameter map to type metadata for object-form
                var parameter = me.workItem.activity.parameters[idx];

                if (parameter.argument && parameter.argument.text && parameter.variable) {

                    if("REST" == parameter.variable.persistOption){
                      if(me.workItem.parameterValues[parameter.argument.text] && me.workItem.parameterValues[parameter.argument.text].uri){
                         var uri = me.workItem.parameterValues[parameter.argument.text].uri;

                         var getLocation = function(href) {
                             var l = document.createElement("a");
                             l.href = href;
                             return l;
                         };
                         var l = getLocation(uri);

                         var externalUri;
                         if(l.hostname.indexOf(".") > -1){ //it is full url
                          externalUri = l.pathname;
                         }else{
                          var serviceName = l.hostname;
                          externalUri = serviceName + l.pathname;
                         }


                         var xhr = new XMLHttpRequest()
                         var self = this;
                         xhr.open('GET', window.backend.$bind.ref + "/" + externalUri, false);
                         xhr.setRequestHeader("access_token", localStorage['access_token']);
                         xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
                         xhr.onload = function () {
                           var received = JSON.parse(xhr.responseText);
                           me.workItem.parameterValues[parameter.argument.text] = received;

                           var metadata = me.metadataResolver(parameter.variable.typeClassName)

                           if(metadata.fieldDescriptors)
                             metadata.fieldDescriptors.forEach(function(fd){
                               if(fd.attributes && fd.attributes['relationAnnotation']){
                                  xhr.open('GET', received._links[fd.name].href, false);
                                  xhr.onload = function(){
                                     var items = JSON.parse(xhr.responseText);
                                     received[fd.name] = items;
                                  }
                                  xhr.send();
                               }
                             })

                         }
                         xhr.send();
                      }
                    }

                    if(!parameter.variable.typeClassName) //TODO: it looks hard code
                        parameter.variable.typeClassName = "java.lang.String";

                    if(parameter.multipleInput){

                      parameterValueDefinition.fieldDescriptors.push({
                        name: parameter.argument.text,
                        displayName: parameter.argument.text,
                        collectionClass: parameter.variable.typeClassName,
                        _online: "REST" == parameter.variable.persistOption
                      });
                    }else
                    {

                      parameterValueDefinition.fieldDescriptors.push({
                        name: parameter.argument.text,
                        displayName: parameter.argument.text,
                        className: parameter.variable.name.indexOf("[roles].") == 0 ? "org.uengine.kernel.RoleMapping" : parameter.variable.typeClassName,
                        _online: "REST" == parameter.variable.persistOption
                      });

                    }

                }
              }

              me.parameterValueDefinition = parameterValueDefinition;
            }
          })

        })


      },
      complete: function (desiredState) {
        var serviceLocator = this.$root.$children[0].$refs["backend"]; //TODO hardcoded

        var me = this;

        if(me.parameterValueDefinition && me.parameterValueDefinition.fieldDescriptors){
          me.parameterValueDefinition.fieldDescriptors.forEach(function(fieldDescriptor){
            if(fieldDescriptor._online){ //REST persisted data only carries it's uri
              me.workItem.parameterValues[fieldDescriptor.name] = {
                _type: 'org.uengine.five.overriding.RestResourceProcessVariableValue',
                uri: me.workItem.parameterValues[fieldDescriptor.name]._links.self.href
              }
            }
          })
        }

        serviceLocator.invoke({
          path: this.uri.replace("worklist", "work-item"),
          method: 'POST',
          data: {
            worklist: {
              status: desiredState // 'COMPLETED' or 'SAVED'
            },
            parameterValues: this.workItem.parameterValues
          },
          success: function (value) {
            //me.workItem = null;
            me.load();

            me.$root.$children[0].success('작업을 완료했습니다.');
            me.$emit('update:reload', true);
          },
          fail: function (value) {
            me.$root.$children[0].error('Fail to complete due to ' + value);
          }

        });

      },

      onChangeTab: function(tabIndex){
        if(tabIndex == 1){
          this.infoEnabled = true;
        }else{
          this.infoEnabled = false;
        }
      },

      metadataResolver: function(className){

        var classDiagram;
        var classNameOnly = className;
        var definitionName;

        if(className.indexOf("#") > 0){
          var definitionAndClassName = className.split("#");
          definitionName = definitionAndClassName[0];
          classNameOnly = definitionAndClassName[1];

          if(this.classDiagram == null || this.classDiagram.name != definitionName){ //find the class from the current class diagram

               var result;
               var uri = (encodeURI(window.backend.$bind.ref + "/definition/raw/" + definitionName + ".ClassDiagram.json"))

               console.log("try to get class diagram: " + uri);

               var xhr = new XMLHttpRequest();
               var me = this;

               xhr.open('GET', uri, false);
               xhr.setRequestHeader("access_token", localStorage['access_token']);
               xhr.onload = function () {
                 result = JSON.parse(xhr.responseText)
               };
               xhr.send();

               this.classDiagram = result.definition;

          }

        }


        if(this.classDiagram!=null){

          //trim the class name if collection
          try{
            var listExp = "List<"

            if(classNameOnly.indexOf(listExp) > -1)
              classNameOnly = classNameOnly.substring(listExp.length, classNameOnly.length - 1);
          }catch(e){}

          for(var i in this.classDiagram.classDefinitions[1]){

            var classDefinition = this.classDiagram.classDefinitions[1][i];
            if(classDefinition.name == classNameOnly){
              classDefinition.baseUri = "backend-" + (this.classDiagram.name.replace(' ', '-') + "/" + classNameOnly + "s").toLowerCase();

              return classDefinition
            }

          }
        }

        throw "There's no Class Declaration for " + className;
      }

    }
  }
</script>


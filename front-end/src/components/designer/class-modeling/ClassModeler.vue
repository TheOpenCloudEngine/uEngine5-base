<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <modeling-designer
    :id="id"
    :backend="backend"
    :elementTypes="elementTypes"
    v-model="value"
    elementListBeanPath="classDefinitions"
    relationListBeanPath="relations"
    relationVueComponentName="class-relation"
    @addElement="addElement"
    @loadModel="onLoadModel"
    @saved="onSaved"
    ref="designer"
  >
    <template slot="toolbar-contents">
      <md-button v-if="id && !deployStatus" @click="generateRestService">
        <md-icon>flash_on</md-icon>
        Generate REST service
      </md-button>
      <md-button v-if="id && deployStatus" @click="showRestServiceProgress">
        <md-spinner v-if="'view app'!=deployStatus" :md-size="20" md-indeterminate class="md-accent"></md-spinner>
        <md-icon v-else>exit_to_app</md-icon>
        {{deployStatus}}
      </md-button>

      <div v-if="projectUrl">
        <div v-if="todoCount==0">

          <md-speed-dial md-open="hover" md-direction="down" class="md-fab-top-right" md-theme="light-blue">
            <md-button class="md-fab" md-fab-trigger>
              <md-tooltip md-direction="left">Round-trip Engineering</md-tooltip>
              <md-icon md-icon-morph>compare_arrows</md-icon>
              <md-icon>repeat</md-icon>
            </md-button>

            <md-button class="md-fab md-primary md-mini md-clean"
                       @click="updateRestService({updateToCode: true, updateFromCode: true})">
              <md-tooltip md-direction="left">Sync with code</md-tooltip>
              <md-icon>repeat</md-icon>
            </md-button>

            <md-button class="md-fab md-primary md-mini md-clean"
                       @click="updateRestService({updateToCode: true, updateFromCode: false})">
              <md-tooltip md-direction="left">Update to code</md-tooltip>
              <md-icon>skip_next</md-icon>
            </md-button>

            <md-button class="md-fab md-primary md-mini md-clean"
                       @click="updateRestService({updateToCode: false, updateFromCode: true})">
              <md-tooltip md-direction="left">Update from code</md-tooltip>
              <md-icon>skip_previous</md-icon>
            </md-button>

          </md-speed-dial>
        </div>
        <md-button v-else>
          <md-spinner :md-size="20" md-indeterminate class="md-accent"></md-spinner>
          working..{{todoCount}}
        </md-button>
      </div>

    </template>
  </modeling-designer>
</template>

<script>
  export default {
    name: 'class-designer',
    props: {
      id: String,
      backend: Object,
      value: {
        default: function () {
          return {
            _type: 'org.uengine.uml.ClassDiagram',
            classDefinitions: ['java.util.ArrayList', []],
            relations: ['java.util.ArrayList', []],
            name: 'New Class Model',
            _selectedLocale: 'ko'
          }
        },
        type: Object
      }
    },
    data() {
      return {
        elementTypes: [
          {
            'icon': 'bpmn-icon-start-event-none',//'OG.shape.essencia.Alpha',
            'component': 'class-definition',
            'label': 'Class',
            'width': '100',
            'height': '100'
          }
        ],
        deployStatus: null,
        projectId: null,
        projectUrl: null,
        codeGenerating: 0,
        todoCount: 0
      }
    },

    created: function () {


    },

    methods: {
      addElement: function (element) {

      },
      onLoadModel: function (value) {
        if (this.value.classDefinitions[1]) {
          this.value.classDefinitions[1].forEach(function (classDefinition) {
            if (!classDefinition._type)
              classDefinition._type = "org.uengine.uml.model.ClassDefinition"
          })
          this.id = value.id;
          this.refreshDeployStatus();
        }
      },

      refreshDeployStatus: function () {
        var me = this;
        me.$root.codi('create-app/' + me.value.id + ".json").get()
          .then(
            function (response) {
              if (response.data.createStatus) {
                me.deployStatus = response.data.createStatus;
                me.projectId = response.data.projectId;

                if (!me.projectUrl)
                  me.$root.codi('gitlab/projects/' + me.projectId).get()
                    .then(function (response) {

                        me.projectUrl = response.data.web_url;

                        //alert( me.projectUrl);
                      }
                    );
                if (me.deployStatus == 'repository-create-success') {
                  me.deployStatus = 'view app';
                  return;//stop polling
                }
              }
              setTimeout(me.refreshDeployStatus, 2000);
            },

            function (response) {
              me.deployStatus = null;
            },
          );
      },

      findClassDefinition: function (name) {
        var me = this;
        if (me.value.classDefinitions[1]) {

          for (var i in me.value.classDefinitions[1]) {
            var classDefinition = me.value.classDefinitions[1][i];

            if (classDefinition.name == name || (classDefinition.elementView && classDefinition.elementView.id == name)) {

              // arrange class definition
              for (var j in classDefinition.fieldDescriptors) {
                var fd = classDefinition.fieldDescriptors[j];

                if (fd.attributes && fd.attributes.isKey == "true") {
                  classDefinition.keyFieldDescriptor = fd;
                }
              }

              return classDefinition;

            }

          }
        }

        return null;
      },

      rearrangeClassHierarchy: function () {
        var me = this;

        if (me.value.relations[1]) {
          me.value.relations[1].forEach(function (relation) {

            var sourceClass = me.findClassDefinition(relation.relationView.from);
            var targetClass = me.findClassDefinition(relation.relationView.to);

            if (sourceClass)
              relation.sourceClassName = sourceClass.name;

            if (targetClass)
              relation.targetClassName = targetClass.name;

          });

          me.value.relations[1].forEach(function (relation) {

            if (relation._type == "org.uengine.uml.model.Generalization") {
              var derivedClass = me.findClassDefinition(relation.targetClassName);

              if (derivedClass) {
                derivedClass.superClasses = ['java.util.ArrayList', [relation.sourceClassName]];
              }

              if (!derivedClass.keyFieldDescriptor) {
                derivedClass.keyFieldDescriptor = me.findClassDefinition(relation.sourceClassName).keyFieldDescriptor;
              }
            }

          });

        }
      },

      generateRestService: function (options) {
        var me = this;

        me.rearrangeClassHierarchy();
        me.$root.codi('create-app/' + me.id).save()
          .then(function (response) {

              me.$root.$children[0].success('REST 서비스를 생성 요청하였습니다: http://localhost:8081/#/apps/detail/' + me.value.name + '/dashboard');
              me.deployStatus = "Requested";

              me.refreshDeployStatus();

            },

            function (response) {
              var message;

              try {
                message = JSON.parse(response.data.message.split("content:")[1]).message
              } catch (e) {
              }

              if (!message) try {
                message = response.data.message
              } catch (e) {
              }

              if (!message) try {
                message = response.data
              } catch (e) {
              }


              me.$root.$children[0].error('Failed due to: ' + message);

              me.refreshDeployStatus();

            });
      },

      err:

        function (response) {
          var me = this;

          var message;

          try {
            message = JSON.parse(response.data.message.split("content:")[1]).message
          } catch (e) {
          }

          if (!message) try {
            message = response.data.message
          } catch (e) {
          }

          if (!message) try {
            message = response.data
          } catch (e) {
          }

          if (!message) message = response.status;


          me.$root.$children[0].error('Failed to commit code changes to the Gitlab: ' + message);
          me.todoCount--;

        },


      updateRestService: function (options) {
        var me = this;

        me.rearrangeClassHierarchy();

        var toBeReverseEngineered = this.value.classDefinitions[1]

        //alert(JSON.stringify(toBeReverseEngineered));
        //find newly added java classes
        if (options.updateFromCode) {

          me.$root.codi('gitlab/projects/' + me.projectId + '/repository/tree?path=src/main/java/hello').get()
            .then(function (response) {

              var files = response.data;

              var classNames = {};
              me.value.classDefinitions[1].forEach(function (classDefinition) {
                  if (classDefinition)
                    classNames[classDefinition.name] = classDefinition
                }
              );

              if (files) {
                var i = 0;

                files.forEach(function (file) {
                  if (file.name.indexOf(".java") > 1) {

                    var className = file.name.split(".java")[0];

                    //newly found class from the code side
                    if (!classNames[className]) {

                      me.$root.codi('java-reverse/java?sourceUrl=' + me.projectUrl + '/raw/master/' + file.path).save(
                        {
                          name: className,
                          fieldDescriptors: [],
                          "elementView": {
                            "height": 100.0,
                            "style": "{\"stroke\":\"black\",\"fill-r\":1,\"fill-cx\":0.1,\"fill-cy\":0.1,\"fill\":\"#FFFFFF\",\"fill-opacity\":0,\"label-position\":\"center\",\"stroke-width\":1.2,\"r\":\"1\",\"cursor\":\"move\"}",
                            "width": 100.0,
                            "x": 500.0 + (i++) * 100,
                            "y": 500.0
                          },
                        }
                      )
                        .then(function (response) {
                            if (response.data && response.data.classDefinition) { //only if the class definition is an Entity (marked as @Entity)
                              //alert(response.data.classDefinition.name);
                              toBeReverseEngineered.push(response.data.classDefinition);
                            }
                          },


                          me.err
                        );


                    }
                  }
                });
              }

            });


        }

        me.todoCount = toBeReverseEngineered.length + 1;

        var newList = [];
        //remove nulls
        toBeReverseEngineered.forEach(function (classDefinition) {
          if (!classDefinition) { //ignore null
            me.todoCount--;
          } else {
            newList.push(classDefinition)
          }
        });

        me.value.classDefinitions[1] = newList;
        toBeReverseEngineered = newList;

        var newClassDefinitions = {};

        //alert("toBeReverseEngineered.count=" + toBeReverseEngineered.length)

        var failed = false;

        toBeReverseEngineered.forEach(function (classDefinition) {

          if (!classDefinition) return;

          me.$root.codi('java-reverse/java?sourceUrl=' + me.projectUrl + '/raw/master/src/main/java/hello/' + (classDefinition.javaClassName ? classDefinition.javaClassName : classDefinition.name) + '.java').save(classDefinition)
            .then(function (response) {
                me.$root.$children[0].success('Source code has been generated: ' + classDefinition.name);

                //validate response first
                if (!response.data.classDefinition) {
                  //alert(JSON.stringify(response.data));

                  failed = true;
                } else {
                  var index = me.value.classDefinitions[1].indexOf(classDefinition)
                  if (response.data.classDefinition) {
                    if (options.updateToCode) {
                      response.data.classDefinition._actions = response.data.actions;
                      classDefinition._actions = response.data.actions;
                      // alert(response.data.actions)
                    }

                    if (options.updateFromCode) {
                      //me.value.classDefinitions[1][index] = response.data.classDefinition;
                      newClassDefinitions[response.data.classDefinition.name] = response.data.classDefinition;
                    }


                  } else { // the class is not an Entity!
                    //console.log("deleted: " + index + " class:" + classDefinition.name)
                    if (options.updateFromCode) {
                      me.value.classDefinitions[1].splice(index, 1);
                    }
                  }


                }

                me.todoCount--;

                if (me.todoCount == 1) {
                  me.$emit("input", me.value);

                  if (failed) {
                    me.todoCount = 0;
                    me.$root.$children[0].error("Some of classes has been not properly analyzed.");

                    return;
                  }

                  if (!options.updateToCode) {
                    me.todoCount = 0;

                    me.$root.$children[0].success('Class Diagram has been updated from source code (Reverse Engineered)');

                    //location.reload()
                    //me.$refs["designer"].save();

                    return;
                  }

                  me.classDefinitions = newClassDefinitions;


                  var commitMessage = prompt("Enter commit message");

                  if (commitMessage == null) {
                    me.todoCount = 0;
                    return;
                  }

                  var commit = {
                    "branch": "master",
                    "commit_message": commitMessage ? commitMessage : "empty commit message",
                    "actions": []
                  };

                  toBeReverseEngineered.forEach(function (classDefinition) {
                    if (!classDefinition || !classDefinition._actions[1]) return;

                    classDefinition._actions[1].forEach(function (action) {

                      // alert(JSON.stringify(action))
                      commit.actions.push({

                        "action": action.action,
                        "file_path": "src/main/java/hello/" + action.fileName,
                        "content": action.sourceCode,
                        "previous_path": "src/main/java/hello/" + action.previousFileName
                      })

                    })

                  });


                  me.$root.codi("gitlab/projects/" + me.projectId + "/repository/commits").save(commit).then(
                    function () {
                      me.$root.$children[0].success('Successfully Synchronized.');
                      me.todoCount--;
                    },
                    me.err
                  );


                }
              },

              me.err
            );
        })
      },

      showRestServiceProgress: function () {
        window.open("http://cloud.pas-mini.io/#/apps/detail/" + this.getProjectName() + "/dashboard", "_blank");
      },

      getProjectName: function () {
        return "backend-" + this.value.name.toLowerCase().replace(' ', '-');
      },

      onSaved: function (id) {
        this.id = id;
      }

    }

  }
</script>



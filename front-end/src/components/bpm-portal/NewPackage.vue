<template>
  <md-dialog
    md-open-from="#newPackage" md-close-to="#newPackage" ref="newPackage">
    <md-dialog-title>New Package</md-dialog-title>

    <md-dialog-content>
      <div>
        <md-input-container>
          <label>Package Name</label>
          <md-input v-model="packageName" type="text"></md-input>
        </md-input-container>
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="createPackage">Save</md-button>
      <md-button class="md-primary" @click="closePackage">Close</md-button>
    </md-dialog-actions>
  </md-dialog>
</template>

<script>
  export default {
    props: {
      currentPath : String,
      directory: Array,
      cards: Array
    },

    created: function () {

    },

    data: function () {
      return {
        packageName: ""
      };
    },
    mounted: function () {},
    methods: {
      closePackage(ref) {
        this.$refs['newPackage'].close();
      },
      openPackage(ref) {
        this.$refs['newPackage'].open();
      },
      createPackage: function () {
        var me = this;
        var path = me.currentPath;
        if(path !== "") path += "/";
        this.$root.codi('definition').save({"name" : path + me.packageName,
          "directory" : true
        }).then(
            function (response) {
              me.$root.$children[0].success('저장되었습니다.');
              var current = me.currentPath;
              current=current.substring(0, current.length-1);
              me.getDefinitionList(current);
            },
            function (response) {
              me.$root.$children[0].error('저장할 수 없습니다.');
            }
          );
        this.$refs['newPackage'].close();
      },
      getDefinitionList: function (_folder) {
        var me = this;

        var access_token = localStorage["access_token"];
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var backend = hybind(serviceLocator.getServiceHost(), {headers:{'access_token': access_token}});

        var definitions = [];
        var url = "definition/" + _folder;

        backend.$bind(url, definitions);

        var cards = [];
        var folders = [];

        definitions.$load().then(function(definitions) {

          if (definitions) {

            definitions.forEach(function (definition) {
              if (definition.directory) {
                folders.push(definition);
              }else{

                cards.push(definition);

                definition.desc=name + '...';
                definition.src = location.pathname + ((location.pathname == '/' || location.pathname.lastIndexOf('/') > 0) ? '' : '/') + 'static/image/sample.png';

              }

            });

          }
        });

        this.$emit('update:directory', folders);
        this.$emit('update:cards',cards);
      }
    }
  }
</script>


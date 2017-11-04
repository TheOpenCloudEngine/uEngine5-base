<template>
  <md-dialog
    md-open-from="#movePackage" md-close-to="#movePackage" ref="movePackage">
    <md-dialog-title>New Package</md-dialog-title>

    <md-dialog-content>
      <div>
        <md-input-container>
          <label>Package Name</label>
          <md-select name="movie" id="movie" v-model="targetPath">
            <md-option v-for="definition in rowData" :value="definition.name">{{definition.name}}</md-option>
          </md-select>
        </md-input-container>
      </div>
    </md-dialog-content>

    <md-dialog-actions>
      <md-button class="md-primary" @click="movePackage">Save</md-button>
      <md-button class="md-primary" @click="closePackage">Close</md-button>
    </md-dialog-actions>
  </md-dialog>
</template>

<script>
  export default {
    props: {
      processName: ""
    },

    created: function () {

    },

    data: function () {
      return {
        targetPath: "",
        rowData: [],
        packagePath: "",
        filePath: "",
        src: ""
      };
    },
    watch: {
      processName: function (after) {
        console.log('editing mode changed');
        if(after) {
          this.settingData(after);
          this.loadData();
        }
      }
    },
    mounted: function () {},
    methods: {
      settingData: function(_processName) {
        var me = this;
        var lastSlash = _processName.lastIndexOf('/') + 1;
        var length = _processName.length;
        me.filePath = _processName.substring(lastSlash, length);
        if(lastSlash > 0) {
          me.packagePath = _processName.substring(0, lastSlash-1);
          me.src = '/definitions/packages/' + me.packagePath + '/processes/' + me.filePath;
        } else {
          me.packagePath = "";
          me.src = '/definitions/processes/' + me.filePath;
        }
      },
      loadData: function () {
        var me = this;
        this.$root.codi('definitions').get()
          .then(function (response) {
            me.rowData = response.data;
            var definitions = [];
            $.each(response.data, function (i, definition) {
              var length = definition.length;
              var lastDot = definition.lastIndexOf('.') + 1;
              var fileName  = definition.substring(lastDot, length);
              definition = definition.replace('/', '');
              if(fileName !== "json" && definition !== me.packagePath) {
                definitions.push({
                  name: definition
                })
              }
            });
            me.rowData = definitions;
          })
      },
      closePackage(ref) {
        this.$refs['movePackage'].close();
      },
      openPackage(ref) {
        this.$refs['movePackage'].open();
      },
      renamePackage(ref) {
        this.$refs['movePackage'].open();
      },
      movePackage: function () {
        var me = this;
        this.$root.codi(me.src).save({"targetPath" : me.targetPath})
          .then(
            function (response) {
              me.$root.$children[0].success('이동되었습니다.');
            },
            function (response) {
              me.$root.$children[0].error('이동할 수 없습니다.');
            }
          );
        this.$refs['movePackage'].close();
      }
    }
  }
</script>


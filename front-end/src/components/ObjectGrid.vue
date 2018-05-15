<template>
  <div>

    <!-- 삭제 확인 팝업 -->
    <md-dialog-confirm
      :md-title="dc.title"
      :md-content-html="dc.contentHtml"
      :md-ok-text="dc.ok"
      :md-cancel-text="dc.cancel"
      @open="onOpen"
      @close="onClose"
      ref="deleteDialog">
    </md-dialog-confirm>

    <md-table-card>

      <!-- Toolbar -->
      <md-toolbar v-if="options_ && options_.toolbar">
        <h1 class="md-title">{{metadata.displayName}}</h1>
        <md-button class="md-icon-button">
          <md-icon>filter_list</md-icon>
        </md-button>
        <md-button class="md-icon-button">
          <md-icon>search</md-icon>
        </md-button>
      </md-toolbar>

      <!-- header -->
      <md-table-alternate-header md-selected-label="selected">
        <md-button class="md-icon-button" @click.native="openDialog('deleteDialog')">
          <md-icon>delete</md-icon>
        </md-button>
      </md-table-alternate-header>

      <!-- 목록 -->
      <md-table md-sort="dessert" md-sort-type="desc" @select="onSelect" @sort="onSort">

        <!-- table header -->
        <md-table-header v-if="primitiveType">
          <md-table-row>
            <md-table-head>
              {{ dataLabel ? dataLabel : metadata.displayName}}
            </md-table-head>
          </md-table-row>
        </md-table-header>
        <md-table-header v-else>
          <md-table-row>
            <md-table-head v-for="key in columns" :md-sort-by="key.name">
              {{ key.displayName | capitalize }}
            </md-table-head>
          </md-table-row>
        </md-table-header>

        <md-table-body v-if="primitiveType">
          <md-table-row v-for="(entry, rowIndex) in value" :key="rowIndex" :md-item="entry" md-selection>
            <md-table-cell>
              <span v-if="!options_.editable">{{ value[rowIndex] }}</span>
              <input v-if="options_.editable" v-model="value[rowIndex]"></input>
            </md-table-cell>
          </md-table-row>
        </md-table-body>

        <md-table-body v-else>
          <md-table-row v-for="(entry, rowIndex) in value" :key="rowIndex" :md-item="entry" @dblclick.native="onEdit(entry, rowIndex)" md-selection>
            <md-table-cell v-for="key in columns">
              <span v-if="!options_.editable">{{ showValue(key, entry) }}</span>
              <component v-if="options_.editable && key.component" :is="key.component" :v-model="value[rowIndex][key.name]"
                         :java="key.elemClassName" :full-fledged="true" :options="options[key.name]"></component>
              <input v-if="options_.editable && !key.component" v-model="value[rowIndex][key.name]"></input>
            </md-table-cell>
          </md-table-row>

          <md-dialog md-open-from="#fab" md-close-to="#fab" ref="updateDialog">
            <md-dialog-title>Edit</md-dialog-title>
            <md-dialog-content>
              <object-form v-if="updateDialogEnabled" ref="object-form"
                           :java="java"
                           v-model="formData"
                           :backend = "backend"
                           :metadataResolver = "metadataResolver"
                           :online = "online"
              >
              </object-form>
            </md-dialog-content>
            <md-dialog-actions>
              <md-button class="md-primary" @click.native="$refs['updateDialog'].close()">Close</md-button>
            </md-dialog-actions>
          </md-dialog>

        </md-table-body>

      </md-table>

      <md-table-pagination
        v-if="options_ && options_.pagination"
        md-size="5"
        md-total="1000"
        md-page="1"
        md-label="Rows"
        md-separator="of"
        :md-page-options="[5, 10, 25, 50]"
        @pagination="onPagination">
      </md-table-pagination>

    </md-table-card>

    <div v-if="fullFledged">
      <md-button class="md-primary" @click.native="newForm"><md-icon>add</md-icon> Add</md-button>
      <md-dialog md-open-from="#fab" md-close-to="#fab" ref="newDialog">
        <md-dialog-title>New</md-dialog-title>
        <md-dialog-content>
          <object-form v-if="addDialogEnabled" ref="object-form"
                       :java="java"
                       v-model="formData"
                       @added="onAdded"
                       :backend="backend"
                       :metadataResolver = "metadataResolver"
                       :online = "online"
          >
          </object-form>
        </md-dialog-content>
        <md-dialog-actions>
          <md-button class="md-primary" @click.native="addObject($refs['object-form'].value); $refs['newDialog'].close()"> enter</md-button>
          <md-button class="md-primary" @click.native="$refs['newDialog'].close()"> Close</md-button>
        </md-dialog-actions>
      </md-dialog>
    </div>

  </div>

</template>

<script>

  export default {
    props: {
      value: Array,
      filterKey: String,
      java: String,
      columnChanger: Object,
      fullFledged: Boolean,
      online: Boolean,
      options: Object,
      dataLabel: String,
      backend: Object,
      metadataResolver: Function
    },

    data: function () {
      let initGrid = this.initGrid();
      initGrid.formData = {
      };

      initGrid.selectedIndex = 0;
      initGrid.updateDialogEnabled = false;
      initGrid.addDialogEnabled = false;

      return initGrid;
    },

    watch: {
      java: function () {
        var initProps = this.initGrid();
        this.columns = initProps.columns;
        this.metadata = initProps.metadata;
      },
      value: {
        handler: function(){
          this.$emit('input', this.value);
        },
        deep: true
      }
    },

    created: function () {
      if(this.options) this.online = (this.options.online)

      if(!this.value){
        this.value = [];
        this.$emit("input", this.value)
      }
      this.dc = {
        title: '데이터 삭제',
        contentHtml: '해당 데이터를 삭제 하시겠습니까?',
        cancel: 'No',
        ok: 'Yes',
      };
      this.loadData();
    },

    computed: {
      filteredData: function () {
        //var data = this.rowData
        return this.rowData
      },
      primitiveType: function() {
        if(this.java && this.java.indexOf("java.lang.") == 0) {
          return true;
        }
        return false;
      }
    },

    filters: {
      capitalize: function (str) {
        return str.charAt(0).toUpperCase() + str.slice(1)
      }
    },

    methods: {
      initGrid: function () {
        var xhr = new XMLHttpRequest();
        var columns = [];
        var self = this;
        var metadata;
        var thisOptions = this.options;
        if (!thisOptions) {
          thisOptions = {};
        }

        if(this.metadataResolver){
          metadata = this.metadataResolver(this.java, this);

        }else{
          xhr.open('GET', this.backend.$bind.ref + "/classdefinition?className=" + this.java, false);
          xhr.setRequestHeader("access_token", localStorage['access_token']);
          xhr.onload = function () {
            metadata = JSON.parse(xhr.responseText)

          };
          xhr.send();
        }

        columns = metadata.fieldDescriptors;

        for (var i = 0; i < columns.length; i++) {
          var fd = columns[i];
          if (!fd.displayName) {
            fd.displayName = fd.name;
          }
          if (fd.options && fd.values) {
            fd.optionMap = {};
            for (var keyIdx in fd.options) {
              var key = fd.options[keyIdx];
              fd.optionMap[key] = fd.values[keyIdx];
            }
            thisOptions[fd.name] = fd.optionMap;
          } else {
            thisOptions[fd.name] = {};
          }
          if (fd.attributes && fd.attributes['hidden']) {
            columns.splice(i, 1);
            i--;
          } else if (fd.optionMap && fd.optionMap['vue-component'] && Vue.options.components[fd.optionMap['vue-component']]) {
            fd.component = fd.optionMap['vue-component'];
          } else if (fd.className == "long" || fd.className == "java.lang.Long" || fd.className == "java.lang.Integer") {
            fd.type = "number";
          } else if (fd.className == "java.util.Date" || fd.className == "java.util.Calendar") {
            fd.type = "date";
          } else if (fd.className.indexOf('[L') == 0 && fd.className.indexOf(";") > 1) {
            fd.component = "object-grid"
            fd.elemClassName = fd.className.substring(2, fd.className.length - 1);
            thisOptions[fd.name]['editable'] = true;
          } else if (fd.collectionClass) {
            fd.component = "object-grid"
            fd.elemClassName = fd.collectionClass;
            thisOptions[fd.name]['editable'] = true;
          }


        }
        if (self.columnChanger) {
          self.columnChanger(columns);
        }

        console.log(columns)

        return {
          columns: columns,
          metadata: metadata,
          options_: (thisOptions ? thisOptions : {}),
          pagination: {page: 1, size: 20},
          sort: null,
          selected: null,
          selectedLength: null,
          selectedClass: null
        };
      },

      onPagination: function (pagination) {
        this.pagination = pagination;
        this.loadData();
      },

      onSort: function (sort) {
        this.sort = sort;
        this.loadData();
      },

      loadData: function () {
        if (this.online) {
          var page = this.pagination.page;
          var size = this.pagination.size;
          var pathElements = this.java.split(".");
          var path = pathElements[pathElements.length - 1].toLowerCase();
          var xhr = new XMLHttpRequest()
          var self = this
          xhr.open('GET', this.backend.$bind.ref + "/" + path + "?page=" + (page - 1) + "&size=" + size + (this.sort ? "&sort=" + this.sort.name + "," + this.sort.type : ""), false);
          xhr.setRequestHeader("access_token", localStorage['access_token']);
          xhr.onload = function () {
            var jsonData = JSON.parse(xhr.responseText);
            self.value = jsonData._embedded[path];
            for (var i in self.value) {
              var row = self.value[i];
              if (row && row._links && row._links.tenantProperties) {
                var tenantPropertiesURI = row._links.tenantProperties.href;
                var xhr_ = new XMLHttpRequest()
                xhr_.open('GET', tenantPropertiesURI, true);
                xhr_.setRequestHeader("access_token", localStorage['access_token']);
                xhr_.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
                xhr_.onload = function () {
                  if (xhr_.responseText && xhr_.responseText.trim().length > 0) {
                    var jsonData = JSON.parse(xhr_.responseText);
                    if (jsonData.json) { //TODO: couchbase specific
                      jsonData = jsonData.json;
                    }
                    if (jsonData && self.metadata) {
                      for (var j in self.metadata.fieldDescriptors) {
                        var fd = self.metadata.fieldDescriptors[j];
                        if (fd.attributes && fd.attributes.extended) {
                          Vue.set(row, fd.name, jsonData[fd.name]);
                        }
                      }
                    }
                  }
                }
                xhr_.send(); //TODO: must be reduced for only the tenant properties
              }
            }
          }
          xhr.send();
        }
      },

      sortBy: function (key) {
        this.sortKey = key
        this.sortOrders[key] = this.sortOrders[key] * -1
      },

//      addRow: function (aRow) {
//        if (!this.value) this.rowData = [];
//        this.value.push(aRow);
//        /// this.$emit('update:data', this.rowData);
//      },

      showValue: function (key, entry) {
        if (key.computed) {
          return key.computed(entry);
        } else
          return entry[key.name];
      },

      onAdded: function (data) {
        this.addObject(data);
      },

      addObject: function (aRow) {
        if(this.primitiveType) aRow = aRow.value; //TODO: not a good manner
        //Variables 안에 추가하는 변수와 같은 이름이 있는지 체크한다.

        if(JSON.stringify(this.value) == "{}") this.value = [];

        this.value.push(aRow);

        this.$emit('input', this.value);
      },

      submit_for_delete: function (uri, num) {
        var path = 'product';
        var xhr = new XMLHttpRequest()
        var self = this
        xhr.open('DELETE', uri, false);
        xhr.setRequestHeader("access_token", localStorage['access_token']);
        xhr.onload = function () {
          console.log(xhr);
        };
        xhr.send();
      },

      changeObject: function (aRow) {
//        var idx = this.selectedIndex;
//        var rowData = this.value;
//        rowData[idx] = aRow;

        this.$emit('input', this.value);

        // 배열 속성 변경이 정상적으로 이루어지지 않아 초기화하고 다시 세팅한다.
//        this.rowData = [];
//        this.rowData = rowData;
//        this.changedData;
      },

      openDialog: function (ref) {
        this.$refs[ref].open();
      },

      closeDialog: function (ref) {
        this.$refs[ref].close();
      },

      onOpen: function () {
        console.log('Opened');
      },

      onClose: function (type) {
        if (type == 'ok' && this.online) {
          this.deleteSubmit();
        } else {
          this.deleteSelectedRows();
        }
        console.log('Closed', type);
      },

      onSelect: function (selected) {
        this.selected = selected;
      },

      onEdit: function (entry, rowIndex) {
//        this.selectedIndex = rowIndex;
        this.formData = this.value[rowIndex];
        this.updateDialogEnabled = true;
        this.$refs['updateDialog'].open();
      },

      deleteSubmit: function () {
        for (var i in this.selected) {
          //var primaryKey = (this.selected[i][this.metadata.keyFieldDescriptor.name]);
          this.submit_for_delete(this.selected[i]._links.self.href);
        }
        this.loadData();
        //this.$emit('update:data', this.rowData);
      },

      deleteSelectedRows: function () {
        var count = 0;
        for (var i in this.selected) {
          var where = this.value.indexOf(this.selected[i]);
          this.value.splice(where - count, 1);
          count++;
        }
        this.loadData();
        //this.$emit('update:data', this.rowData);
      },

      newForm: function () {
        var me = this;

        me.addDialogEnabled = true;

        me.$nextTick(function(){
          me.formData = {
            "typeClassName" : "java.lang.String"
          };
          me.$refs['newDialog'].open();
        });


      }
    }
  }
</script>

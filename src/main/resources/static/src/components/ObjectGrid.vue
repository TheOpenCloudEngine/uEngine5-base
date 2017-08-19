<template>
    <div>
     <md-dialog-confirm
              :md-title="dc.title"
              :md-content-html="dc.contentHtml"
              :md-ok-text="dc.ok"
              :md-cancel-text="dc.cancel"
              @open="onOpen"
              @close="onClose"
              ref="dialog5">
    </md-dialog-confirm>
    <md-dialog-alert
            md-content="한개 선택 후, 수정해주시기 바랍니다."
            md-ok-text="확인"
            @open="onOpen"
            @close="onClose"
            ref="dialog3">
    </md-dialog-alert>

        <md-table-card>
            <md-toolbar v-if="options_.toolbar">
                <h1 class="md-title">{{metadata.displayName}}</h1>
                <md-button class="md-icon-button">
                    <md-icon>filter_list</md-icon>
                </md-button>

                <md-button class="md-icon-button">
                    <md-icon>search</md-icon>
                </md-button>
            </md-toolbar>

            <md-table-alternate-header md-selected-label="selected">
                <md-button class="md-icon-button" @click.native="openDialog('dialog5')">
                    <md-icon>delete</md-icon>
                </md-button>

    <!-- @click.native="$refs['dialog'].open()"-->
                <!--
                <md-button class="md-icon-button" @click.native="duplicationCheck()">
                    <md-icon>update</md-icon>
                </md-button>
                -->

                <md-button class="md-icon-button">
                    <md-icon>more_vert</md-icon>
                </md-button>
            </md-table-alternate-header>

            <md-table md-sort="dessert" md-sort-type="desc" @select="onSelect" @sort="onSort" >
                <md-table-header>
                    <md-table-row>
                        <md-table-head v-for="key in columns"
                                       :md-sort-by="key.name">
                            {{ key.displayName | capitalize }}
                        </md-table-head>
                    </md-table-row>
                </md-table-header>

                <md-table-body>
                    <md-table-row v-for="(entry, rowIndex) in filteredData" :key="rowIndex" :md-item="entry" md-selection v-on:dblclick.native = "doubleClickEvent(entry)">
                        <md-table-cell v-for="key in columns">
                            <span v-if="!options_.editable">{{ showValue(key, entry) }}</span>

                            <component v-if="options_.editable && key.component" :is="key.component" :data="entry[key.name]" :java="key.elemClassName" :full-fledged="true" :options="options[key.name]"></component>

                            <input v-if="options_.editable && !key.component" v-model="entry[key.name]"></input>
                        </md-table-cell>
                    </md-table-row>
                </md-table-body>
            </md-table>

            <md-table-pagination
                    v-if="options_.pagination"
                    md-size="5"
                    md-total="1000"
                    md-page="1"
                    md-label="Rows"
                    md-separator="of"
                    :md-page-options="[5, 10, 25, 50]"
                    @pagination="onPagination"></md-table-pagination>
        </md-table-card>

        <md-dialog md-open-from="#fab" md-close-to="#fab" ref="dialog">
            <md-dialog-title>제품 마스터 생성</md-dialog-title>

            <md-dialog-content>
                <object-form ref="object-form"
                        :java="java"
                        :data = "checked"
                        :event-listeners = "['grid']"
                >
                </object-form>
            </md-dialog-content>

            <md-dialog-actions>
                <md-button class="md-primary" @click.native="$refs['object-form'].update_(); $refs['dialog'].close()">수정</md-button>
                <md-button class="md-primary" @click.native="$refs['dialog'].close()">닫기</md-button>
            </md-dialog-actions>
        </md-dialog>
    </div>
</template>
<script>

    export default {
        props: {
            data: Array,
            // columns: Array,
            filterKey: String,
            java: String,
            columnChanger: Object,
            fullFledged: Boolean,
            online: Boolean,
            options: Object,
            checked: Array
        },

        data: function () {
            return this.initGrid();
        },

    watch: {
        java: function(){
            var initProps = this.initGrid();

            this.columns = initProps.columns;
            this.metadata = initProps.metadata;

        },
        options: function(val){
            var initProps = this.initGrid();

            this.columns = initProps.columns;
            this.metadata = initProps.metadata;
        }
    },

    created: function () {
        this.dc = {
            title: '데이터 삭제',
            contentHtml: '해당 데이터를 삭제 하시겠습니까?',
            cancel: 'No',
            ok: 'Yes',
        },
        this.alert ={
             content: '중복 체크는 수정 할 수가 없습니다.',
             ok: 'Cool!'
        }

        this.loadData();

    },
    computed: {
        filteredData: function () {
            var data = this.rowData

            return data
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


            xhr.open('GET', "http://localhost:8080/classdefinition?className=" + this.java, false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.onload = function () {
                metadata = JSON.parse(xhr.responseText)

                columns = metadata.fieldDescriptors;
                self.options = {};

                for (var i = 0; i < columns.length; i++) {
                    var fd = columns[i];

                    if (fd.options && fd.values) {
                        fd.optionMap = {};
                        for (var keyIdx in fd.options) {
                            var key = fd.options[keyIdx];
                            fd.optionMap[key] = fd.values[keyIdx];
                        }

                        self.options[fd.name] = fd.optionMap;
                    } else {
                        self.options[fd.name] = {};
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

                        self.options[fd.name]['editable'] = true;

                    } else if (fd.collectionClass) {
                        fd.component = "object-grid"
                        fd.elemClassName = fd.collectionClass;

                        self.options[fd.name]['editable'] = true;

                    }
                }


                if (self.columnChanger) {
                    self.columnChanger(columns);
                }
            }
            xhr.send();


            return {
                rowData: this.data,
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

        onPagination: function(pagination){
            //console.log(pagination);

            this.pagination = pagination;
           //this.infoExtraction(pagination);
            this.loadData();

        },

        onSort: function(sort){
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


                xhr.open('GET', "http://localhost:8080/" + path+ "?page=" + (page-1) + "&size=" + size + (this.sort ? "&sort=" + this.sort.name + "," + this.sort.type : ""), false);
                xhr.setRequestHeader("access_token", localStorage['access_token']);
                xhr.onload = function () {
                    var jsonData = JSON.parse(xhr.responseText);
                    self.rowData = jsonData._embedded[path];
                }
                xhr.send();
            }

        },

        sortBy: function (key) {
            this.sortKey = key
            this.sortOrders[key] = this.sortOrders[key] * -1
        },

        addRow: function (aRow) {
            this.rowData.push(aRow);
        },

        showValue: function (key, entry) {
            if (key.computed) {
                return key.computed(entry);
            } else
                return entry[key.name];
        },

        onEvent: function (event, data) {
            if (event == "saved") {
                this.addRow(data);
            }
        },
        addObject: function (aRow) {
            if (!this.rowData) this.rowData = [];

            this.rowData.push(aRow);

            this.data = this.rowData;

        },
        submit_for_delete: function (key, num) {
            var path = 'product';
            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('DELETE', "http://localhost:8080/" + path + "/"+key, false);    
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.onload = function () {
                console.log(xhr);

            }
            xhr.send();

         },

        openDialog: function(ref) {
            alert(ref);
            this.$refs[ref].open();
        },
        closeDialog: function(ref) {
            this.$refs[ref].close();
          },
        onOpen: function(){
            console.log('Opened');
          },
        onClose: function(type) {

            if(type == 'ok'){
                this.deleteSubmit();
            }
            console.log('Closed', type);
          },

          onSelect: function(selected){

                this.selected = selected;

          },



          deleteSubmit: function(){
             for(var i in this.selected){
                   var primaryKey = (this.selected[i][this.metadata.keyFieldDescriptor.name]);
                   this.submit_for_delete(primaryKey);

             }

             this.loadData();
            
         },
         /*
          duplicationCheck: function() {
            var checkedNum = window.document.getElementsByClassName("md-checked").length;
            var obj = this.selected;
            var obj2 = "";
            if(checkedNum == 1){
                for(key in obj){
                obj2 = obj[key]
                
             }
            this.checked = obj2;
            this.$refs['dialog'].open();
            }else{
                this.$refs['dialog3'].open();
            }
         },*/
         doubleClickEvent: function(object){
            var obj = object;
            delete obj['_links'];
            this.checked = obj;
            this.$refs['dialog'].open();
         }
     }
    }
</script>
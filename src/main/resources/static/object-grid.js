var template;
{
    var xhr = new XMLHttpRequest();
    xhr.open('GET', "http://rawgit.com/TheOpenCloudEngine/metaworks4/master/src/main/resources/static/object-grid.html", false);
    xhr.onload = function () {
        template = xhr.responseText
    }
    xhr.send();
}


Vue.component('object-grid', {
    template: template,
    props: {
        data: Array,
        // columns: Array,
        filterKey: String,
        java: String,
        columnChanger: Object,
        fullFledged: Boolean,
        online: Boolean,
        options: Object,
        serviceLocator: Object
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
        // options: function(val){
        //     var initProps = this.initGrid();
        //
        //     this.columns = initProps.columns;
        //     this.metadata = initProps.metadata;
        // }
    },

    created: function () {
        this.dc = {
            title: '데이터 삭제',
            contentHtml: '해당 데이터를 삭제 하시겠습니까?',
            cancel: 'No',
            ok: 'Yes',
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


            xhr.open('GET', this.getServiceHost() + "/classdefinition?className=" + this.java, false);
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

        loadData: function () {
            if (this.online) {
                var page = this.pagination.page;
                var size = this.pagination.size;

                var pathElements = this.java.split(".");
                var path = pathElements[pathElements.length - 1].toLowerCase();
                var xhr = new XMLHttpRequest()
                var self = this


                xhr.open('GET', this.getServiceHost() + "/" + path+ "?page=" + (page-1) + "&size=" + size + (this.sort ? "&sort=" + this.sort.name + "," + this.sort.type : ""), false);
                xhr.setRequestHeader("access_token", localStorage['access_token']);
                xhr.onload = function () {
                    var jsonData = JSON.parse(xhr.responseText);
                    self.rowData = jsonData._embedded[path];

                    for(var i in self.rowData){

                        var row = self.rowData[i];

                        //load tenant properties as well
                        if(row && row._links && row._links.tenantProperties){
                            var tenantPropertiesURI = row._links.tenantProperties.href;

                            var xhr_ = new XMLHttpRequest()
                            xhr_.open('GET', tenantPropertiesURI, true);
                            xhr_.setRequestHeader("access_token", localStorage['access_token']);
                            xhr_.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
                            xhr_.myRow = row;
                            xhr_.onload = function () {
                                if(xhr_.responseText && xhr_.responseText.trim().length > 0){

                                    var jsonData = JSON.parse(xhr_.responseText);

                                    if(jsonData.json){ //TODO: couchbase specific
                                        jsonData = jsonData.json;
                                    }

                                    if(jsonData && self.metadata){
                                        for(var j in self.metadata.fieldDescriptors){
                                            var fd = self.metadata.fieldDescriptors[j];

                                            if(fd.attributes && fd.attributes.extended){
                                                Vue.set(this.myRow, fd.name, jsonData[fd.name]);
                                            }

                                        }

                                    }
                                }
                            }
                            xhr_.send();

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

        addRow: function (aRow) {
            if(!this.rowData) this.rowData = [];

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
        submit_for_delete: function (uri, num) {
            var path = 'product';
            var xhr = new XMLHttpRequest()
            var self = this
            //var uri = this.getServiceHost() + "/" + path + "/"+key
            xhr.open('DELETE', uri, false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.onload = function () {
                console.log(xhr);

            }
            xhr.send();

        },

        openDialog: function(ref) {
            this.$refs[ref].open();
        },
        closeDialog: function(ref) {
            this.$refs[ref].close();
        },
        onOpen: function(){
            console.log('Opened');
        },
        onClose: function(type) {

            if(type == 'ok' && this.online){
                this.deleteSubmit();
            }else{
                this.deleteSelectedRows();
            }
            console.log('Closed', type);
        },

        onSelect: function(selected){

            this.selected = selected;

        },


        deleteSubmit: function(){
            for(var i in this.selected){
                //var primaryKey = (this.selected[i][this.metadata.keyFieldDescriptor.name]);
                this.submit_for_delete(this.selected[i]._links.self.href);

            }

            this.loadData();

        },


        deleteSelectedRows: function(){
            var count = 0;
            for(var i in this.selected){
                this.rowData.splice(i-count, 1);
                count ++;
            }

            this.loadData();

        }
    }
})

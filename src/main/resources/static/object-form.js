var template;
{
    var xhr = new XMLHttpRequest();
    xhr.open('GET', "http://rawgit.com/TheOpenCloudEngine/metaworks4/master/src/main/resources/static/object-form.html", false);
    xhr.onload = function () {
        template = xhr.responseText
    }
    xhr.send();
}


Vue.component('object-form', {
    template: template,
    props: {
        java: String,
        data: Object,
        eventListeners: Array,
        online: Boolean,
        options: Object,
        checked: Object,
        pNo: String,
        serviceLocator: Object
    },

    watch:{
        java: function(){
            var initVars = this.initForm();

            this.metadata = initVars.metadata;
            this.columns = initVars.columns;
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

            var xhr = new XMLHttpRequest()
            var self = this
            var columns;
            var metadata;


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

            }
            xhr.send();


            return {
                columns: columns,
                metadata: metadata
            }

        },


        submit_: function () {

            var pathElements = this.java.split(".");
            var path = pathElements[pathElements.length - 1].toLowerCase();

            console.log(this.data);

            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('POST', this.getServiceHost() + "/" + path, false);
            //xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            xhr.onload = function () {
                var received = JSON.parse(xhr.responseText);

                self.data.ormid = received.ormid;
                self.data._links = received._links;
            }
            xhr.send(JSON.stringify(this.data));

            if (this.eventListeners) {
                this.eventListeners.forEach(function (listenerRef) {
                    var listener = self.$root.$refs[listenerRef];

                    if (listener.onEvent) {
                        listener.onEvent('saved', self.data);
                    }
                });
            }

            //send tenant properties as well
            if(self.data && self.data._links && self.data._links.tenantProperties){
                var tenantPropertiesURI = self.data._links.tenantProperties.href;

                var xhr = new XMLHttpRequest()
                var self = this
                xhr.open('POST', tenantPropertiesURI, false);
                xhr.setRequestHeader("access_token", localStorage['access_token']);
                xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
                xhr.onload = function () {
                }

                var tenantProperties = {};
                if(self.metadata){
                    for(var j in self.metadata.fieldDescriptors){
                        var fd = self.metadata.fieldDescriptors[j];

                        if(fd.attributes && fd.attributes.extended){
                            tenantProperties[fd.name] = this.data[fd.name];
                        }

                    }

                }

                xhr.send(JSON.stringify(tenantProperties));

            }
        },
        update_: function () {

            var pathElements = this.java.split(".");
            var path = pathElements[pathElements.length - 1].toLowerCase();

            console.log(this.data);
            var xhr = new XMLHttpRequest()
            var self = this

            //var uri = this.getServiceHost() + "/" + path + "/" +this.data.pNo;
            var uri = this.data._links.self.href;
            xhr.open('PUT', uri, false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            xhr.onload = function () {
                console.log(xhr);

            }
            xhr.send(JSON.stringify(this.data));

            if (this.eventListeners) {
                this.eventListeners.forEach(function (listenerRef) {
                    var listener = self.$root.$refs[listenerRef];

                    if (listener.onEvent) {
                        listener.onEvent('saved', self.data);
                    }
                });
            }
        },

        mw4Call_: function(e){
            var methodName = e.srcElement.id;
            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('POST', this.getServiceHost() + "/rpc", false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            xhr.onload = function () {
                console.log(xhr);

            }

            var invocationContext = {
                objectTypeName: this.java,
                clientObject: this.data,
                methodName: methodName
            }

            xhr.send(JSON.stringify(invocationContext));

            if (this.eventListeners) {
                this.eventListeners.forEach(function (listenerRef) {
                    var listener = self.$root.$refs[listenerRef];

                    if (listener.onEvent) {
                        listener.onEvent('called.' + methodName, self.data);
                    }
                });
            }
        }

    }

})


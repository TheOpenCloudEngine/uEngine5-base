<template>
    <form novalidate @submit.stop.prevent="submit" v-once>
        <md-input-container v-for="key in columns">
            <label>{{ key.displayName }}</label>
            <md-input v-if="!key.component" v-model="data[key.name]" :type="key.type"></md-input>
            <component v-if="key.component" :is="key.component" :data="data[key.name]" :java="key.elemClassName" :full-fledged="true" :options="options[key.name]" :selection="-1"></component>
        </md-input-container>

        <md-button v-if="online" class="md-raised md-primary" v-on:click.native="submit_">Submit</md-button>
    </form>
</template>

<script>
    export default {
        props: {
            java: String,
            data: Object,
            eventListeners: Array,
            online: Boolean
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

        initForm: function(){

            var xhr = new XMLHttpRequest()
            var self = this
            var columns;
            var metadata;


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

            }
            xhr.send();


            return {
                columns: columns,
                metadata: metadata
            }

        },

        onEvent: function (event, data) {
            if (event == "saved") {
                this.addRow(data);
            }
        },
        

        submit_: function () {

            var pathElements = this.java.split(".");
            var path = pathElements[pathElements.length - 1].toLowerCase();

            console.log(this.data);

            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('POST', "http://localhost:8080/" + path, false);
            //xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            xhr.onload = function () {
                console.log(xhr);

            }
            xhr.send(JSON.stringify(this.data));    

            if (this.eventListeners) {
                this.eventListeners.forEach(function (listenerRef) {
                    var listener = self.$parent.$parent.$parent.$refs[listenerRef];

                    if (listener.onEvent) {
                        listener.onEvent('saved', self.data);
                    }
                });
            }
        },

         update_: function () {

            var pathElements = this.java.split(".");
            var path = pathElements[pathElements.length - 1].toLowerCase();

            console.log(this.data);
            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('PUT', "http://localhost:8080/" + path + "/" +this.data.pNo, false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            xhr.onload = function () {
                console.log(xhr);

            }
            xhr.send(JSON.stringify(this.data));

            if (this.eventListeners) {
                this.eventListeners.forEach(function (listenerRef) {
                    var listener = self.$parent.$parent.$parent.$parent.$refs[listenerRef];

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
            xhr.open('POST', "http://localhost:8080/rpc", false);
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
    }   
</script>
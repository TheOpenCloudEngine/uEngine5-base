<template>


  <form novalidate @submit.stop.prevent="submit">
    <md-input-container v-for="key in columns">
      <label>{{ key.displayName }}</label>
      <md-input v-if="!key.component" v-model="data[key.name]" :type="key.type"></md-input>
      <component v-if="key.component" :is="key.component" :data.sync="data[key.name]" :java="key.elemClassName"
                 :full-fledged="true" :options="options_[key.name]" :selection="-1"></component>
    </md-input-container>

    <md-button v-if="online" class="md-raised md-primary" v-on:click.native="submit_">Submit</md-button>

    <span v-for="serviceMethod in metadata.serviceMethodContexts">
        <md-button class="md-raised md-primary" v-on:click.native="mw4Call_" :id="serviceMethod.methodName">{{serviceMethod.methodName}}</md-button>
    </span>
  </form>

</template>

<script>
  export default {
    props: {
      java: String,
      classDefinition: Object,
      data: Object,
      eventListeners: Array,
      online: Boolean,
      options: Object,
      checked: Object,
      pNo: String,
      serviceLocator: Object
    },

    watch: {
      java: function () {
        var initVars = this.initForm();

        this.metadata = initVars.metadata;
        this.columns = initVars.columns;
        this.options_ = initVars.options_;
      }
    },
    data: function () {
      return this.initForm();
    },

    methods: {
      getServiceHost: function () {
        if (this.serviceLocator) {
          if (this.serviceLocator.host) {
            return this.serviceLocator.host;
          } else if (this.$root.$refs[this.serviceLocator]) {
            return this.$root.$refs[this.serviceLocator].host;
          } else {
            return this.serviceLocator;
          }

        } else {
          return "http://127.0.0.1:8080"
        }
      },

      initForm: function () {
        var self = this;
        var columns;
        var metadata;
        var initOptions = this.options;

        if (!initOptions) {
          initOptions = {};
        }

        if(this.classDefinition){
            metadata = this.classDefinition;

        }else {

          var xhr = new XMLHttpRequest();

          xhr.open('GET', this.getServiceHost() + "/classdefinition?className=" + this.java, false);
          xhr.setRequestHeader("access_token", localStorage['access_token']);
          xhr.onload = function () {
            metadata = JSON.parse(xhr.responseText)

          };
          xhr.send();
        }

        columns = metadata.fieldDescriptors;

        for (var i = 0; i < columns.length; i++) {
          var fd = columns[i];

          //if(!fd.className) throw "field [" + fd.name + "] doesn't have its className";

          if (fd.options && fd.values) {
            fd.optionMap = {};
            for (var keyIdx in fd.options) {
              var key = fd.options[keyIdx];
              fd.optionMap[key] = fd.values[keyIdx];
            }

            initOptions[fd.name] = fd.optionMap;
          } else {
            initOptions[fd.name] = {};
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
          } else if (fd.className && fd.className.indexOf('[L') == 0 && fd.className.indexOf(";") > 1) {
            fd.component = "object-grid"
            fd.elemClassName = fd.className.substring(2, fd.className.length - 1);

            initOptions[fd.name]['editable'] = true;

          } else if (fd.collectionClass) {
            fd.component = "object-grid"
            fd.elemClassName = fd.collectionClass;

            initOptions[fd.name]['editable'] = true;

          }
        }

        return {
          columns: columns,
          metadata: metadata,
          options_: initOptions ? initOptions : {}
        }
      },





      submit_: function () {
        console.log('this.data' , this.data);
        var pathElements = this.java.split(".");
        var path = pathElements[pathElements.length - 1].toLowerCase();

        var xhr = new XMLHttpRequest()
        var self = this;
        xhr.open('POST', this.getServiceHost() + "/" + path, false);
        //xhr.setRequestHeader("Content-Type", "application/json");
        xhr.setRequestHeader("access_token", localStorage['access_token']);
        xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
        xhr.onload = function () {
          var received = JSON.parse(xhr.responseText);

          console.log('received' , received);
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
        if (self.data && self.data._links && self.data._links.tenantProperties) {
          var tenantPropertiesURI = self.data._links.tenantProperties.href;

          var xhr = new XMLHttpRequest()
          var self = this
          xhr.open('POST', tenantPropertiesURI, false);
          xhr.setRequestHeader("access_token", localStorage['access_token']);
          xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
          xhr.onload = function () {
          }

          var tenantProperties = {};
          if (self.metadata) {
            for (var j in self.metadata.fieldDescriptors) {
              var fd = self.metadata.fieldDescriptors[j];

              if (fd.attributes && fd.attributes.extended) {
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

      mw4Call_: function (e) {
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
  }
</script>

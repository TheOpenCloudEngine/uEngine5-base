<template>


  <form novalidate @submit.stop.prevent="submit">
    <div v-for="fd in columns">
      <div v-if="fd.component">
        <md-subheader>{{fd.displayName}}</md-subheader>

        <component :is="fd.component" v-model="value[fd.name]" :java="fd.elemClassName" :data-label="fd.displayName"
                 :full-fledged="true" :options="options_[fd.name]" :selection="-1" :metadataResolver = "metadataResolver" :fieldDescriptor="fd"></component>
      </div>
      <div v-else>
        <div v-if="isObject(value[fd.name])">
          <md-subheader>{{fd.displayName}}</md-subheader>
          <tree-view :data="value[fd.name]" :options="{maxDepth: 3}"></tree-view>
          <br>
        </div>
        <md-input-container style="margin-top: -15px;" v-else>
            <label>{{ fd.displayName }}</label>
            <md-input v-model="value[fd.name]" :type="fd.type"></md-input>
        </md-input-container>
      </div>
    </div>


    <md-button v-if="online" class="md-raised md-primary" v-on:click.native="submit_hateoas">Save</md-button>

    <span v-if="metadata.serviceMethodContexts" v-for="serviceMethod in metadata.serviceMethodContexts">
        <md-button class="md-raised md-primary" v-on:click.native="mw4Call_" :id="serviceMethod.methodName">{{serviceMethod.methodName}}</md-button>
    </span>
  </form>

</template>

<script>
  export default {
    props: {
      java: String,
      classDefinition: Object,
      value: Object,
      eventListeners: Array,
      online: Boolean,
      options: Object,
      checked: Object,
      pNo: String,
      serviceLocator: Object,
      backend: Object,
      metadataResolver: Function,
      baseUri: String
    },

    watch: {
      classDefinition: function () {
        var me = this;
        me.newForm();
      },
      java: function () {
        var me = this;
        me.newForm();
      },
      value: {
        deep: true,
        handler: function(){

          this.$emit("input", this.value)
        }

      },
      online:function(newVal, oldVal){
        var me = this;
        if(oldVal && !newVal)
        this.$nextTick(function(){
          me.online = true;
        });//, 2000)
      }
    },
    data: function () {
      return this.initForm();
    },

    created: function(){

      if(!this.value){
        this.value = {};

        this.$emit("input", this.value)
      }

      if(!this.online && this.options && this.options["online"]){ //don't know why the parent value for online options is changed.

        var me = this;
        this.$nextTick(function(){
          me.online = true;
        });//, 2000)

      }
    },

    methods: {
      newForm: function () {
        var initVars = this.initForm();

        this.metadata = initVars.metadata;
        this.columns = initVars.columns;
        this.options_ = initVars.options_;

        var pathElements = this.java.split(".");
        this.baseUri = pathElements[pathElements.length - 1].toLowerCase();
      },
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

            if(this.backend){
              return this.backend.$bind.ref;
            }

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

          if(this.metadataResolver){
            metadata = this.metadataResolver(this.java, this);
          }else{

            xhr.open('GET', this.getServiceHost() + "/classdefinition?className=" + this.java, false);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.onload = function () {
              metadata = JSON.parse(xhr.responseText)
            };
            xhr.send();
          }
        }

        var baseUri;

        if(metadata.baseUri)
          baseUri = metadata.baseUri;

        columns = metadata.fieldDescriptors;

        for (var i = 0; i < columns.length; i++) {
          var fd = columns[i];

          //if(!fd.className) throw "field [" + fd.name + "] doesn't have its className";

          if(!fd.displayName) fd.displayName = fd.name;

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

          } else if (fd.className.indexOf("#") > 1){

            fd.component = "object-form"
            fd.elemClassName = fd.className;

            initOptions[fd.name]['editable'] = true;
          }

          if(fd._online || (this.options && this.options["online"]) || this.online){
            initOptions[fd.name]['online'] = true;
          }

          if(fd.attributes && fd.name && fd.className){
            var relation = fd.attributes['relationAnnotation'];
            if(relation && relation.indexOf("Many") == 0){
              //relation starts with 'Many', that means this requires to reference the parent's value
               var parent = self.$parent;
               while(parent && (!parent.metadata || (parent.metadata.name != fd.className))){
                 parent = parent.$parent;
               }

               if(parent && parent.metadata.name == fd.className){
                 fd.attributes['uriReferencingParent'] = parent;
               }
            }
          }

          if(fd.component == null && fd.options && fd.values){
            fd.component = "object-form-select"
          }

          if(fd.component == null && fd.className == "java.lang.Boolean"){
            fd.component = "object-form-boolean"
          }


          if(fd.component == null && fd.className != "java.lang.String"){ //finally there's no matching component

             //change the camelCase to dash-separated component
            var component = "object-form-" + fd.className.replace( /([a-z])([A-Z])/g, '$1-$2' ).replace(/\./g, '-').toLowerCase()

            if(Vue._components[component]){
              fd.component = component;
            }
          }

        }

        return {
          columns: columns,
          metadata: metadata,
          options_: initOptions ? initOptions : {},
          baseUri_: baseUri
        }
      },

      // hateoas version of submit
      submit_hateoas: function(){

        // set the referencing value if there are.
        for (var i = 0; i < this.columns.length; i++) {
          var fd = this.columns[i];

          if(!fd.attributes) continue;

          var uriReferencingParent = fd.attributes['uriReferencingParent'];

          if(uriReferencingParent){
            var parentValue = uriReferencingParent.value;
            this.value[fd.name] = parentValue._links.self.href;
          }
        }

        var me = this;
        var xhr = new XMLHttpRequest();
        var added = false;

        if(this.value._links && this.value._links.self){ //it is update
          xhr.open('PUT', this.value._links.self.href, false);
        }else{ // addition
          xhr.open('POST', this.getServiceHost() + "/" + this.baseUri_, false);
          added = true;
        }

        xhr.setRequestHeader("access_token", localStorage['access_token']);
        xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

        xhr.onload = function () {
          var received = JSON.parse(xhr.responseText);

          me.value.ormid = received.ormid;
          me.value._links = received._links;

          me.$emit(added ? "added" : "updated", me.value);
        }

        xhr.send(JSON.stringify(this.value));

      },

      submit_: function () {
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
          self.value.ormid = received.ormid;
          self.value._links = received._links;
        }
        xhr.send(JSON.stringify(this.value));

        if (this.eventListeners) {
          this.eventListeners.forEach(function (listenerRef) {
            var listener = self.$root.$refs[listenerRef];

            if (listener.onEvent) {
              listener.onEvent('saved', self.value);
            }
          });
        }

        //send tenant properties as well
        if (self.value && self.value._links && self.value._links.tenantProperties) {
          var tenantPropertiesURI = self.value._links.tenantProperties.href;

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
                tenantProperties[fd.name] = this.value[fd.name];
              }

            }

          }

          xhr.send(JSON.stringify(tenantProperties));

        }
      },

      update_: function () {

        var pathElements = this.java.split(".");
        var path = pathElements[pathElements.length - 1].toLowerCase();

        console.log(this.value);
        var xhr = new XMLHttpRequest()
        var self = this

        var uri = this.value._links.self.href;
        xhr.open('PUT', uri, false);
        xhr.setRequestHeader("access_token", localStorage['access_token']);
        xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
        xhr.onload = function () {
          console.log(xhr);

        }
        xhr.send(JSON.stringify(this.value));

        if (this.eventListeners) {
          this.eventListeners.forEach(function (listenerRef) {
            var listener = self.$root.$refs[listenerRef];

            if (listener.onEvent) {
              listener.onEvent('saved', self.value);
            }
          });
        }
      },

      pretty: function(original){
        return JSON.stringify(original, null, 2);
      },

      isObject(val) {
          if (val === null) { return false;}
          return ( (typeof val === 'function') || (typeof val === 'object') );
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
          clientObject: this.value,
          methodName: methodName
        }

        xhr.send(JSON.stringify(invocationContext));

        if (this.eventListeners) {
          this.eventListeners.forEach(function (listenerRef) {
            var listener = self.$root.$refs[listenerRef];

            if (listener.onEvent) {
              listener.onEvent('called.' + methodName, self.value);
            }
          });
        }
      }
    }
  }
</script>

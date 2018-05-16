<template>

  <md-card md-with-hover v-if="instance">
    <md-card-header>

      <div class="md-title">{{instance.name}}</div>
      <div class="md-subhead">Started by {{instance.initRsNm}} at {{instance.startedDate}}</div>
    </md-card-header>

    <md-card-content>
      <md-tabs md-right :md-dynamic-height="false" class="md-transparent example-tabs" style="margin-top: -48px;"
               @change="onChangeTab">
        <md-tab class="code-content" md-label="Info" id="info" md-active>

          <div style="height:1000px" v-if="infoEnabled">
            <process-designer
              :backend="backend"
              :id="''+instance.rootInstId"
              :monitor="true"
              :rootId="''+instance.rootInstId">
            </process-designer>
          </div>
        </md-tab>
        <md-tab class="example-content" md-label="Actions">

          <md-button v-for="(signalEvent, key) in variables[':signalEvents:prop']" @click="signal(key)">
            {{signalEvent.signalName}}
          </md-button>

        </md-tab>
      </md-tabs>
    </md-card-content>
  </md-card>
</template>


<script>
  export default {
    props: {
      uri: String,
    },

    watch: {
      uri: function (val) {
        this.load();
      }
    },

    data: function () {

      return {
        instance: null,
        variables: {},
        backend: window.backend,
        infoEnabled: false
      };
    },

    created: function () {
    },

    mounted: function () {
    },

    methods: {
      load: function () {
        var me = this;
        var instance = {}

        window.backend.$bind(this.uri, instance);
        instance.$load().then(function (instance) {
          me.instance = instance;

          me.$root.codi('instance{/id}/variables').get({id: me.instance.rootInstId})
            .then(function (response) {
              me.variables = response.data;
            });

        });


      },

      onChangeTab: function (tabIndex) {
        if (tabIndex == 0) {
          this.infoEnabled = true;
        } else {
          this.infoEnabled = false;
        }
      },

      signal: function (key) {

        var me = this;

        me.$root.codi('instance/' + me.instance.rootInstId + '/signal/' + key).save()
          .then(function (response) {
            alert('succeed');
          });


      }

    }
  }
</script>


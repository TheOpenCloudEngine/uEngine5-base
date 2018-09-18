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
              :instanceId="'' + id"
              :monitor="true"
              :rootInstanceId="'' + instance.rootInstId">
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
      id: String,
    },
    watch: {
      id: function (val) {
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
    mounted: function () {
      this.load();
    },

    methods: {
      load: function () {
        var me = this;
        var instance = {}
        window.backend.$bind('instances/' + me.id, instance);
        instance.$load().then(function (instance) {
          me.instance = instance;
          console.log(me.instance);
          me.$root.codi('instance{/id}/variables').get({id: me.id})
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
        me.$root.codi('instance/' + me.id + '/signal/' + key).save()
          .then(function (response) {
            alert('succeed');
          });
      }
    }
  }
</script>


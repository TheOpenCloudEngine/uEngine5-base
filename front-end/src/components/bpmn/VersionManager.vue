<template>
  <div>
    <md-button class="md-primary md-raised" v-on:click="editMode = true" @click.native="getTime">{{ $t("message['version.add']") }}</md-button>
    <transition name="slide-fade">
      <div v-if="editMode == true">
        <md-whiteframe md-elevation="4">
          <form style="padding: 5px;">
            <md-input-container>
              <label>{{ $t("message['version.writer']") }}</label>
              <md-input v-model="user.name" readonly></md-input>
            </md-input-container>
            <md-input-container>
              <label>{{ $t("message['version.date']") }}</label>
              <md-input v-model="dateTime" readonly></md-input>
            </md-input-container>
            <md-input-container>
              <label>{{ $t("message['version.description']") }}</label>
              <md-input v-model="description"></md-input>
            </md-input-container>
            <div align="center">
              <md-radio v-model="versionUp" id="my-test4" name="my-test-group2" md-value="major" class="md-primary">{{ $t("message['version.update.major']") }}</md-radio>
              <md-radio v-model="versionUp" id="my-test5" name="my-test-group2" md-value="minor" class="md-primary" selected>{{ $t("message['version.update.minor']") }}</md-radio>
            </div>
            <div align="right">
              <md-button class="md-warn md-raised" @click.native="addVersion">Add</md-button>
              <md-button class="md-warn md-raised"  v-on:click="editMode = false">Close</md-button>
            </div>
          </form>
        </md-whiteframe>
      </div>
    </transition>
    <!-- 버전관리 시작 -->
    <md-table>
      <md-table-header>
        <md-table-row>
          <md-table-head>Version</md-table-head>
          <md-table-head>Description</md-table-head>
          <md-table-head>Date</md-table-head>
          <md-table-head>Writer</md-table-head>
          <md-table-head>Production</md-table-head>
        </md-table-row>
      </md-table-header>
      <md-table-body>
        <md-table-row v-for="version in versions">
          <md-table-cell>{{version.version.major}}.{{version.version.minor}}

          </md-table-cell>
          <md-table-cell>{{version.version.description}}</md-table-cell>
          <md-table-cell>{{version.version.date}}</md-table-cell>
          <md-table-cell>작성자</md-table-cell>
          <md-table-cell style="width: 140px;" v-if="version.version.production!==true">
            <md-theme>
              <md-button class="md-raised md-primary" style="width: 100%" @click.native="makeProduction(version)">활성화</md-button>
            </md-theme>
          </md-table-cell>
          <md-table-cell v-if="version.version.production==true">현재 버전</md-table-cell>
        </md-table-row>
      </md-table-body>
    </md-table>
    <!-- 버전 관리 끝 -->
  </div>
</template>
<script>

  export default {
    props: {
      backend: Object,
    },
    data () {
    return {
      versions: null,
      selectedVersion: null,
      iam: window.iam,
      user: {

      },
      editMode: false,
      dateTime : '',
      description: '',
      versionUp: 'minor'
    }
  },
  mounted() {
    var me = this;
    me.loadVersions();

    me.iam.getUser(localStorage['userName']).then(function(response){
      me.user = response;
      console.log(me.user);
    })

  },
  methods: {
    loadVersions: function() {
      var me = this;

      var versions = [];

      me.backend.$bind("version", versions);

      versions.$load().then(function(versions) {
        if (versions) {
          me.versions = versions;
        }
      });

    },

    makeProduction: function (version) {
      var me = this;
      version.makeProduction.$create().then(function () {
        me.loadVersions()
      })
    },
    getTime: function () {
      var d = new Date();

      this.dateTime = this.leadingZeros(d.getFullYear(), 4) + '-' + this.leadingZeros(d.getMonth()+1, 2) + '-' + this.leadingZeros(d.getDate(), 2) + ' '  + this.leadingZeros(d.getHours(), 2) + ':' + this.leadingZeros(d.getMinutes(), 2);

    },
    leadingZeros: function (n, digits) {
      var zero = '';
      n = n.toString();

      if (n.length < digits) {
        for (var i = 0; i < digits - n.length; i++)
          zero += '0';
      }
      return zero + n;
    },
    addVersion: function () {
      var me = this;
//
//      var versions = {};
//
//      me.backend.$bind("version", versions);
//
//      versions.$create({}).then(function() {
//
//        alert('xxx')
//        me.loadVersions();
//
//      });
      if (this.versionUp == 'minor') {
        me.$root.codi('version/').save()
          .then(function (response) {

            me.loadVersions();
            me.$root.$children[0].success('저장되었습니다.');
          });
      } else if (this.versionUp == 'major') {
        me.$root.$children[0].success('Major 저장 미구현.');
      }
      console.log(this.editMode);
      this.editMode = false;


    }
  }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .slide-fade-enter-active {
    transition: all .3s ease;
  }
  .slide-fade-leave-active {
    transition: all .4s cubic-bezier(1.0, 0.5, 0.8, 1.0);
  }
  .slide-fade-enter, .slide-fade-leave-to
    /* .slide-fade-leave-active below version 2.1.8 */ {
    transform: translateX(10px);
    opacity: 0;
  }
  .md-table .md-table-head {
    text-align: center;
  }
  .md-table .md-table-cell {
    text-align: center;
  }
</style>

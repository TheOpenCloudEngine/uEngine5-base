<template>
  <md-layout md-gutter>
    <md-layout md-gutter>
      <md-layout md-flex="15">
        <div class="phone-viewport">
          <md-toolbar md-theme="white">
            <span class="md-title">프로세스 목록</span>
          </md-toolbar>

          <md-list>
            <md-list-item md-expand-multiple>
              <md-icon>folder</md-icon>
              <span class="md-body-1">LEVEL1</span>
              <md-list-expand>
                <md-list>
                  <md-list-item class="md-inset" v-for="tree in trees" :key="tree.name">
                    {{tree.name}}
                  </md-list-item>
                </md-list>
              </md-list-expand>
            </md-list-item>
          </md-list>
        </div>
      </md-layout>
      <md-layout md-flex="85">
        <md-table>
          <md-table-header>
            <md-table-row>
              <md-table-head v-for="header in headers" :key="header.text">{{header.text}}</md-table-head>
            </md-table-row>
          </md-table-header>

          <md-table-body>
            <md-table-row v-for="item in items" :key="item.defId">
              <md-table-cell>{{item.status}}</md-table-cell>
              <md-table-cell>{{item.instId}}</md-table-cell>
              <md-table-cell><a href="#" v-on:click="move(item.instId)">{{item.defId}}</a></md-table-cell>
              <md-table-cell>{{item.defName}}</md-table-cell>
              <md-table-cell>{{item.defName}}</md-table-cell>
              <md-table-cell>{{item.eventHandler}}</md-table-cell>
              <md-table-cell>{{item.info}}</md-table-cell>
              <md-table-cell>{{item.startedDate}}</md-table-cell>
              <md-table-cell>{{item.finishedDate}}</md-table-cell>
              <md-table-cell>{{item.ext1}}</md-table-cell>
              <md-table-cell>{{item.instId}}</md-table-cell>
            </md-table-row>
          </md-table-body>
        </md-table>
      </md-layout>
    </md-layout>
  </md-layout>
</template>
<script>
  export default {
    data () {
      return {
        headers: [
          {text: '상태', value: 'status'},
          {text: '아이디', value: 'instId'},
          {text: '인스턴스명', value: 'defId'},
          {text: '프로세스명', value: 'defName'},
          {text: '시작자', value: 'defName'},
          {text: '현담당자', value: 'eventHandler'},
          {text: '정보', value: 'info'},
          {text: '시작일', value: 'startedDate'},
          {text: '종료일', value: 'finishedDate'},
          {text: 'Ext1', value: 'ext1'},
          {text: '삭제', value: 'instId'}
        ],
        items: [
          {
            instId: 'instId',
            defName: 'defName',
            defId: 'defId',
            name: 'name',
            status: 'status',
            eventHandler: 'eventHandler',
            isSubProcess: 'isSubProcess',
            startedDate: 'startedDate',
            info: 'info',
            ext1: 'ext1',
            finishedDate: 'finishedDate'
          }
        ],
        trees: [
          {
            name: 'name'
          }
        ]
      }
    },
    mounted() {
      var me = this;
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
      this.$root.codi('instances').get()
        .then(function (response) {
          var instances = [];
          if (response.data._embedded && response.data._embedded.instances && response.data._embedded.instances.length) {
            $.each(response.data._embedded.instances, function (i, instance) {
              let split = instance._links.self.href.split('/');
              instance['instId'] = split[split.length - 1];
              instances.push(instance);
            });
            me.items = instances;
          }
        })
      var tree = this;
      this.$root.codi('definitions').get()
        .then(function (response) {
          var trees = [];
          $.each(response.data, function (i, definition) {
            definition = definition.replace('/', '');
            trees.push({
              name: definition
            });
          });
          tree.trees = trees;
        })
    },
    methods: {
      move: function (instId) {
        this.$router.push({
          path: 'instance/' + instId
        })
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }
</style>

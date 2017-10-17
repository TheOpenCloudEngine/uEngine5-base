<template>
  <md-layout md-gutter>
    <md-layout md-gutter>
      <md-layout md-flex="15">
        <div class="phone-viewport">
          <md-toolbar md-theme="white">
            <span class="md-title">프로세스</span>
          </md-toolbar>

          <md-list>
            <md-list-item md-expand-multiple>
              <md-icon>folder</md-icon>
              <span class="md-body-1">프로세스 목록</span>
              <md-list-expand>
                <md-list>
                  <md-list-item class="md-inset" v-for="tree in trees" :key="tree.name">
                    {{tree.name}}
                  </md-list-item>
                </md-list>
              </md-list-expand>
            </md-list-item>
          </md-list>
          <md-list>
            <md-list-item md-expand-multiple>
              <md-icon>folder</md-icon>
              <span class="md-body-1">인스턴스 검색</span>
              <md-list-expand>
                <md-list>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <md-select name="status" id="status" v-model="filter.status" @change="setStatus">
                        <md-option value="all">모두</md-option>
                        <md-option value="doing">진행중</md-option>
                        <md-option value="ready">준비중</md-option>
                        <md-option value="complete">완료됨</md-option>
                        <md-option value="stop">중지됨</md-option>
                        <md-option value="skip">건너뜀</md-option>
                        <md-option value="pause">일시중지</md-option>
                        <md-option value="fail">실패함</md-option>
                      </md-select>
                    </md-input-container>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>인스턴스 아이디</label>
                      <md-input placeholder="Instance ID" v-model="filter.instId"></md-input>
                    </md-input-container>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>이름</label>
                      <md-input placeholder="name" v-model="filter.name"></md-input>
                    </md-input-container>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>시작자</label>
                      <md-input placeholder="starter" v-model="filter.defName"></md-input>
                    </md-input-container>
                    <md-dialog-prompt
                      :md-title="prompt.title"
                      :md-ok-text="prompt.ok"
                      :md-cancel-text="prompt.cancel"
                      @open="onOpen"
                      @close="onClose"
                      ref="dialog6">
                    </md-dialog-prompt>

                    <md-button class="md-icon-button md-raised md-primary" @click.native="openDialog('dialog6')">
                      <md-icon style="color: white">search</md-icon>
                    </md-button>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>현담당자</label>
                      <md-input placeholder="current manager" v-model="filter.eventHandler"></md-input>
                    </md-input-container>
                    <md-dialog-prompt
                      :md-title="prompt.title"
                      :md-ok-text="prompt.ok"
                      :md-cancel-text="prompt.cancel"
                      @open="onOpen"
                      @close="onClose"
                      ref="dialog6">
                    </md-dialog-prompt>
                    <md-button class="md-icon-button md-raised md-primary">
                      <md-icon style="color: white">search</md-icon>
                    </md-button>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>시작일</label>
                      <md-input placeholder="Start Date" v-model="filter.startedDate"></md-input>
                    </md-input-container>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-input-container>
                      <label>종료일</label>
                      <md-input placeholder="End Date" v-model="filter.finishedDate"></md-input>
                    </md-input-container>
                  </md-list-item>
                  <md-list-item class="md-inset">
                    <md-button class="md-raised md-primary" v-on:click="search()">Search</md-button>
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
    data() {
      return {
        status: 'all',
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
        prompt: {
          title: 'What\'s your name?',
          ok: 'Done',
          cancel: 'Cancel',
          id: 'name',
          name: 'name',
          placeholder: 'Type your name...',
          maxlength: 30,
          value: ''
        },
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
        ],
        filter:{
          instId: 'instId',
          defName: 'defName',
          defId: 'defId',
          name: 'name',
          status: 'status',
          eventHandler: 'eventHandler',
          startedDate: 'startedDate',
          finishedDate: 'finishedDate'
        }
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
      },
      search: function () {
        var url = 'instances/search/findFilterICanSee?';
        if (this.filter.instId != null ){
          url += 'instId=' + this.filter.instId;
        }
        this.$root.codi(url).get()
          .then(function (response) {
            var items = [];
            $.each(response.data, function (i, filteredData){
              console.log(filteredData.instances[0].defId);
              console.log(filteredData.instances);
//              filteredData = filteredData.replace('/', '');
              items.push({
                instId: filteredData.instId,
                defName: filteredData.defName,
                defId: filteredData.defId,
                name: filteredData.name,
                status: filteredData.status,
                eventHandler: filteredData.eventHandler,
                isSubProcess: filteredData.isSubProcess,
                startedDate: filteredData.startedDate,
                info: filteredData.info,
                ext1: filteredData.ext1,
                finishedDate: filteredData.finishedDate
              });
            });
            item.items = items;
          })
      },
      setStatus: function (status) {
        this.status = status;
      },
      openDialog(ref) {
        this.$refs[ref].open();
      },
      closeDialog(ref) {
        this.$refs[ref].close();
      },
      onOpen() {
        console.log('Opened');
      },
      onClose(type) {
        console.log('Closed', type);
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }
</style>

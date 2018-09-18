<template xmlns:v-on="http://www.w3.org/1999/xhtml">
  <md-layout style="height: 100%">
    <md-layout md-flex="20" class="bg-white" style="border-right: 1px solid #DFE3E6;padding: 0px">
      <div style="padding: 16px; width: 100%;height: 100%;overflow-x: hidden;overflow-y: auto">

        <md-input-container>
          <label>상태</label>
          <md-select name="status" id="status" v-model="filter.status" @change="setStatus">
            <md-option value="All">모두</md-option>
            <md-option value="Running">진행중</md-option>
            <md-option value="Ready">준비중</md-option>
            <md-option value="Completed">완료됨</md-option>
            <md-option value="Stopped">중지됨</md-option>
            <md-option value="Skipped">건너뜀</md-option>
            <md-option value="Suspended">일시중지</md-option>
            <md-option value="Failed">실패함</md-option>
          </md-select>
        </md-input-container>

        <md-input-container md-clearable>
          <label>인스턴스 아이디</label>
          <md-input v-model="filter.instId"></md-input>
        </md-input-container>

        <md-input-container md-clearable>
          <label>이름</label>
          <md-input v-model="filter.defId"></md-input>
        </md-input-container>

        <md-input-container>
          <label>시작자</label>
          <user-autocomplete
            :role="role"
            v-if="role"
            v-on:userSelected="userSelected"
          ></user-autocomplete>
        </md-input-container>

        <md-input-container>
          <label>현담당자</label>
          <user-autocomplete
            :role="role"
            v-if="role"
            v-on:userSelected="userSelected"
          ></user-autocomplete>
        </md-input-container>


        <md-input-container>
          <label>시작일</label>
          <md-input type="date" placeholder="Start Date" v-model="filter.startedDate"></md-input>
        </md-input-container>

        <md-input-container>
          <label>종료일</label>
          <md-input type="date" placeholder="End Date" v-model="filter.finishedDate"></md-input>
        </md-input-container>

        <md-button class="md-raised md-primary" v-on:click="makeList">Search</md-button>
      </div>
    </md-layout>
    <md-layout md-flex="80">
      <div style="width: 100%;padding: 16px">
        <md-table-card>
          <div class="header-top-line"></div>
          <md-table>
            <md-table-header>
              <md-table-row>
                <md-table-head>아이디</md-table-head>
                <md-table-head>상태</md-table-head>
                <md-table-head>프로세스명</md-table-head>
                <md-table-head>현담당자</md-table-head>
                <md-table-head>시작일</md-table-head>
                <md-table-head>종료일</md-table-head>
                <md-table-head>액션</md-table-head>
              </md-table-row>
            </md-table-header>
            <md-table-body>
              <md-table-row v-for="item in items" :md-item="item" style="cursor:pointer">
                <md-table-cell @click.native="onClickList(item.instId)"><a>{{item.instId}}</a></md-table-cell>
                <md-table-cell>{{item.status}}</md-table-cell>
                <md-table-cell>{{item.defName}}</md-table-cell>
                <md-table-cell>{{item.endpoint}}</md-table-cell>
                <md-table-cell>{{yyyyMMdd(item.startedDate)}}</md-table-cell>
                <md-table-cell>{{yyyyMMdd(item.finishedDate)}}</md-table-cell>
                <md-table-cell class="md-has-action">
                  <md-menu md-direction="bottom-end">
                    <md-icon class="folder-menu" md-menu-trigger>more_vert</md-icon>
                    <md-menu-content>
                      <md-menu-item
                        @click.native="stopInstance(item.instId)">
                        <span>{{ $t("message['button.stop']") }}</span>
                        <md-icon>stop</md-icon>
                      </md-menu-item>
                      <md-menu-item @click.native="pauseInstance(item.instId);">
                        <span>{{ $t("message['button.pause']") }}</span>
                        <md-icon>pause</md-icon>
                      </md-menu-item>
                      <md-menu-item @click.native="resumeInstance(item.instId)">
                        <span>{{ $t("message['button.resume']") }}</span>
                        <md-icon>play_arrow</md-icon>
                      </md-menu-item>
                    </md-menu-content>
                  </md-menu>
                </md-table-cell>
              </md-table-row>
            </md-table-body>
            <md-table-body v-if="items.length == 0">
              <md-table-row>
                <md-table-cell colspan="11">not exists instance list</md-table-cell>
              </md-table-row>
            </md-table-body>
          </md-table>
          <md-table-pagination
            :md-size="size"
            :md-total="total"
            :md-page="page"
            md-label="Rows"
            md-separator="of"
            :md-page-options="[5, 10, 20]"
            @pagination="onPagination"></md-table-pagination>
        </md-table-card>
      </div>
    </md-layout>
  </md-layout>
</template>
<script>

  export default {
    props: {
      iam: Object,
      backend: Object
    },
    data() {
      return {
        status: 'All',
        windowWidth: 0,
        headers: [
          {text: '아이디', value: 'instId'},
          {text: '상태', value: 'status'},
          {text: '프로세스명', value: 'defName'},
          {text: '현담당자', value: 'eventHandler'},
          {text: '시작일', value: 'startedDate'},
          {text: '종료일', value: 'finishedDate'},
          {text: '액션'}
        ],
        items: [],
        paging: {
          rowSize: 0,
          rowTotal: 0,
          rowStart: 0
        },
        id: "",
        users: [],
        role: "endpoint",
        roleName: "",
        trees: [
          {
            name: 'name'
          }
        ],
        filter: {
          instId: '',
          defName: '',
          defId: '',
          name: '',
          endpoint: '',
          eventHandler: '',
          startedDate: '',
          finishedDate: ''
        },
        total: 100,
        size: 10,
        page: 1
      }
    },
    mounted() {
      this.makeList();
    },
    methods: {
      yyyyMMdd: function (dateString) {
        if (dateString) {
          return new Date(dateString).toISOString().substring(0, 10);
        } else {
          return null;
        }
      },
      stopInstance: function (instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/stop';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({}).then(
          function (response) {
            me.$root.$children[0].success('중지되었습니다.');
            me.makeList();
          });
      },
      pauseInstance: function (instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/stop';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({}).then(
          function (response) {
            me.$root.$children[0].success('일시정지 되었습니다.');
            me.makeList();
          });

      },
      resumeInstance: function (instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/start';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({}).then(
          function (response) {
            me.$root.$children[0].success('재시작되었습니다.');
            me.makeList();
          });
      },
      makeList: function () {
        var me = this;
        me.items = [];
        var filter = this.filter;
        var hasFilter = false;
        $.each(this.filter, function (obj, value) {
          if (value && value != '') {
            hasFilter = true;
          }
        })

        var url = hasFilter ? 'instances/search/findFilterICanSee?' : 'instances?';
        url += 'page=' + (me.page - 1) + '&size=' + me.size + '&sort=instId,desc';

        if (hasFilter) {
          $.each(this.filter, function (obj, value) {
            if (value && value != '') {
              url = url + "&" + obj + "=" + value;
            }
          })
        }

        var instances = {};
        var items = [];
        me.backend.$bind(url, instances);
        instances.$load().then(function () {
          $.each(instances, function (i, instance) {
            let split = instance.$bind.self.split('/');
            instance['instId'] = split[split.length - 1];

            //TODO 최상단 인스턴스일 경우에만 보이도록 하고 있는데... 하위 인스턴스도 보이도록?
            if (instance['instId'] == instance.rootInstId || instance.rootInstId == null) {
              items.push(instance);
            }
          });
          me.items = items;
          me.total = instances.$resource.page.totalElements;
        });
      },
      setStatus: function (status) {
        this.status = status;
      },
      openUserPicker(roleName) {
        var me = this;
        me.roleName = roleName;
        me.roles[0].name = roleName;
        me.$refs['userPicker'].openUserPicker();
      },
      userSelected: function (item, role) {
        this.filter.endpoint = item;
        console.log(this.filter);
      },
      onSelect: function (item) {
        //selected instance list
      },
      onClickList: function (instId) {
        //move to instance detail page
        this.$router.push({
          path: 'instance/' + instId + '/' + instId
        })
      },
      onPagination(val) {
        this.size = val.size;
        this.page = val.page;
        this.makeList();
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }

  td {
    text-align: center;
  }
</style>

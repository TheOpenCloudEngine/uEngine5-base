<template>
    <div>
      <div style="float:left; width: 13%; min-width: 250px;" v-if="windowWidth > 920">
        <!--<md-toolbar md-theme="white">-->
        <!--<span class="md-title">인스턴스 검색</span>-->
        <!--</md-toolbar>-->
        <md-list class="md-double-line">
          <md-list-item>
            <md-input-container>
              <label for="status">상태</label>
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
          </md-list-item>
          <md-list-item>
            <md-input-container md-clearable>
              <label>인스턴스 아이디</label>
              <md-input v-model="filter.instId"></md-input>
            </md-input-container>
          </md-list-item>
          <md-list-item>
            <md-input-container md-clearable>
              <label>이름</label>
              <md-input v-model="filter.defId"></md-input>
            </md-input-container>
          </md-list-item>
          <md-list-item>
            <div class="md-list-text-container">
              <span>시작자</span>
              <user-autocomplete
                :role="role"
                v-if="role"
                @userSelected:user:role="userSelected"
              ></user-autocomplete>
            </div>
            <!--<md-input-container md-clearable>-->
            <!--<label>시작자</label>-->
            <!--<md-input v-model="filter.defName"></md-input>-->
            <!--</md-input-container>-->

            <!--<md-button class="md-icon-button md-raised md-primary" @click="openUserPicker('starter')">-->
            <!--<md-icon style="color: #ffffff">search</md-icon>-->
            <!--</md-button>-->
          </md-list-item>
          <md-list-item>
            <div class="md-list-text-container">
              <span>현담당자</span>
              <user-autocomplete
                :role="role"
                v-if="role"
                @userSelected:user:role="userSelected"
              ></user-autocomplete>
            </div>
            <!--<md-input-container md-clearable>-->
            <!--<label>현담당자</label>-->
            <!--<md-input v-model="filter.endpoint"></md-input>-->
            <!--</md-input-container>-->
            <!--<md-button class="md-icon-button md-raised md-primary" @click="openUserPicker('endpoint')">-->
            <!--<md-icon style="color: #ffffff">search</md-icon>-->
            <!--</md-button>-->
            <!--<user-picker-->
            <!--:roles.sync="roles"-->
            <!--:id="id"-->
            <!--ref="userPicker"-->
            <!--style="min-width: 70%;"></user-picker>-->
          </md-list-item>
          <md-list-item>
            <div class="md-list-text-container">
              <span>시작일</span>
              <md-input-container md-clearable>
                <md-input type="date" placeholder="Start Date" v-model="filter.startedDate"></md-input>
              </md-input-container>
            </div>
          </md-list-item>
          <md-list-item>
            <div class="md-list-text-container">
              <span>종료일</span>
              <md-input-container md-clearable>
                <md-input type="date" md-format="yyyy/mm/dd" placeholder="End Date"
                          v-model="filter.finishedDate"></md-input>
              </md-input-container>
            </div>
          </md-list-item>
          <md-list-item>
            <md-button class="md-raised md-primary" v-on:click="search(1, 10)">Search</md-button>
          </md-list-item>
        </md-list>
      </div>
      <div>
        <md-table-card>
          <md-table @select="onSelect">
            <md-table-header>
              <md-table-row>
                <md-table-head style="text-align: center" v-for="header in headers" :key="header.text">{{header.text}}</md-table-head>
              </md-table-row>
            </md-table-header>
            <md-table-body v-if="items.length > 0">
              <md-table-row v-for="item in items" :md-item="item" md-auto-select md-selection style="cursor:pointer">
                <md-table-cell @click.native="onClickList(item.instId)">{{item.status}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" >{{item.instId}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 1724">{{item.defId}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)">{{item.defName}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)"  v-if="windowWidth > 1724">{{item.endpoint}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 660">{{item.endpoint}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 1724">{{item.info}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 1724">{{item.startedDate}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 1724">{{item.finishedDate}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 1724">{{item.ext1}}</md-table-cell>
                <md-table-cell @click.native="onClickList(item.instId)" v-if="windowWidth > 660">{{item.instId}}</md-table-cell>
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
            :md-size="paging.rowSize"
            :md-total="paging.rowTotal"
            :md-page="paging.rowStart"
            md-label="Rows"
            md-separator="of"
            :md-page-options="[5, 10, 20]"
            @pagination="onPagination($event)"></md-table-pagination>
        </md-table-card>
      </div>
    </div>
</template>
<script>

  export default {
    props: {
      iam: Object,
      backend: Object
    },
    watch: {
      windowWidth: {
        handler: function(newVal) {
          var me = this
          if(this.windowWidth < 660) {
            me.headers = [
              {text: '상태', value: 'status'},
              {text: '아이디', value: 'instId'},
              {text: '프로세스명', value: 'defName'},
              {text: '액션'}
            ]
          } else if(this.windowWidth < 1725) {
            me.headers = [
              {text: '상태', value: 'status'},
              {text: '아이디', value: 'instId'},
              {text: '프로세스명', value: 'defName'},
              {text: '현담당자', value: 'eventHandler'},
              {text: '삭제', value: 'instId'},
              {text: '액션'}
            ]
          } else {
            me.headers = [
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
              {text: '삭제', value: 'instId'},
              {text: '액션'}
            ]
          }
        },
      }
    },
    data() {
      return {
        status: 'All',
        windowWidth: 0,
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
          {text: '삭제', value: 'instId'},
          {text: '액션'}
        ],
        items: [],
        paging: {
          rowSize: 0,
          rowTotal: 0,
          rowStart: 0
        },
        listMode: 'default',
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
        }
      }
    },
    mounted() {
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
      this.listData(1, 10);
      this.$nextTick(function() {
        window.addEventListener('resize', this.getWindowWidth);
        //Init
        this.getWindowWidth()
      })
    },
    methods: {
      stopInstance: function(instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/stop';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({
        }).then(
          function (response) {
            me.$root.$children[0].success('중지되었습니다.');
            me.listData(me.paging.rowStart, 10);
        });

      },
      pauseInstance: function(instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/stop';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({
        }).then(
          function (response) {
            me.$root.$children[0].success('일시정지 되었습니다.');
            me.listData(me.paging.rowStart, 10);
        });

      },
      resumeInstance: function(instanceId) {
        var me = this;
        var url = 'instance/';
        url = url + instanceId + '/start';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$create({
        }).then(
          function (response) {
            me.$root.$children[0].success('재시작되었습니다.');
            me.listData(me.paging.rowStart, 10);
        });

      },
      listData(_page, _size) {
        var me = this;
        var url = 'instances?'
        var page = _page - 1;
        url += 'page=' + page + '&size=' + _size + '&sort=instId,desc';
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$load().then(function () {
          var instances = [];
          var resource = instance.$resource;
          $.each(instance, function (i, instance) {
            let split = instance.$bind.self.split('/');
            instance['instId'] = split[split.length - 1];
            //최상단 인스턴스일 경우에만 보이도록 한다.
            if (instance['instId'] == instance.rootInstId || instance.rootInstId == null) {
              instances.push(instance);
            }
          });
          me.paging = {
            rowSize: _size,
            rowTotal: resource.page.totalElements,
            rowStart: _page
          };
          me.items = instances;
        });
      },
      search(_page, _size) {
        var me = this;
        var url = 'instances/search/findFilterICanSee?';
        var filter = this.filter;
        $.each(this.filter, function (obj, value) {
          if (value != undefined && value != '') {
            if (url.indexOf("?") != url.length - 1) {
              url = url + "&" + obj + "=" + value;
            } else {
              url = url + obj + "=" + value;
            }
          }
        })
        var page = _page - 1;
        url += '&page=' + page + '&size=' + _size + '&sort=instId,desc';
        me.listMode = 'select';
        me.items = [];
        var instance = {};
        me.backend.$bind(url, instance);
        instance.$load().then(function () {
          var items = [];
          var resource = instance.$resource;
          $.each(instance, function (i, filteredData) {
            let split = filteredData.$bind.self.split('/');
            items['instId'] = split[split.length - 1];
            items.push({
              instId: split[split.length - 1],
              defName: filteredData.defName,
              defId: filteredData.defId,
              name: filteredData.name,
              status: filteredData.status,
              endpoint: filteredData.endpoint,
              eventHandler: filteredData.eventHandler,
              isSubProcess: filteredData.isSubProcess,
              startedDate: filteredData.startedDate,
              info: filteredData.info,
              ext1: filteredData.ext1,
              finishedDate: filteredData.finishedDate
            });
          });
          me.paging = {
            rowSize: _size,
            rowTotal: resource.page.totalElements,
            rowStart: _page
          };
          me.items = items;
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
      onPagination(e) {
        if (this.listMode == 'select') {
          this.search(e.page, e.size);
        } else {
          this.listData(e.page, e.size);
        }
      },
      getWindowWidth(event) {
        this.windowWidth = document.documentElement.clientWidth;
      }
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.getWindowWidth);

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

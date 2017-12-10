<template>
  <div>
    <md-speed-dial md-open="hover" md-direction="left" class="md-fab-top-right" md-theme="purple">
      <md-button class="md-fab" md-fab-trigger>
        <md-icon md-icon-morph>ondemand_video</md-icon>
        <md-icon>add</md-icon>
      </md-button>

      <md-button class="md-fab md-primary md-mini md-clean" v-on:click="newProcess">
        <md-icon>ondemand_video</md-icon>
      </md-button>

      <md-button class="md-fab md-primary md-mini md-clean" @click="newPackage">
        <md-icon>folder</md-icon>
      </md-button>
    </md-speed-dial>

    <new-package
      ref="newPackage"
      :currentPath="current"
      :directory.sync="directory"
      :cards.sync="cards"
      style="min-width: 70%;"></new-package>

    <div class="side-margin">
      <md-layout>
        <ul class="breadcrumb">
          <li v-for="item in breadcrumb" @dragover.prevent @drop="drop(item)">
            <a v-on:click="selectedNavigation(item.path, item.seq)" style="cursor:pointer">{{item.name}}</a>
          </li>
        </ul>
      </md-layout>
      <md-layout v-if="directory.length > 0">
        Package
      </md-layout>
      <md-layout>
        <md-layout md-gutter="24">
          <md-layout md-flex-xsmall="100" md-flex-small="50" md-flex-medium="25" md-flex="25"
                     v-for="item in directory"
                     draggable="true"
                     @dragstart.native="dragover(item)"
                     @drop.native="drop(item)"
          >
            <md-card class="folder-card" @dblclick.native="selectedFolder(item.name)" style="cursor:pointer">
              <md-card-header>
                <md-card-header-text class="ellipsis">
                  <md-icon class="folder-icon">folder</md-icon>
                  <span class="folder-title">{{ item.name }}</span>
                  <md-menu md-direction="bottom-end">
                    <md-icon class="folder-menu" md-menu-trigger>more_vert</md-icon>
                    <md-menu-content>
                      <md-menu-item id="renamePackage" @click.native="doChange(item.path, item.name)">
                        <span>rename</span>
                        <md-icon>edit</md-icon>
                      </md-menu-item>

                      <md-menu-item id="deletePackage" @click.native="doDelete(item.path)">
                        <span>Delete</span>
                        <md-icon>delete</md-icon>
                      </md-menu-item>
                    </md-menu-content>
                  </md-menu>
                </md-card-header-text>
              </md-card-header>
            </md-card>

          </md-layout>
          <rename-package
            ref="renamePackage"
            :packageName="changeName"
            :packagePath="changePath"
            :currentPath="current"
            :directory.sync="directory"
            :cards.sync="cards"
            style="min-width: 70%;"></rename-package>
          <delete-package
            ref="deletePackage"
            :packageName="deletePath"
            :currentPath="current"
            :directory.sync="directory"
            :cards.sync="cards"
            style="min-width: 70%;"></delete-package>
        </md-layout>
      </md-layout>
      <md-layout v-if="cards.length > 0">
        Process
      </md-layout>
      <md-layout mo-gutter>
        <md-layout md-gutter="24">
          <md-layout md-flex-xsmall="100" md-flex-small="50" md-flex-medium="25" md-flex="25"
                     v-for="card in cards"
                     :key="card.name"
                     draggable="true"
                     @dragstart.native="dragover(card)"
          >
            <md-card md-with-hover>
              <md-card-area>
                <md-card-media>
                  <img :src="card.src">
                </md-card-media>

                <md-card-header>
                  <div class="md-title">{{card.name}}</div>
                </md-card-header>

                <md-card-content>
                  {{card.desc}}
                </md-card-content>
              </md-card-area>

              <md-card-actions>
                <md-button v-on:click="initiateProcess(card)">Activate</md-button>
                <md-button v-on:click="move(card)">Edit</md-button>
              </md-card-actions>
              <md-card-actions>
                <md-button id="movePackage" @click="movePackage(card.name)">Move</md-button>
                <md-button v-on:click="deleteProcess(card.name, card.packagePath)">Delete</md-button>
              </md-card-actions>
            </md-card>
          </md-layout>
        </md-layout>
      </md-layout>
    </div>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        cards: [],
        directory: [],
        breadcrumb: [
          {
            seq: 1,
            name: 'Home',
            path: ''
          }
        ],
        current: '',
        treeData: {
          name: 'Package List',
          children: []
        },
        processName: "",
        changePath: "",
        changeName: "",
        deletePath: "",
        draggableItem: ""
      }
    },
    mounted() {
      var me = this;
      $('.scroll-inner').slimScroll({
        height: '100%'
      });
      this.getDefinitionList('');
    },
    methods: {
      dragover: function (item) {
        var me = this;
        me.draggableItem = [];
        me.draggableItem = item;
      },
      drop: function(item) {
        var me = this;
        //드래그 앤 드롭으로 패키지 및 프로세스를 이동시킨다.
        var me = this;
        var src = 'definition/' + me.draggableItem.path;
        var path = item.path + "/" + me.draggableItem.name;
        this.$root.codi(src).update({ path : path })
          .then(function (response) {
              me.$root.$children[0].success('이동되었습니다.');
              me.getDefinitionList(me.current);
            },
            function (response) {
              me.$root.$children[0].error('이동할 수 없습니다.');
            }
          );
      },
      newPackage(ref) {
        this.$refs['newPackage'].openPackage();
      },
      movePackage(ref) {
        this.processName = ref;
        this.$refs['movePackage'].openPackage();
      },
      selectedFolder: function (_folder) {
        this.current += _folder + "/";
        this.getDefinitionList(this.current);
        var seq = this.breadcrumb.length + 1;

        this.breadcrumb.push({
          seq: seq,
          name: _folder,
          path: this.current
        });
      },
      selectedNavigation: function (_link, _seq) {
        this.current = _link;
        this.getDefinitionList(_link);

        var breadcrumbs = [];

        for(var i = 0; i < _seq; i ++) {
          breadcrumbs.push(this.breadcrumb[i]);
        }

        this.breadcrumb = breadcrumbs;
      },
      getDefinitionList: function (_folder) {
        var me = this;

        var access_token = localStorage["access_token"];
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var backend = hybind(serviceLocator.getServiceHost(), {headers:{'access_token': access_token}});

        var definitions = [];
        var url = "definition/" + _folder;

        backend.$bind(url, definitions);

        var cards = [];
        var folders = [];

        definitions.$load().then(function(definitions) {

          if (definitions) {

            definitions.forEach(function (definition) {
              if (definition.directory) {
                folders.push(definition);
              }else{

                cards.push(definition);

                definition.desc=name + '...';
                definition.src='/static/image/sample.png';

              }

            });

          }
        });

        me.directory = folders;
        me.cards = cards;
      },
      doChange: function (_link, _name) {
        var me = this;
        me.changePath = _link;
        me.changeName = _name;
        me.$refs['renamePackage'].openPackage();
      },
      doDelete: function (_link) {
        var me = this;
        me.deletePath = _link;
        me.$refs['deletePackage'].openPackage();
      },
      getPackageFile: function(_path, _cards) {
        var src = 'definitions/packages/' + _path + "/processes"; //패키지 내 파일 찾기
        var packageChildren = []; // 좌측 트리에 보여질 패키지 파일 리스트

        this.$root.codi(src).get()
          .then(function (response) {
            $.each(response.data, function (i, name) {
              name = name.replace('/', '');
              _cards.push({
                name: name,
                packagePath: _path,
                desc: name + '...',
                src: '/static/image/sample.png'
              })
              packageChildren.push(
                { name: name,
                  package: false,
                  children: []
                }
              )
            });
          })
        //packageChildren 배열에 패키지 안에 있는 파일들을 리스트로 가지고 있다가
        //this.treeData.children 안에 집어 넣어준다.
        //패키지 내에 파일이 존재한다면 좌측에 뿌려진다.
        this.treeData.children.push(
          { name: _path,
            package: true,
            children: packageChildren
          }
        );
      },
      newProcess: function () {
        var path = this.current.replace(/\//g, "_");
        if(path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path  + 'new-process-definition'
        })
      },
      move: function (card) {
        var path = this.current.replace(/\//g, "_");
        if(path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path + card.name.replace('.json', '')
        })
      },
      deleteProcess: function (card) {

        var me = this;

        var access_token = localStorage["access_token"];
        var serviceLocator = this.$root.$children[0].$refs['backend'];
        var backend = hybind(serviceLocator.getServiceHost(), {headers:{'access_token': access_token}});

        var definitions = [];
        var url = "definition" + me.current + "/" + card;
        backend.$bind(url, definitions);

        definitions.$delete().then(function(){
          me.$root.$children[0].success('리소스가 삭제되었습니다.');
          me.getDefinitionList(me.current);
        });

      },
      initiateProcess: function (card) {
        var me = this;

        card.instantiation.$create()
          .then(
            function (instance) {
              var instanceId = instance.instanceId;
              me.$root.$children[0].success('프로세스가 시작되었습니다.');
              me.$router.push({
                path: '/instance/' + instanceId
              })
            },
            function (response) {
              me.$root.$children[0].error('프로세스를 시작할 수 없습니다.');
            }
          );
      },
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }

  .md-layout {
    margin-bottom: 5px;
    margin-top: 5px;
  }

  .side-margin {
    margin: 10px;
  }

  .md-theme-default .folder-card {
    width: 500px;
    height: 80%;
  }

  .ellipsis {
    height: 100%;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }

  .folder-icon {
    color: #9a9a9a;
  }

  .folder-title {
    font-size: 12pt;
  }

  .md-menu .folder-menu {
    align: right;
    color: #9a9a9a;
    position: absolute;
    top: 15px; right: 0px;
  }

  /*** Breadcrumbs ***/
  /* Style the list */
  ul.breadcrumb {
    padding: 0px 0px;
    list-style: none;
  }

  /* Display list items side by side */
  ul.breadcrumb li {
    display: inline-block;
    font-size: 14px;
  }

  /* Add a slash symbol (/) before/behind each list item */
  ul.breadcrumb li+li:before {
    padding: 8px;
    color: black;
    content: ">\00a0";
  }

  ul.breadcrumb li:last-child {
    padding: 8px;
    color: black;
    font-weight: bolder;
  }

  /* Add a color to all links inside the list */
  ul.breadcrumb li a {
    color: #0275d8;
    text-decoration: none;
  }

  /* Add a color on mouse-over */
  ul.breadcrumb li a:hover {
    color: #01447e;
    background-color: #dadada;
    text-decoration: none;
  }

</style>

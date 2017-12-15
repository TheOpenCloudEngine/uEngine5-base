<template>
  <div>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <md-speed-dial md-open="hover" md-direction="left" class="md-fab-top-right" md-theme="purple">
      <md-button class="md-fab" md-fab-trigger>
        <md-icon md-icon-morph>ondemand_video</md-icon>
        <md-icon>add</md-icon>
      </md-button>

      <md-button class="md-fab md-primary md-mini md-clean" v-on:click="newProcess">
        <md-icon>ondemand_video</md-icon>
      </md-button>

      <md-button class="md-fab md-primary md-mini md-clean"
                 @click.native="selectedPackge = {}; $refs['create'].open()">
        <md-icon>folder</md-icon>
      </md-button>
    </md-speed-dial>
    <div class="side-margin">
      <md-layout>
        <ul class="breadcrumb">
          <li v-for="item in breadcrumb" @dragover.prevent @drop="drop(item)"
              @dragleave="onDragLeave(item, 'navigation')"
              @dragenter="onDragenter(item, 'navigation')">
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
                     @dragleave.native="onDragLeave(item, 'folder')"
                     @dragenter.native="onDragenter(item, 'folder')"
          >
            <md-card class="folder-card" @dblclick.native="selectedFolder(item.name)" :class="{ 'folder-hover' : item.name == folderName }">
              <md-card-header>
                <md-card-header-text class="ellipsis">
                  <md-icon class="folder-icon">folder</md-icon>
                  <span class="folder-title">{{ item.name }}</span>
                  <md-menu md-direction="bottom-end">
                    <md-icon class="folder-menu" md-menu-trigger>more_vert</md-icon>
                    <md-menu-content>
                      <md-menu-item
                        @click.native="selectedPackge = {'name' : item.name, 'path' : item.path}; $refs['update'].open()">
                        <span>Rename</span>
                        <md-icon>edit</md-icon>
                      </md-menu-item>
                      <md-menu-item @click.native="originPackage = item.path; $refs['move'].open();">
                        <span>Move</span>
                        <md-icon>folder_open</md-icon>
                      </md-menu-item>
                      <md-menu-item @click.native="selectedPackge = item; $refs['delete'].open()">
                        <span>Delete</span>
                        <md-icon>delete</md-icon>
                      </md-menu-item>
                    </md-menu-content>
                  </md-menu>
                </md-card-header-text>
              </md-card-header>
            </md-card>

          </md-layout>
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
                <md-button
                  @click.native="originPackage = card.path; $refs['move'].open();">
                  Move
                </md-button>
                <md-button v-on:click="deleteProcess(card.name, card.packagePath)">Delete</md-button>
              </md-card-actions>
            </md-card>
          </md-layout>
        </md-layout>
      </md-layout>
    </div>
    <!-- Move Form -->
    <md-dialog md-open-from="#fab" md-close-to="#fab" ref="move">
      <md-dialog-title>Move Pacakge</md-dialog-title>
      <md-dialog-content>
        <md-input-container>
          <label>Move Package List</label>
          <md-select v-model="selectedPackge" class="select-option">
            <md-option v-if="current !== package.path"
                       v-for="package in breadcrumb" :value="package.path">
              <span class="package-name">{{package.name}}</span>
            </md-option>
            <md-subheader v-if="current == ''" class="package-title">Home</md-subheader>
            <md-subheader v-if="current !== ''" class="package-title">
              {{current.substring(current.lastIndexOf("/")+1, current.length)}}
            </md-subheader>
            <md-option v-for="package in directory" :value="package.path">
              <md-icon class="fa fa-angle-right"></md-icon>
              <span class="package-name">{{package.name}}</span>
            </md-option>
          </md-select>
        </md-input-container>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="md-primary" @click.native="moveClose(); $refs['move'].close()">Move</md-button>
        <md-button class="md-primary" @click.native="$refs['move'].close()">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
    <!-- Update Form -->
    <!-- Insert Form -->
    <md-dialog md-open-from="#fab" md-close-to="#fab" ref="create">
      <md-dialog-title>New Package</md-dialog-title>
      <md-dialog-content>
        <md-input-container>
          <label>Package Name</label>
          <md-input v-model="selectedPackge.name" type="text" ></md-input>
        </md-input-container>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="md-primary" @click.native="insertClose(); $refs['create'].close()">Create</md-button>
        <md-button class="md-primary" @click.native="$refs['create'].close()">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
    <!-- Update Form -->
    <md-dialog md-open-from="#fab" md-close-to="#fab" ref="update">
      <md-dialog-title>Rename Package</md-dialog-title>
      <md-dialog-content>
        <md-input-container>
          <label>Package Name</label>
          <md-input v-model="selectedPackge.name" type="text"></md-input>
        </md-input-container>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="md-primary" @click.native="updateClose(); $refs['update'].close()">Rename</md-button>
        <md-button class="md-primary" @click.native="$refs['update'].close()">Close</md-button>
      </md-dialog-actions>
    </md-dialog>
    <!-- Delete Confirm -->
    <md-dialog-confirm
      :md-title="dc.title"
      :md-content-html="dc.contentHtml"
      :md-ok-text="dc.ok"
      :md-cancel-text="dc.cancel"
      @close="deleteClose"
      ref="delete">
    </md-dialog-confirm>
  </div>
</template>
<script>
  export default {
    props: {
      backend: Object
    },
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
        draggableItem: "",
        selectedPackge: "",
        folderName: "",
        originPackage: {}
      }
    },
    created: function () {
      this.dc = {
        title: 'Delete Package',
        contentHtml: '패키지를 삭제하면 패키지에 속한 프로세스도 함께 삭제됩니다. \<br /\> 패지키를 삭제하시겠습니까?',
        cancel: 'Close',
        ok: 'Delete',
      };
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
      onDragenter: function (item, type) {
        var me = this;
        if(type == "folder") {
          me.folderName = item.name;
        }
      },
      onDragLeave: function (item, type) {
        var me = this;
        if(type == "folder") {
          var parent = event.fromElement.parentElement.className;
          if(parent.match("md-layout") != null) {
            me.folderName = "";
          }
        }
      },
      drop: function (item) {
        //드래그 앤 드롭으로 패키지 및 프로세스를 이동시킨다.
        var me = this;
        var src = 'definition/' + me.draggableItem.path;
        var path = item.path + "/" + me.draggableItem.name;

        if(path.indexOf("/") <= 0) path = path.substring(1, path.length);

        var packages = {path: path};
        this.onMove(src, packages);
      },
      onMove: function(src, packages) {
        src = src.replace(".xml", ".json");

        var me = this;
        me.backend.$bind(src, packages);
        packages.$save().then(
          function (response) {
            me.$root.$children[0].success('이동되었습니다.');
            me.getDefinitionList(me.current);
            me.folderName = "";
            me.selectedPackge = "";
          },
          function (response) {
            me.$root.$children[0].error('이동할 수 없습니다.');
            me.folderName = "";
            me.selectedPackge = "";
          }
        );
      },
      selectedFolder: function (_folder) {
        this.current += "/" + _folder;
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

        var definitions = [];
        var url = "definition" + _folder;

        me.backend.$bind(url, definitions);

        var cards = [];
        var folders = [];

        definitions.$load().then(function(definitions) {

          if (definitions) {

            definitions.forEach(function (definition) {
              if (definition.directory) {
                folders.push(definition);
              }else{

                cards.push(definition);

                definition.desc = name + '...';
                definition.src = '/static/image/sample.png';

              }

            });

          }
        });

        me.directory = folders;
        me.cards = cards;
      },
      getPackageFile: function (_path, _cards) {
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
                {
                  name: name,
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
          {
            name: _path,
            package: true,
            children: packageChildren
          }
        );
      },
      newProcess: function () {
        var path = this.current.replace(/\//g, "-");
        path = path.substring(1, path.length);
        if (path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path + 'new-process-definition'
        })
      },
      move: function (card) {
        var path = this.current.replace(/\//g, "-");
        path = path.substring(1, path.length);
        if (path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path + card.name.replace('.xml', '')
        })
      },
      deleteProcess: function (card) {

        var me = this;

        var definitions = [];
        var url = "definition" + me.current + "/" + card;
        me.backend.$bind(url, definitions);

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
      moveClose: function () {
        var me = this;
        var src = "definition/" + me.originPackage;
        var path = me.selectedPackge;
        var fileName = src.substring(src.lastIndexOf("/"), src.length);
        path = path + fileName;

        //path 값의 앞에 / 가 붙을 경우 uengine5에서 제공하는 API에 어긋나는 path 값이 들어가게 된다.
        //따라서, /의 위치를 확인하고 맨 앞에 올 시 잘라내줘야 한다.
        if(path.indexOf("/") == 0) {
          path = path.substring(1, path.length);
        }

        var packages = {path: path};
        this.onMove(src, packages);
      },
      insertClose: function () {
        var me = this;
        var path = me.current;
        if (path !== "") path += "/";
        var packages = [];
        me.backend.$bind('definition', packages);
        packages.$create({
          name: path + me.selectedPackge.name,
          directory: true
        }).then(
          function (response) {
            me.$root.$children[0].success('저장되었습니다.');
            me.getDefinitionList(me.current);
          },
          function (response) {
            me.$root.$children[0].error('저장할 수 없습니다.');
          }
        );
      },
      updateClose: function () {
        var me = this;
        var oldPath = me.selectedPackge.path;
        var rename = me.selectedPackge.name;
        var src = 'definition/' + oldPath;
        var split = oldPath.split('/');
        var newPath = "";

        for (var i = 0; i < split.length; i++) {
          if (i + 1 == split.length) {
            newPath += rename;
          } else {
            newPath += split[i] + "/";
          }
        }
        var packages = {path: newPath};
        me.backend.$bind(src, packages);
        packages.$save().then(
          function (response) {

            me.$root.$children[0].success('변경되었습니다.');
            me.getDefinitionList(me.current);
          },
          function (response) {
            me.$root.$children[0].error('변경할 수 없습니다.');
          }
        );
      },
      deleteClose: function (type) {
        if (type == 'ok') {
          var me = this;
          var src = 'definition/' + me.selectedPackge.path;
          var packages = [];
          me.backend.$bind(src, packages);
          packages.$delete().then(
            function (response) {
              me.$root.$children[0].success('삭제되었습니다.');
              me.getDefinitionList(me.current);
            },
            function (response) {
              me.$root.$children[0].error('삭제할 수 없습니다.');
            }
          );
        }
      },
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .package-name {
    font-size: 15px;
  }

  .package-title {
    font-size: 15px;
  }

  .md-layout {
    margin-bottom: 5px;
    margin-top: 5px;
  }

  .side-margin {
    margin: 10px;
  }

  .md-theme-default .folder-card {
    width: 100%;
    height: 80%;
    cursor:pointer
  }

  .md-theme-default .folder-hover{
    background-color: #3f51b5;
    color: #ffffff;
    .folder-icon {
      color: #ffffff;
    }
    .md-menu .folder-menu {
      align: right;
      color: #ffffff;
      position: absolute;
      top: 15px;
      right: 0px;
    }
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

  /** move select box */
  .select-option {
    width: 250px;
  }

</style>

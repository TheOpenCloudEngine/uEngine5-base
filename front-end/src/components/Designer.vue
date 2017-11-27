<template>
  <div>
    <md-layout>
      <md-button class="md-raised md-primary" v-on:click="newProcess">New Process</md-button>
      <md-button class="md-raised md-primary" id="newPackage" @click="newPackage">New Package</md-button>
      <new-package
        ref="newPackage"
        :currentPath="current"
        style="min-width: 70%;"></new-package>
      <move-package
        ref="movePackage"
        :processName="processName"
        style="min-width: 70%;"></move-package>
    </md-layout>
    <md-layout>
      <ul class="breadcrumb">
        <li v-for="item in breadcrumb" v-on:click="selectedNavigation(item.link, item.seq)">{{item.name}}</li>
      </ul>
    </md-layout>
    <md-layout v-if="directory.length > 0">
      Folder
    </md-layout>
    <md-layout>
      <md-layout md-gutter="24">
        <md-layout md-flex-xsmall="100" md-flex-small="50" md-flex-medium="25" md-flex="25"
                   v-for="item in directory"
        >
          <md-card  @click.native="selectedFolder(item.name)">
            <md-card-header>
              <md-card-header-text>
                <md-avatar class="md-avatar-icon md-primary">
                  <md-icon>folder</md-icon>
                </md-avatar> {{ item.name }}
              </md-card-header-text>
            </md-card-header>
          </md-card>

        </md-layout>
      </md-layout>
    </md-layout>
    <md-layout v-if="cards.length > 0">
      File
    </md-layout>
    <md-layout mo-gutter>
      <md-layout md-gutter="24">
        <md-layout md-flex-xsmall="100" md-flex-small="50" md-flex-medium="25" md-flex="25"
                   v-for="card in cards"
                   :key="card.name"
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
            link: ''
          }
        ],
        current: '',
        treeData: {
          name: 'Package List',
          children: []
        },
        processName: ""
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
      newPackage(ref) {
        this.$refs['newPackage'].openPackage();
      },
      movePackage(ref) {
        this.processName = ref;
        this.$refs['movePackage'].openPackage();
      },
      selectedFolder: function (_folder) {
        this.current += '/' + _folder;
        this.getDefinitionList(this.current);
        var seq = this.breadcrumb.length + 1;

        this.breadcrumb.push({
          seq: seq,
          name: _folder,
          link: this.current
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
        var backend = hybind("http://localhost:8080", {headers:{'access_token': access_token}});

        var definitions = [];
        var url = "definition" + _folder;

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
        path = path.substring(1, path.length);
        if(path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path  + 'new-process-definition'
        })
      },
      move: function (card) {
        var path = this.current.replace(/\//g, "_");
        path = path.substring(1, path.length);
        if(path !== "") path += "/";
        this.$router.push({
          path: 'definition/' + path + card.name.replace('.json', '')
        })
      },
      deleteProcess: function (card) {

        var me = this;

        card.$delete().then(function(){
          me.$root.$children[0].success('리소스가 삭제되었습니다.');
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

  /*** Breadcrumbs ***/
  /* Style the list */
  ul.breadcrumb {
    padding: 10px 16px;
    list-style: none;
    background-color: #eee;
  }

  /* Display list items side by side */
  ul.breadcrumb li {
    display: inline;
    font-size: 14px;
  }

  /* Add a slash symbol (/) before/behind each list item */
  ul.breadcrumb li+li:before {
    padding: 8px;
    color: black;
    content: "/\00a0";
  }

  /* Add a color to all links inside the list */
  ul.breadcrumb li a {
    color: #0275d8;
    text-decoration: none;
  }

  /* Add a color on mouse-over */
  ul.breadcrumb li a:hover {
    color: #01447e;
    text-decoration: underline;
  }
</style>

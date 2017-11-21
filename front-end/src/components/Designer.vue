<template>
  <div>
    <md-button class="md-raised md-primary" v-on:click="newProcess">New Process</md-button>
    <md-button class="md-raised md-primary" id="newPackage" @click="newPackage">New Package</md-button>
    <new-package
      ref="newPackage"
      style="min-width: 70%;"></new-package>
    <move-package
      ref="movePackage"
      :processName="processName"
      style="min-width: 70%;"></move-package>

    <md-layout mo-gutter>
      <md-layout md-flex="20">
        <md-list>
          <list-package
            :model="treeData">
          </list-package>
        </md-list>
      </md-layout>
      <md-layout md-flex="80">
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

    </md-layout>
  </div>
</template>
<script>
  import MdListItem from "vue-material/src/components/mdList/mdListItemDefault";

  export default {
    components: {MdListItem},
    data () {
      return {
        cards: [],
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
      this.getDefinitionList();
    },
    methods: {
      newPackage(ref) {
        this.$refs['newPackage'].openPackage();
      },
      movePackage(ref) {
        this.processName = ref;
        this.$refs['movePackage'].openPackage();
      },
      getDefinitionList: function () {
        var me = this;

        var access_token = localStorage["access_token"];
        var backend = hybind("http://localhost:8080", {headers:{'access_token': access_token}});

        var definitions = [];

        backend.$bind("definition", definitions);

        var cards = [];


        definitions.$load().then(function(definitions) {

          if (definitions) {

            definitions.forEach(function (definition) {

              if (definition.directory) {
              }else{

                cards.push(definition);

                definition.desc=name + '...';
                definition.src='/static/image/sample.png';

              }

            });

          }
        });

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

              _cards.

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
        this.$router.push({
          path: 'definition/new-process-definition'
        })
      },
      move: function (card) {
        this.$router.push({
          path: 'definition/' + card.name.replace('.json', '')
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
</style>

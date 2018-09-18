<template>
  <div>
    <geometry-element
      selectable
      movable
      resizable
      connectable
      deletable
      :id.sync="activity.tracingTag"
      :x.sync="activity.elementView.x"
      :y.sync="activity.elementView.y"
      :width.sync="activity.elementView.width"
      :height.sync="activity.elementView.height"
      :_style.sync="style"
      :parentId.sync="activity.elementView.parent"
      :label.sync="activity.name.text"
      v-on:dblclick="showProperty"
      v-on:selectShape="closeComponentChanger(); selectedActivity();"
      v-on:deSelectShape="closeComponentChanger(); deSelectedActivity();"
      v-on:removeShape="closeComponentChanger"
      v-on:redrawShape="closeComponentChanger"
      v-on:addedToGroup="onAddedToGroup"
    >
      <geometry-rect
        :_style="{
          'fill-r': 1,
          'fill-cx': .1,
          'fill-cy': .1,
          'stroke-width': 3,
          fill: '#FFFFFF',
          'fill-opacity': 0,
          r: '10'
        }"
      >
      </geometry-rect>

      <sub-elements>

        <bpmn-loop-type :loopType="loopType"></bpmn-loop-type>
        <bpmn-state-animation :status="status" :type="type"></bpmn-state-animation>
      </sub-elements>
      <bpmn-sub-controller :type="type" :className="className"
                           :callee-definition-id="activity.definitionId"></bpmn-sub-controller>
    </geometry-element>

    <bpmn-property-panel
      :drawer.sync="drawer"
      :item.sync="activity"
    >
      <template slot="properties-contents">
        <md-input-container>
          <label>액티비티 명</label>
          <md-input type="text"
                    v-model="activity.name.text"></md-input>
        </md-input-container>
        <md-input-container>
          <label>연결 프로세스 정의</label>

          <md-autocomplete v-model="activity.definitionId" :list="recommendedDefinitionList"
                           :filter-list="fetchDefinitionList">
          </md-autocomplete>


        </md-input-container>


        <div v-if="activity.definitionId">
          <p>연결 변수 매핑</p>
          <bpmn-parameter-contexts
            :parameter-contexts="activity.variableBindings"
            :definition="definition"
            :callee-definition-id="activity.definitionId"
            :for-sub-process="true"
            :for-call-activity="true"
          ></bpmn-parameter-contexts>

          <p>연결 역할 매핑</p>
          <bpmn-role-parameter-contexts
            :parameter-contexts="activity.roleBindings"
            :callee-definition-id="activity.definitionId"
            :definition="definition">
          </bpmn-role-parameter-contexts>
        </div>


        <!--</md-input-container>-->
        <md-input-container>
          <label>Retry Delay</label>
          <md-input type="number"
                    v-model.number="activity.retryDelay"></md-input>
        </md-input-container>
      </template>
      <template slot="additional-tabs">

      </template>
    </bpmn-property-panel>
  </div>
</template>

<script>
  import IBpmn from '../IBpmn'

  export default {
    mixins: [IBpmn],
    name: 'bpmn-call-activity',
    props: {},
    computed: {
      defaultStyle() {
        return {}
      },
      type() {
        return 'Task'
      },
      className() {
        return 'org.uengine.kernel.bpmn.CallActivity'
      },
      createNew(newTracingTag, x, y, width, height) {
        return {
          _type: this.className(),
          name: {
            text: ''
          },
          tracingTag: newTracingTag,
          selected: false,
          definitionId: "",
          variableBindings: [],
          roleBindings: [],
          elementView: {
            '_type': 'org.uengine.kernel.view.DefaultActivityView',
            'id': newTracingTag,
            'x': x,
            'y': y,
            'width': width,
            'height': height,
            'style': JSON.stringify({})
          }
        }
      }
    },
    data: function () {
      return {
        rowData: [],
        recommendedDefinitionList: [{name: ''}]
      };
    },
    created: function () {

    },
    //매번 창을 열때 (창을 열때 activity 를 갱신시켜주는건 프로퍼티 패널에 장치가 되있음.) 리스트를 갱신하길 원함.
    //그러기 위해서는 watch 를 해야하는데, watch 대상은 activity 이다.
    watch: {
      drawer: function (editingMode) {
        console.log('editing mode changed');
        if (editingMode) {
          this.loadData();
        }
      },
    },
    mounted: function () {
      //데피니션 리스트 조회
      this.loadData();
      this.fetchDefinitionList();
    },
    methods: {
      loadData: function () {
        var me = this;
        var src = 'definitions/all';
        this.$root.codi(src).get()
          .then(function (response) {
            me.rowData = response.data;
            var definitions = [];
            $.each(response.data, function (i, definition) {
              var length = definition.length;
              var lastDot = definition.lastIndexOf('.') + 1;
              var fileName = definition.substring(lastDot, length);
              if (fileName == "json") {
                definition = definition.replace('.json', '');
                definitions.push({
                  name: definition
                })
              }
            });
            me.rowData = definitions;
          })
      },

      fetchDefinitionList(list, param) {
        var query = param;
        var me = this;
        var access_token = localStorage["access_token"];

        me.recommendedDefinitionList = [({name: '-----List-----'})];
        var search = {};

        //TODO 404 error. is findByDefIdContaining path exist in definision service?
        window.backend.$bind("definition/search/findByDefIdContaining?defId=" + query, search);

        search.$load().then(function (definition) {
          for (var i = 0; i < definition.length; i++) {

            var tmp = definition[i].name;
            me.recommendedDefinitionList.push({name: tmp});

          }
        });

//        me.recommendedDefinitionList.splice(0);
        return me.recommendedDefinitionList
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


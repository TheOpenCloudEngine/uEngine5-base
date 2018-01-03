<template>
  <md-sidenav class="md-right" ref="rightSidenav" @open="open('Right')" @close="close('Right')" id="test">
    <md-tabs>
      <md-tab :id="'properties' + _uid" md-label="Properties">

        <md-input-container v-if="tracingTag !== null">
          <label>액티비티 ID</label>
          <md-input v-model="tracingTag"
                    type="text"
                    maxlength="50"
                    required></md-input>
        </md-input-container>

        <slot name="properties-contents">
        </slot>
      </md-tab>

      <slot name="additional-tabs">
      </slot>

      <md-tab :id="'visual' + _uid" md-label="Visual">
        <div v-if="item.elementView">
          <md-layout>
            <md-layout>
              <md-input-container>
                <label>x</label>
                <md-input type="number"
                          v-model.number="x"></md-input>
              </md-input-container>
            </md-layout>
            <md-layout>
              <md-input-container>
                <label>y</label>
                <md-input type="number"
                          v-model.number="y"></md-input>
              </md-input-container>
            </md-layout>
          </md-layout>
          <md-layout>
            <md-layout>
              <md-input-container>
                <label>width</label>
                <md-input type="number"
                          v-model.number="width"></md-input>
              </md-input-container>
            </md-layout>
            <md-layout>
              <md-input-container>
                <label>height</label>
                <md-input type="number"
                          v-model.number="height"></md-input>
              </md-input-container>
            </md-layout>
          </md-layout>
        </div>

        <md-input-container v-for="(item, index) in style"
                            :item="item"
                            :index="index">
          <label>{{item.key}}</label>
          <md-input type="text"
                    v-model="style[index].value"></md-input>
        </md-input-container>
      </md-tab>

    </md-tabs>
  </md-sidenav>
</template>

<script>
  import BpmnVueFinder from './BpmnVueFinder'
  import BpmnComponentFinder from './BpmnComponentFinder'
  export default {
    mixins: [BpmnVueFinder, BpmnComponentFinder],
    name: 'bpmn-property-panel',
    props: {
      drawer: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      item: Object
    },
    computed: {},
    data: function () {
      var me = this;
      return {
        navigationDrawer: this.drawer,
        _item: this.item,
        preventWatch: false,
        x: null,
        y: null,
        width: null,
        height: null,
        style: [],
        active: null,
        tracingTag: null
      }
    },
    created: function () {

    },
    mounted: function () {

    },
    watch: {
      drawer: function (val) {
        this.navigationDrawer = val;
      },
      //프로퍼티 창이 오픈되었을 때 모델값을 새로 반영한다.
      navigationDrawer: {
        handler: function (val, oldval) {
          console.log('val', val);
          if (val == true) {
            this._item = this.item;

            if (this.item.elementView) {
              this.x = this.item.elementView.x;
              this.y = this.item.elementView.y;
              this.width = this.item.elementView.width;
              this.height = this.item.elementView.height;
            }

            //맵 형식의 스타일을 어레이타입으로 변형한다.
            var view = this.item.elementView || this.item.relationView;
            var style = [];
            if (view.style) {
              var itemStyle = JSON.parse(view.style);
              if (!$.isEmptyObject(itemStyle)) {
                for (var key in itemStyle) {
                  style.push({
                    key: key,
                    value: itemStyle[key]
                  });
                }
              }
              this.style = style;
            }

            if (this.item.tracingTag) {
              this.tracingTag = this.item.tracingTag;
            }

            //bpmnVue 에 프로퍼티 에디팅중임을 알린다.
            //프로퍼티 에디팅 중 데피니션 변화는 히스토리에 기록된다.
            this.bpmnVue.propertyEditing = true;
            this.$emit('update:drawer', true);

            this.toggleRightSidenav();
          } else {
            //프로퍼티 에디팅 해제.
            if (this.bpmnVue) {
              this.bpmnVue.propertyEditing = false;
            }
            this.$emit('update:drawer', false);

            this.closeRightSidenav();
          }
        }
      },
      x: function (val) {
        this._item.elementView.x = val;
        this.$emit('update:item', this._item);
      },
      y: function (val) {
        this._item.elementView.y = val;
        this.$emit('update:item', this._item);
      },
      width: function (val) {
        this._item.elementView.width = val;
        this.$emit('update:item', this._item);
      },
      height: function (val) {
        this._item.elementView.height = val;
        this.$emit('update:item', this._item);
      },
      style: {
        handler: function (newVal, oldVal) {
          var style = {};
          if (newVal && newVal.length) {
            $.each(newVal, function (i, item) {
              style[item.key] = item.value;
            });
          }
          var view = this._item.elementView || this._item.relationView;
          view.style = JSON.stringify(style);
          this.$emit('update:item', this._item);
        },
        deep: true
      },

      //모델러에 의해 tracingTag 가 변경되었을 경우.
      tracingTag: function (value) {
        var me = this;
        //동일함.
        if (me._item.tracingTag == value) {

        }
        //이미 있음.
        else if (me.bpmnVue.checkExistTracingTag(value)) {
          console.log('TracingTag aleardy exist.');
        }
        //트레이싱 태그 값이 바뀜.
        else if (value && value.length > 0) {
          var oldTracingTag = me._item.tracingTag;

          //해당 액티비티 업데이트.
          me._item.tracingTag = value;
          me.$emit('update:item', me._item);

          //해당 트레이싱 태그를 사용중인 릴레이션의 source,target 을 변경한다.
          var sequenceFlows = me.bpmnVue.data.definition.sequenceFlows;
          if (sequenceFlows && sequenceFlows.length) {
            $.each(sequenceFlows, function (i, relation) {
              if (relation.sourceRef == oldTracingTag) {
                relation.sourceRef = value;
              }
              if (relation.targetRef == oldTracingTag) {
                relation.targetRef = value;
              }
            });
          }
        }
      }
    },
    mounted: function () {

    },
    methods: {
      open(ref) {
        this.navigationDrawer = true;
        console.log('Opened: ' + ref);
      },
      close(ref) {
        this.navigationDrawer = false;
        console.log('Closed: ' + ref);
      },
      closeRightSidenav() {
        this.$refs.rightSidenav.close();
      },
      toggleRightSidenav() {
        this.$refs.rightSidenav.toggle();
      },
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      }
    }
  }
</script>


<style lang="scss" rel="stylesheet/scss">
  .md-sidenav .md-sidenav-content {
    width: 400px
  }
  .md-sidenav.md-right .md-sidenav-content {
    width: 600px;
  }

</style>


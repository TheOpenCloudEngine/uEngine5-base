<template>
  <md-sidenav class="md-right" ref="rightSidenav" @open="open('Right')" @close="close('Right')">
    <md-tabs v-if="navigationDrawer">
      <md-tab :id="'properties' + _uid" md-label="Properties" selected>

        <slot name="properties-contents">
        </slot>
      </md-tab>

      <slot name="additional-tabs">
      </slot>

      <md-tab :id="'visual' + _uid" md-label="Visual">
        <div v-if="value.elementView">
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
  export default {
    name: 'modeling-property-panel',
    props: {
      drawer: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      value: Object
    },
    computed: {},
    data: function () {
      var me = this;
      return {
        navigationDrawer: this.drawer,
        _item: this.value,
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
      value: {
        handler: function () {
          this.$emit("input", this.value);
        },
        deep: true
      },
      //프로퍼티 창이 오픈되었을 때 모델값을 새로 반영한다.
      navigationDrawer: {
        handler: function (val, oldval) {

          console.log('val', val, this.value);
          if (val == true) {
            this._item = this.value;

            if (this.value.elementView) {
              this.x = this.value.elementView.x;
              this.y = this.value.elementView.y;
              this.width = this.value.elementView.width;
              this.height = this.value.elementView.height;
            }

            //맵 형식의 스타일을 어레이타입으로 변형한다.
            var view = this.value.elementView || this.value.relationView;
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

            if (this.value.tracingTag) {
              this.tracingTag = this.value.tracingTag;
            }

            //bpmnVue 에 프로퍼티 에디팅중임을 알린다.
            //프로퍼티 에디팅 중 데피니션 변화는 히스토리에 기록된다.
            if (this.bpmnVue) {
              this.bpmnVue.propertyEditing = true;
            }
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
        this.$emit('update:value', this._item);
      },
      y: function (val) {
        this._item.elementView.y = val;
        this.$emit('update:value', this._item);
      },
      width: function (val) {
        this._item.elementView.width = val;
        this.$emit('update:value', this._item);
      },
      height: function (val) {
        this._item.elementView.height = val;
        this.$emit('update:value', this._item);
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
          this.$emit('update:value', this._item);
        },
        deep: true
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


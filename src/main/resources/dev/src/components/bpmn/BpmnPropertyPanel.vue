<template>
  <v-navigation-drawer class="property-panel"
                       temporary
                       v-model="navigationDrawer"
                       right
                       light
                       overflow
                       absolute
  >
    <v-tabs light v-model="active"
            :scrollable="false"
            centered>
      <v-tabs-bar class="cyan">
        <v-tabs-item ripple :href="'#properties' + _uid">
          Properties
        </v-tabs-item>
        <slot name="additional-tabs">
        </slot>

        <v-tabs-item
          ripple
          :href="'#visual' + _uid">
          Visual
        </v-tabs-item>
        <v-tabs-slider class="primary"></v-tabs-slider>
      </v-tabs-bar>
      <v-tabs-items>
        <v-tabs-content :id="'properties' + _uid">
          <v-layout v-if="tracingTag" row wrap class="pa-3">
            <v-flex xs12>
              <v-text-field
                label="액티비티 ID"
                counter
                max="50"
                v-model="tracingTag"
                :rules="[rules.required, rules.tracingTag]"
              ></v-text-field>
            </v-flex>
          </v-layout>
          <slot name="properties-contents">
          </slot>
        </v-tabs-content>

        <slot name="additional-tabs-contents">

        </slot>
        <v-tabs-content :id="'visual' + _uid">
          <v-layout row wrap class="pa-3">
            <v-flex xs6>
              <v-text-field
                type="number"
                label="x"
                v-model.number="x"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="y"
                v-model.number="y"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="width"
                v-model.number="width"
              ></v-text-field>
            </v-flex>
            <v-flex xs6>
              <v-text-field
                type="number"
                label="height"
                v-model.number="height"
              ></v-text-field>
            </v-flex>
            <v-flex xs12 v-for="(item, index) in style"
                    :item="item"
                    :index="index">
              <v-text-field
                type="text"
                :label="item.key"
                v-model="style[index].value"
              ></v-text-field>
            </v-flex>
          </v-layout>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </v-navigation-drawer>
</template>

<script>
  export default {
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
        bpmnVue: null,
        navigationDrawer: this.drawer,
        _item: this.item,
        preventWatch: false,
        x: null,
        y: null,
        width: null,
        height: null,
        style: [],
        active: null,
        tracingTag: null,
        bpmnComponent: null,
        rules: {
          required: function (value) {
            if (!value || value.length < 1) {
              return 'Required.';
            } else {
              return true;
            }
          },
          tracingTag: function (value) {
            //동일함.
            if (me._item.tracingTag == value) {
              return true;
            }
            //이미 있음.
            else if (me.bpmnVue.checkExistTracingTag(value)) {
              return 'TracingTag aleardy exist.';
            }
            //트레이싱 태그 값이 바뀜.
            else if (value && value.length > 0) {
              var oldTracingTag = me._item.tracingTag;

              //해당 액티비티 업데이트.
              me._item.tracingTag = value;
              me.$emit('update:item', me._item);

              //해당 트레이싱 태그를 사용중인 릴레이션의 source,target 을 변경한다.
              var sequenceFlows = me.bpmnVue.filteredDefinition.sequenceFlows;
              $.each(sequenceFlows, function (i, relation) {
                if (relation.sourceRef == oldTracingTag) {
                  relation.sourceRef = value;
                }
                if (relation.targetRef == oldTracingTag) {
                  relation.targetRef = value;
                }
              });
              return true;
            } else {
              return 'Invalid TracingTag.';
            }
          }
        }
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
          if (val == true) {
            this._item = this.item;
            this.x = this.item.elementView.x;
            this.y = this.item.elementView.y;
            this.width = this.item.elementView.width;
            this.height = this.item.elementView.height;
            //맵 형식의 스타일을 어레이타입으로 변형한다.
            var style = [];
            if (this.item.elementView.style) {
              var itemStyle = JSON.parse(this.item.elementView.style);
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

            //이 프로퍼티 패널의 BpmnVue 를 등록한다.
            var bpmnVue = null;
            var parent;
            var getParent = function (component) {
              parent = component.$parent;
              if (parent) {
                if (parent.bpmnRole == 'bpmn-vue') {
                  bpmnVue = parent;
                } else {
                  getParent(parent);
                }
              }
            }
            getParent(this);
            this.bpmnVue = bpmnVue;

            //bpmnVue 에 프로퍼티 에디팅중임을 알린다.
            //프로퍼티 에디팅 중 데피니션 변화는 히스토리에 기록된다.
            this.bpmnVue.propertyEditing = true;
            this.$emit('update:drawer', true);
          } else {
            //프로퍼티 에디팅 해제.
            if (this.bpmnVue) {
              this.bpmnVue.propertyEditing = false;
            }
            this.$emit('update:drawer', false);
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
          this._item.elementView.style = JSON.stringify(style);
          this.$emit('update:item', this._item);
        },
        deep: true
      }
    },
    mounted: function () {

    },
    methods: {
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


<style scoped lang="scss" rel="stylesheet/scss">

</style>


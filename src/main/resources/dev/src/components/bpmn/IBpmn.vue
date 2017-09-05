<template>

</template>

<script>
  export default {
    name: 'bpmn-base',
    props: {
      activity: Object,
      role: Object,
      relation: Object,
      canvas: Object
    },
    data: function () {
      return {
        elementId: null,
        formStyle: [],
        shape: null,
        preventWatch: false
      }
    },
    computed: {
      className(){
        return ''
      },
      shapeId(){
        return ''
      },
      x: {
        get: function () {
          return this.getView('x');
        },
        set: function (val) {
          this.setView('x', val);
        }
      },
      y: {
        get: function () {
          return this.getView('y');
        },
        set: function (val) {
          this.setView('y', val);
        }
      },
      width: {
        get: function () {
          return this.getView('width');
        },
        set: function (val) {
          this.setView('width', val);
        }
      },
      height: {
        get: function () {
          return this.getView('height');
        },
        set: function (val) {
          this.setView('height', val);
        }
      },
      label: {
        get: function () {
          return this.getView('label');
        },
        set: function (val) {
          this.setView('label', val);
        }
      },
      parent: {
        get: function () {
          return this.getView('parent');
        },
        set: function (val) {
          this.setView('parent', val);
        }
      },
      id: {
        get: function () {
          return this.getView('id');
        },
        set: function (val) {
          this.setView('id', val);
        }
      },
      from: {
        get: function () {
          return this.getView('from');
        },
        set: function (val) {
          this.setView('from', val);
        }
      },
      to: {
        get: function () {
          return this.getView('to');
        },
        set: function (val) {
          this.setView('to', val);
        }
      },
      value: {
        get: function () {
          return this.getView('value');
        },
        set: function (val) {
          this.setView('value', val);
        }
      },
      /**
       * Vue는 이미 만들어진 인스턴스에 새로운 루트레벨의 반응형 속성을 동적으로 추가하는 것을 허용하지 않는다.
       * 따라서, Style 은 definition 단계에서 포함되어 오도록 한다.
       */
      style: {
        get: function () {
          try {
            var style = JSON.parse(this.getView('style'));
            if ($.isEmptyObject(style) && this.id) {
              var element = this.canvas.getElementById(this.id);
              return element.shape.geom.style.map;
            } else {
              return JSON.parse(this.getView('style'))
            }
          } catch (e) {
            return {};
          }
        },
        set: function (val) {
          try {
            this.setView('style', JSON.stringify(val));
          } catch (e) {
            this.setView('style', JSON.stringify({}));
          }
        }
      },
      geom: {
        get: function () {
          return this.getView('geom');
        },
        set: function (val) {
          this.setView('geom', val);
        }
      },
    },
    watch: {
      formStyle: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch && newVal && newVal.length) {
            var style = {};
            $.each(newVal, function (i, item) {
              style[item.key] = item.value;
            });
            this.style = style;
          }
          this.preventWatch = false;
        },
        deep: true
      },
      activity: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            if (newVal) {
              this.updateShape();
            } else {
              if (this.destroyShape) {
                this.destroyShape();
              }
            }
          }
          this.preventWatch = false;
        },
        deep: true
      },
      role: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            if (newVal) {
              this.updateShape();
            } else {
              if (this.destroyShape) {
                this.destroyShape();
              }
            }
          }
          this.preventWatch = false;
        },
        deep: true,
      },
      relation: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            if (newVal) {
              this.updateShape();
            } else {
              if (this.destroyShape) {
                this.destroyShape();
              }
            }
          }
          this.preventWatch = false;
        },
        deep: true,
      },
    },
    mounted: function () {
      this.updateShape();
    },
    //상위 컴포넌트에 의해 삭제된 경우.
    beforeDestroy: function () {
      var me = this;
      if (me.id && me.canvas) {
        let existElement = me.canvas.getElementById(me.id);
        if (existElement) {
          me.canvas.removeShape(existElement, true);
        }
      }
      //불특정한 상황의 컴포넌트 삭제시에는 watch 를 받아들이도록 한다.
      this.preventWatch = false;
    },
    destroyShape: function () {
      //발생
      //activity , role,  relation 이 null 로 들어왔을 때. => watch
      //컴포넌트의 element 가 있다면 캔버스에서 element 를 삭제한다.
      var me = this;
      if (me.id) {
        let existElement = me.canvas.getElementById(me.id);
        if (existElement) {
          me.canvas.removeShape(existElement, true);
        }
      }
    },
    methods: {
      getViewFromComponent: function () {
        if (this.activity) {
          return this.activity.elementView
        }
        if (this.role) {
          return this.role.elementView
        }
        if (this.relation) {
          return this.relation.relationView
        }
      },
      getView: function (property) {
        var view = this.getViewFromComponent();
        if (view) {
          return view[property];
        }
      },
      setView: function (property, val) {
        var view = this.getViewFromComponent();
        if (view) {
          view[property] = val;
        }
      },
      setGroup: function (element) {
        var me = this;
        //그룹위에 그려졌을 경우 그룹처리
        var frontGroup = me.canvas.getRenderer().getFrontForBoundary(me.canvas.getRenderer().getBoundary(element));

        if (!frontGroup) {
          return;
        }
        //draw 대상이 Edge 이면 리턴.
        if (me.canvas.getRenderer().isEdge(element)) {
          return;
        }
        //draw 대상이 Lane 인 경우 리턴.
        if (me.canvas.getRenderer().isLane(element)) {
          return;
        }
        //그룹이 Lane 인 경우 RootLane 으로 변경
        if (me.canvas.getRenderer().isLane(frontGroup)) {
          frontGroup = me.canvas.getRenderer().getRootLane(frontGroup);
        }
        if (!me.canvas.getRenderer()._CONFIG.GROUP_DROPABLE || !frontGroup.shape.GROUP_DROPABLE) {
          return;
        }
        //자신일 경우 반응하지 않는다.
        if (frontGroup.id === element.id) {
          return;
        }
        frontGroup.appendChild(element);
      },
      drawShape: function (element) {
        var me = this;
        var needToRedraw = false;

        //기존 도형이 있을 경우
        if (element) {
          //범위 요소에 따른 비교
          let boundary = me.canvas.getBoundary(element);
          if (boundary.getWidth() != me.width ||
            boundary.getHeight() != me.height ||
            boundary.getCentroid().x != me.x ||
            boundary.getCentroid().y != me.y ||
            element.shape.label != me.label
          ) {
            needToRedraw = true;
          }

          //shapeId 비교
          if (element.shape.SHAPE_ID != me.shapeId) {
            needToRedraw = true;
          }

          //스타일 비교
          if (JSON.stringify(me.style) != JSON.stringify(element.shape.geom.style.map)) {
            needToRedraw = true;
          }

          //부모 비교
          var parent = me.canvas.getRenderer().getParent(element);
          //기존 도형의 부모가 있을 경우
          if (parent && parent.id) {
            if (parent.id != me.parent) {
              needToRedraw = true;
            }
          }
          //기존 도형의 부모가 없는데 컴포넌트 부모값이 있을 경우
          else {
            if (me.parent) {
              needToRedraw = true;
            }
          }
        }
        //기존 도형이 없을 경우
        else {
          needToRedraw = true;
        }

        var createdElement;
        //신규로 생성하거나 기존 도형을 변경해야 하는 경우.
        if (needToRedraw) {
          console.log('needToRedraw', me.id);
          var shape = eval('new ' + me.shapeId + '(me.label)');

          //기존 도형은 삭제한다.
          try {
            me.canvas.removeShape(me.id, true);
          } catch (e) {

          }
          createdElement = me.canvas.drawShape([me.x, me.y], shape, [me.width, me.height], me.style, me.id, me.parent, true, true);
          me.elementId = createdElement.id;
          me.setGroup(createdElement);
        } else {
          createdElement = element;
          me.elementId = createdElement.id;
        }
        this.bindEvents(createdElement);

        //formStyle 등록.
        var formStyle = [];
        var createdStyle = createdElement.shape.geom.style.map;
        for (var key in createdStyle) {
          formStyle.push({
            key: key,
            value: createdStyle[key]
          });
        }
        //formStyle 업데이트로 인해 컴포넌트 리로딩 방지.
        me.preventWatch = true;
        me.formStyle = formStyle;
      },
      drawEdge: function (element) {
        var me = this;
        var needToRedraw = false;

        //기존 도형이 있을 경우
        if (element) {
          //변곡점과 라벨에 따른 비교
          if (element.shape.geom.vertices.toString() != me.value ||
            element.shape.label != me.label) {
            needToRedraw = true;
          }

          //shapeId 비교
          if (element.shape.SHAPE_ID != me.shapeId) {
            needToRedraw = true;
          }

          //from,to 비교
          if ($(element).attr('_from') != me.from ||
            $(element).attr('_to') != me.to
          ) {
            needToRedraw = true;
          }
        }
        //기존 도형이 없을 경우
        else {
          needToRedraw = true;
        }


        var createdElement;

        //신규로 생성하거나 기존 도형을 변경해야 하는 경우.
        if (needToRedraw) {
          console.log('needToRedraw', me.id);
          var list = JSON.parse('[' + me.value + ']');
          var geom = new OG.geometry.PolyLine(list);
          geom.type = 'PolyLine';
          var edgeShape = new OG.EdgeShape(list[0], list[list.length - 1], me.label);
          edgeShape.geom = geom;

          var addAttrValues = function (element, name, value) {
            var attrValue = $(element).attr(name),
              array = attrValue ? attrValue.split(",") : [],
              newArray = [];
            $.each(array, function (idx, item) {
              if (item !== value) {
                newArray.push(item);
              }
            });
            newArray.push(value);

            $(element).attr(name, newArray.toString());
            return element;
          };

          var fromShape = me.canvas.getElementById(me.relation.sourceRef);
          var toShape = me.canvas.getElementById(me.relation.targetRef);

          //기존 도형은 삭제한다.
          try {
            me.canvas.removeShape(me.id, true);
          } catch (e) {

          }
          createdElement = me.canvas.drawShape(null, edgeShape, null, null, me.id, null, true);
          me.elementId = createdElement.id;
          // 연결 노드 정보 설정
          if (me.from) {
            $(createdElement).attr("_from", me.from);
            addAttrValues(fromShape, "_toedge", me.id);
          }
          if (me.to) {
            $(createdElement).attr("_to", me.to);
            addAttrValues(toShape, "_fromedge", me.id);
          }

        } else {
          createdElement = element;
          me.elementId = createdElement.id;
        }
        this.bindEvents(createdElement);
      },
      updateShape: function () {
        var me = this;
        //릴레이션인 경우 아이디 지정.
        if (me.relation) {
          me.id = me.relation.sourceRef + '-' + me.relation.targetRef;
        }

        //신규 아이디와 기존 아이디가 틀릴 경우, 기존 아이디의 도형은 삭제한다.
        //이 경우는 상위 컴포넌트에서 아이디가 변경될 경우 해당됨.
        if (me.id && me.elementId && me.elementId != me.id) {
          var existElement = me.canvas.getElementById(me.elementId);
          if (existElement) {
            me.canvas.removeShape(existElement, true);
          }
        }

        let element = me.canvas.getElementById(me.id);
        if (me.activity || me.role) {
          me.drawShape(element);
        } else if (me.relation) {
          me.drawEdge(element);
        }
      },
      updateVue: function () {
        this.preventWatch = true;
        var me = this;
        var element = me.canvas.getElementById(me.id);
        let boundary = me.canvas.getBoundary(element);
        if (!element || !boundary) {
          return;
        }
        me.width = boundary.getWidth();
        me.height = boundary.getHeight();
        me.x = boundary.getCentroid().x;
        me.y = boundary.getCentroid().y;
        me.label = element.shape.label;

        var parent = me.canvas.getRenderer().getParent(element);
        if (parent) {
          me.parent = parent.id;
        } else {
          me.parent = null;
        }
        me.id = element.id;

        if (element.shape.TYPE === OG.Constants.SHAPE_TYPE.EDGE) {
          if ($(element).attr('_from')) {
            me.from = $(element).attr('_from');
          }
          if ($(element).attr('_to')) {
            me.to = $(element).attr('_to');
          }
        }

        if (element.shape.geom.vertices) {
          me.value = element.shape.geom.vertices.toString();
        }

        me.style = element.shape.geom.style.map;
        me.geom = null;

        //view 에 shapeId 등록
        if (me.activity || me.role) {
          me.setView('shapeId', me.shapeId);
          me.setView('_type', 'org.uengine.kernel.view.DefaultActivityView');
        }

        //java class 등록 && 트래이싱 태그 동기화
        if (me.activity) {
          me.activity._type = me.className;
          me.activity.tracingTag = me.id;
        }
      },
      bindEvents: function (element) {
        var me = this;
        if (!element) {
          return;
        }
        $(element).unbind('dblclick');
        $(element).bind('dblclick', function () {
          me.drawer = true;
        });

        element.shape.onResize = function (offset) {

        };

        element.shape.onDrawLabel = function (text) {

        };
        element.shape.onLabelChanged = function (text, beforeText) {

        };

        element.shape.onBeforeRemoveShape = function () {

        }

        element.shape.onRemoveShape = function () {

        }

        element.shape.onDrawShape = function () {

        }

        element.shape.onBeforeLabelChange = function () {

        }

        element.shape.onRedrawShape = function () {

        };

        element.shape.onBeforeConnectShape = function (edge, fromShape, toShape) {

        }

        element.shape.onConnectShape = function (edge, fromShape, toShape) {

        }

        element.shape.onDisconnectShape = function (edge, fromShape, toShape) {

        }

        element.shape.onGroup = function (groupShapeEle) {

        }

        element.shape.onUnGroup = function () {

        }

        element.shape.onMoveShape = function (offset) {

        }

        element.shape.onRotateShape = function (angle) {

        }

        element.shape.onDuplicated = function (target, duplicated) {

        }

        element.shape.onPasteShape = function (copied, pasted) {

        }
        element.shape.onAddToGroup = function (groupElement, elements, eventOffset) {

        }
        element.shape.onAddedToGroup = function (groupElement, element, eventOffset) {

        }

        element.shape.onSelectShape = function () {

        }

        element.shape.onDeSelectShape = function () {

        }
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>

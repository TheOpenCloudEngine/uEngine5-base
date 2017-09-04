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
      style: {
        get: function () {
          return this.getView('style');
        },
        set: function (val) {
          this.setView('style', val);
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
    data: function () {
      return {
        element: null,
        shape: null,
        preventWatch: false
      }
    },
    watch: {
      activity: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            console.log('activity changed');
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
      role: {
        handler: function (newVal, oldVal) {
          if (!this.preventWatch) {
            console.log('role changed');
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
            console.log('relation changed');
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
      console.log('Mounted!!', this);
      this.updateShape(true);
    },
    //상위 컴포넌트에 의해 삭제된 경우.
    beforeDestroy: function () {
      console.log('beforeDestroy!!', this.label);
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
      if (me.element) {
        let existElement = me.canvas.getElementById(me.element.id);
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
      drawShape: function (element) {
        var me = this;
        console.log('drawShape!!', me.label, '!!');
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

        var setGroup = function () {
          //그룹위에 그려졌을 경우 그룹처리
          var frontGroup = me.canvas.getRenderer().getFrontForBoundary(me.canvas.getRenderer().getBoundary(me.element));

          if (!frontGroup) {
            return;
          }
          //draw 대상이 Edge 이면 리턴.
          if (me.canvas.getRenderer().isEdge(me.element)) {
            return;
          }
          //draw 대상이 Lane 인 경우 리턴.
          if (me.canvas.getRenderer().isLane(me.element)) {
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
          if (frontGroup.id === me.element.id) {
            return;
          }
          frontGroup.appendChild(me.element);
        };

        if (needToRedraw) {
          console.log('needToRedraw', me.label);
          var shape = eval('new ' + me.shapeId + '(me.label)');
          me.element = me.canvas.drawShape([me.x, me.y], shape, [me.width, me.height], null, me.id, me.parent, true, true);
          setGroup();
        } else {
          me.element = element;
        }
        this.bindEvents(me.element);
      },
      drawEdge: function (element) {
        var me = this;
        var needToRedraw = false;
        //기존 도형이 있을 경우
        if (element) {
          //범위 요소에 따른 비교
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

        if (needToRedraw) {
          console.log('needToRedraw', me.label, me.id);
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

          me.element = me.canvas.drawShape(null, edgeShape, null, null, me.id, null, true);
          // 연결 노드 정보 설정
          if (me.from) {
            $(me.element).attr("_from", me.from);
            addAttrValues(fromShape, "_toedge", me.id);
          }
          if (me.to) {
            $(me.element).attr("_to", me.to);
            addAttrValues(toShape, "_fromedge", me.id);
          }

        } else {
          me.element = element;
        }
        this.bindEvents(me.element);
      },
      updateShape: function (isNew) {
        var me = this;
        //릴레이션인 경우 아이디 지정.
        if (me.relation) {
          me.id = me.relation.sourceRef + '-' + me.relation.targetRef;
        }

        var draw = function (element) {
          if (me.activity || me.role) {
            me.drawShape(element);
          } else if (me.relation) {
            me.drawEdge(element);
          }
        }

        // 마운트 되었을 때,
        if (isNew) {
          let element = me.canvas.getElementById(me.id);
          draw(element);
        }
        // 컴포넌트에 의한 값 변경시,
        else {
          let element = me.canvas.getElementById(me.id);

          //도형의 아이디가 변경된 경우
          //TODO 아래 순서를 프로퍼티 패널에서 트레이싱 태그 변경시 적용토록 한다.
          //me.element 는 이전에 등록된 element
          //해당 경우는 트레이신 태그를 가진 activity 에서만 통용된다.
          //프로퍼티 패널 아이디 변경
          //릴레이션 source, target 변경. from,to 를 source,target 아이디로 변경
          //릴레이션 아이디가 틀리게 옴.
          //선연결이 사라짐.
          //새로 선연결을 함.
          if (me.id && me.element && me.element.id && me.element.id != me.id) {
            var existElement = me.canvas.getElementById(me.element.id);
            if (existElement) {
              me.canvas.removeShape(existElement, true);
            }
          }
          draw(element);
        }
      },
      updateVue: function () {
        console.log('updateVue!!');
        this.preventWatch = true;
        var me = this;
        let boundary = me.canvas.getBoundary(me.element);
        if (!me.element || !me.element.id || !boundary) {
          return;
        }
        me.width = boundary.getWidth();
        me.height = boundary.getHeight();
        me.x = boundary.getCentroid().x;
        me.y = boundary.getCentroid().y;
        me.label = me.element.shape.label;

        var parent = me.canvas.getRenderer().getParent(me.element);
        if (parent) {
          me.parent = parent.id;
        } else {
          me.parent = null;
        }
        me.id = me.element.id;

        if (me.element.shape.TYPE === OG.Constants.SHAPE_TYPE.EDGE) {
          if ($(me.element).attr('_from')) {
            me.from = $(me.element).attr('_from');
          }
          if ($(me.element).attr('_to')) {
            me.to = $(me.element).attr('_to');
          }
        }

        if (me.element.shape.geom.vertices) {
          me.value = me.element.shape.geom.vertices.toString();
        }

        me.style = null;
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

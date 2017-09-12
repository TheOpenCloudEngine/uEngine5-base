<template>

</template>

<script>
  export default {
    name: 'opengraph-element',
    props: {
      redraw: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      id: String,
      parentId: String,
      x: {
        default: function () {
          return 0;
        },
        type: Number
      },
      y: {
        default: function () {
          return 0;
        },
        type: Number
      },
      width: {
        default: function () {
          return 10;
        },
        type: Number
      },
      height: {
        default: function () {
          return 10;
        },
        type: Number
      },
      angle: {
        default: function () {
          return 0;
        },
        type: Number
      },
      _style: {
        default: function () {
          return {};
        },
        type: Object
      },
      label: {
        default: function () {
          return '';
        },
        type: String
      },
      image: String,
      vertices: {
        default: function () {
          return [];
        },
        type: Array
      },
      html: String,
      xml: String,
      text: String,

      /**
       * 연결 정의
       */
      from: String,
      to: String,
      fromPosition: {
        default: function () {
          return [50, 50];
        },
        type: Array
      },
      toPosition: {
        default: function () {
          return [50, 50];
        },
        type: Array
      },

      /**
       * 서브 도형 프로퍼티
       */
      subWidth: {
        default: function () {
          return '100%';
        },
        type: [String, Number]
      },
      subHeight: {
        default: function () {
          return '100%';
        },
        type: [String, Number]
      },
      subTop: {
        default: function () {
          return '0px';
        },
        type: [String, Number]
      },
      subBottom: [String, Number],
      subLeft: {
        default: function () {
          return '0px';
        },
        type: [String, Number]
      },
      subRight: [String, Number],
      subAlign: String,
      subVerticalAlign: String,
      subStyle: {
        default: function () {
          return {};
        },
        type: Object
      }
    },
    computed: {
      opengraphRole: function () {
        return 'opengraph-element';
      }
    },
    data: function () {
      return {
        elementRole: null,
        shapdId: 'OG.shape.' + this.uuShapeId(),
        immidiateId: this.uuid(),
        canvasComponent: null,
        parentElementComponent: null,
        parentControllerComponent: null,
        element: null,
        shape: null,
        geometrys: {},
        subshapes: {},
        subcontrollers: {}
      }
    },
    watch: {
      _props: {
        handler: function (newVal, oldVal) {
          if (this.elementRole == 'opengraph-element') {
            if (!this.element) {
              this.drawShape();
            } else {
              this.updateShape();
            }
            this.emitElement();
          }
        },
        deep: true
      }
    },
    destroyed: function () {
      var me = this;

      //오픈그래프 역할일 경우 캔버스에 엘리먼트 등록 삭제.
      if (me.elementRole == 'opengraph-element') {
        if (me.canvasComponent) {
          me.canvasComponent.removeElement(me.id ? me.id : me.immidiateId);
        }
      }
      //서브 엘리먼트 역할일 경우 서브엘리먼트 등록
      else if (this.elementRole == 'sub-elements') {
        if (this.parentElementComponent) {
          this.parentElementComponent.removeSubShapes(me.id ? me.id : me.immidiateId);
        }
      }
      //서브 컨트롤러 역할일 경우 서브컨트롤러 등록
      else if (this.elementRole == 'sub-controller') {
        if (this.parentControllerComponent) {
          this.parentControllerComponent.removeCloneElement();
        }
      }
    },
    mounted: function () {
      var me = this;
      //console.log(this.geometrys);
      this.setElementRole();

      //오픈그래프 엘리먼트 역할일 경우 렌더링 수행
      if (this.elementRole == 'opengraph-element') {
        this.drawShape();
        this.emitElement();
      }
      //서브 엘리먼트 역할일 경우 서브엘리먼트 등록
      else if (this.elementRole == 'sub-elements') {
        if (this.parentElementComponent) {
          this.parentElementComponent.addSubShapes(this, me.id ? me.id : me.immidiateId);
        }
      }
      //서브 컨트롤러 역할일 경우 서브컨트롤러 등록
      else if (this.elementRole == 'sub-controller') {
        if (this.parentControllerComponent) {
          this.parentControllerComponent.addCloneElement(this);
        }
      }
    },
    methods: {
      addGeometry: function (geometryComponenet, id) {
        this.geometrys[id] = geometryComponenet;
      },
      removeGeometry: function (id) {
        delete this.geometrys[id];
      },
      addSubShapes: function (subShapeComponenet, id) {
        this.subshapes[id] = subShapeComponenet;
      },
      removeSubShapes: function (id) {
        delete this.subshapes[id];
      },
      addSubContollers: function (subControllerComponenet, id) {
        this.subcontrollers[id] = subControllerComponenet;
      },
      removeSubContollers: function (id) {
        delete this.subcontrollers[id];
      },
      emitElement: function () {
        var me = this;
        if (!me.element) {
          return;
        }
        let boundary = me.canvasComponent.canvas.getBoundary(me.element);
        if (!boundary) {
          return;
        }

        me.$emit('update:redraw', false);

        //width,height,x,y,angle,은 그대로 사용.

        //angle 이 있다면 잠시 원복시킨다.

//        if (angle) {
//          me.$emit('update:angle', angle);
//          me.element.shape.geom.rotate(-1 * angle);
//        }
//        me.$emit('update:width', boundary.getWidth());
//        me.$emit('update:height', boundary.getHeight());
//        me.$emit('update:x', boundary.getCentroid().x);
//        me.$emit('update:y', boundary.getCentroid().y);
//        //계산이 끝난 후 원복시킨다.
//        if (angle) {
//          me.element.shape.geom.rotate(angle);
//        }


        //geometry.rotate(angle);

//        me.$emit('update:width', boundary.getWidth());
//        me.$emit('update:height', boundary.getHeight());
//        me.width = boundary.getWidth();
//        me.height = boundary.getHeight();
//        me.x = boundary.getCentroid().x;
//        me.y = boundary.getCentroid().y;
//        me.label = me.element.shape.label;
//
//        var parent = me.canvas.getRenderer().getParent(element);
//        if (parent) {
//          me.parent = parent.id;
//        } else {
//          me.parent = null;
//        }
//        me.id = element.id;
//
//        if (element.shape.TYPE === OG.Constants.SHAPE_TYPE.EDGE) {
//          if ($(element).attr('_from')) {
//            me.from = $(element).attr('_from');
//          }
//          if ($(element).attr('_to')) {
//            me.to = $(element).attr('_to');
//          }
//        }
//
//        if (element.shape.geom.vertices) {
//          me.value = element.shape.geom.vertices.toString();
//        }
//
//        me.style = element.shape.geom.style.map;
//        me.geom = null;
//        me.element.shape
      },
      setElementRole: function () {
        //console.log(this.$parent);
        var me = this;
        var parentElementComponent = null;
        var parent;
        var elementRole;
        var getParent = function (component) {
          parent = component.$parent;
          if (parent) {

            //캔버스를 만나면 중단한다.
            if (parent.opengraphRole == 'canvas') {
              me.canvasComponent = parent;
              me.elementRole = 'opengraph-element';
            }
            //서브 엘리먼트를 만나면 상위 컴포넌트를 찾을때까지 계속한다.
            else if (parent.opengraphRole == 'sub-elements') {
              me.elementRole = 'sub-elements';
              getParent(parent);
            }
            //서브 컨트롤러를 만나면 중단한다.
            else if (parent.opengraphRole == 'sub-controller') {
              me.elementRole = 'sub-controller';
              me.parentControllerComponent = parent;
            }
            //상위 컴포넌트를 만나면 중단한다.
            else if (parent.opengraphRole == 'opengraph-element') {
              me.parentElementComponent = parent;
            }
            else {
              getParent(parent);
            }
          }
        }
        getParent(this);

        //상위 컴포넌트를 만나는동안 서브엘리먼트를 만나지 못했다면 에러
        if (me.parentElementComponent && me.elementRole != 'sub-elements') {
          throw new Error('Child element in element should be in sub-elements.');
        }

        //상위 컴포넌트가 없고, 캔버스를 만나지 못하고 컨트롤러에 등록된 상태가 아니면  에러
        if (!me.parentElementComponent && !me.canvasComponent && !me.parentControllerComponent) {
          throw new Error('Not found canvas for element.');
        }

        //상위 컴포넌트가 없고, 캔버스가 있을 때 캔버스에 등록
        if (!me.parentElementComponent && me.canvasComponent) {
          me.canvasComponent.addElement(this, me.id ? me.id : me.immidiateId);
        }
      },
      updateShape: function () {
        //아이디가 업데이트 된 경우
        var me = this;
        var id = me.id ? me.id : me.immidiateId;
        if (id != me.element.id) {
          me.element = me.canvasComponent.canvas.updateId(me.element, id);
        }
        //아이디를 보전하며 다시 그린다.
        this.drawShape();
      },
      drawShape: function () {
        var me = this;
        var id = me.id ? me.id : me.immidiateId;
        var shape = me.generateShape();
        if (!shape) {
          return;
        } else {
          shape.createSubShape = function () {
            return me.generateSubShapes();
          };
          shape.createController = function () {
            return me.generateSubController();
          };
          //shape.TYPE
          //if(OG.Constants.SHAPE_TYPE.TEXT)
          switch (shape.TYPE) {
            case OG.Constants.SHAPE_TYPE.GEOM:
            case OG.Constants.SHAPE_TYPE.GROUP:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height], me._style, id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.EDGE:
              if (me.vertices && me.vertices.length > 1) {
                var geom = new OG.geometry.PolyLine(me.vertices);
                geom.type = 'PolyLine';
                shape.geom = geom;
              }

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

              var fromElement, toElement, fromTerminal, toTerminal, fromP, toP;
              if (me.from) {
                fromElement = me.canvasComponent.canvas.getElementById(me.from);
                fromTerminal = me.from + '_TERMINAL_' + me.fromPosition[0] + '_' + me.fromPosition[1];
                fromP = me.canvasComponent.canvas.getRenderer()._getPositionFromTerminal(fromTerminal);
              }
              if (me.to) {
                toElement = me.canvasComponent.canvas.getElementById(me.to);
                toTerminal = me.to + '_TERMINAL_' + me.toPosition[0] + '_' + me.toPosition[1];
                toP = me.canvasComponent.canvas.getRenderer()._getPositionFromTerminal(toTerminal);
              }

              //vertices 가 없고, 연결할 대상이 있다면 connect 로 연결한다. 이때 연결 노드 정보는 자동으로 생성됨.
              if ((!me.vertices || me.vertices.length < 2) && fromElement && toElement) {
                me.element = me.canvasComponent.canvas.connect(
                  fromElement, toElement, me._style, me.label, fromP, toP, true, me.id);
              }
              //vertices 가 있고 연결할 대상이 있는 경우
              else if (fromElement && toElement) {
                me.element = me.canvasComponent.canvas.connect(
                  fromElement, toElement, me._style, me.label, fromP, toP, true, me.id, shape);
              }
              //그 외 연결할 대상이 없는 경우
              else {
                me.element = me.canvasComponent.canvas.drawShape(null, shape, null, me._style, id, null, true);
              }

              break;
            case OG.Constants.SHAPE_TYPE.HTML:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], me._style, id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.IMAGE:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], me._style, id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.TEXT:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], me._style, id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.SVG:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], me._style, id, me.parentId);
              break;
          }
        }
      },
      generateSubController: function () {
        var me = this;
        var controllers = [];
        if (!me.subcontrollers || $.isEmptyObject(me.subcontrollers)) {
          return controllers;
        }
        else {
          for (var key in me.subcontrollers) {
            var controllerComponent = me.subcontrollers[key];
            var cloneable = controllerComponent.cloneable;
            if (cloneable) {
              controllers.push({
                image: controllerComponent.image,
                create: {
                  shape: controllerComponent.cloneComponent.generateShape(),
                  width: controllerComponent.cloneComponent.width,
                  height: controllerComponent.cloneComponent.height,
                  style: controllerComponent.cloneComponent.style
                }
              })
            } else {
              controllers.push({
                image: controllerComponent.image,
                action: function (element) {
                  controllerComponent.$emit('click', me);
                }
              })
            }
          }
          return controllers;
        }
      },
      generateSubShapes: function () {
        var me = this;
        var subShape = [];
        if (!me.subshapes || $.isEmptyObject(me.subshapes)) {
          return subShape;
        }
        else {
          for (var key in me.subshapes) {
            let shape = me.subshapes[key].generateShape();
            subShape.push({
              shape: shape,
              width: me.subshapes[key].subWidth,
              height: me.subshapes[key].subHeight,
              top: me.subshapes[key].subTop,
              bottom: me.subshapes[key].subBottom,
              left: me.subshapes[key].subLeft,
              right: me.subshapes[key].subRight,
              align: me.subshapes[key].subAlign,
              'vertical-align': me.subshapes[key].subVerticalAlign,
              style: me.subshapes[key].subStyle
            })
          }
          return subShape;
        }
      },
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      },
      uuShapeId: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return 's' + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
      }
    }
  }
</script>


<style scoped lang="scss" rel="stylesheet/scss">

</style>


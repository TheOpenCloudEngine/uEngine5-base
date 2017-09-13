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
      },
      /**
       * 도형 속성
       */
      selectable: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      /**
       * 이동 가능여부
       * @type Boolean
       */
      movable: {
        default: function () {
          return false;
        },
        type: Boolean
      },

      /**
       * 리사이즈 가능여부
       * @type Boolean
       */
      resizable: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      /**
       * 가로방향 리사이즈 가능
       * @type {boolean}
       */
      resizex: {
        default: function () {
          return true;
        },
        type: Boolean
      },

      /**
       * 세로 방향 리사이즈 가능
       * @type {boolean}
       */
      resizey: {
        default: function () {
          return true;
        },
        type: Boolean
      },


      /**
       * 연결 가능여부
       * @type Boolean
       */
      connectable: {
        default: function () {
          return false;
        },
        type: Boolean
      },

      /**
       * From 연결 가능여부 (From(Shape) => To)
       * @type Boolean
       */
      enableFrom: {
        default: function () {
          return true;
        },
        type: Boolean
      },

      /**
       * To 연결 가능여부 (From => To(Shape))
       * @type Boolean
       */
      enableTo: {
        default: function () {
          return true;
        },
        type: Boolean
      },


      /**
       * Self 연결 가능여부
       * @type Boolean
       */
      selfConnectable: {
        default: function () {
          return false;
        },
        type: Boolean
      },

      /**
       * 가이드에 자기자신을 복사하는 컨트롤러 여부.
       * @type Boolean
       */
      connectCloneable: {
        default: function () {
          return false;
        },
        type: Boolean
      },

      /**
       * 드래그하여 연결시 연결대상 있는 경우에만 Edge 드로잉 처리 여부
       * @type Boolean
       */
      connectRequired: {
        default: function () {
          return true;
        },
        type: Boolean
      },

      /**
       * 드래그하여 연결시 그룹을 건너뛸때 스타일 변경 여부
       * @type Boolean
       */
      connectStyleChange: {
        default: function () {
          return true;
        },
        type: Boolean
      },

      /**
       * 가이드에 삭제 컨트롤러 여부
       * @type Boolean
       */
      deletable: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      /**
       * 라벨 수정여부
       * @type Boolean
       */
      labelEditable: {
        default: function () {
          return true;
        },
        type: Boolean
      },
      /**
       * 복사 가능 여부
       * @type {boolean}
       */
      copyable: {
        default: function () {
          return false;
        },
        type: Boolean
      },
      /**
       * x,y 축만 이동 가능여부. Y | N | none
       * @type {null}
       */
      copyable: {
        default: function () {
          return 'none';
        },
        type: String
      }
    },
    computed: {
      opengraphRole: function () {
        return 'opengraph-element';
      },
      _id: function () {
        if (this.id) {
          return this.id;
        } else {
          return this.immidiateId;
        }
      }
    }
    ,
    data: function () {
      return {
        props: JSON.parse(JSON.stringify(this._props)),
        elementRole: null,
        shapdId: 'OG.shape.' + this.uuShapeId(),
        immidiateId: this.uuid(),
        canvasComponent: null,
        parentElementComponent: null,
        parentControllerComponent: null,
        element: null,
        shape: null,
        geometrys: {}
        ,
        subshapes: {}
        ,
        subcontrollers: {}
      }
    }
    ,
    watch: {
      _props: {
        handler: function (newVal, oldVal) {
          this.props = JSON.parse(JSON.stringify(newVal))
        }
        ,
        deep: true
      },
      props: {
        handler: function (newVal, oldVal) {
          var needToWatch = false;
          for (var key in newVal) {
            if (typeof newVal[key] == 'object') {
              if (!oldVal[key] || JSON.stringify(newVal[key]) != JSON.stringify(oldVal[key])) {
                console.log(key, newVal[key], oldVal[key]);
                needToWatch = true;
              }
            } else {
              if (newVal[key] != oldVal[key]) {
                //리드로우 트리거가 false 일때는 반응하지 않는다.
                if (key == 'redraw' && newVal[key] == false) {
                  needToWatch = false;
                } else {
                  needToWatch = true;
                  console.log(key, newVal[key], oldVal[key]);
                }
              }
            }
          }
          if (!needToWatch) {
            return;
          }
          if (this.elementRole == 'opengraph-element') {
            if (!this.element) {
              this.drawShape();
            } else {
              this.updateShape();
            }
          }
        }
        ,
        deep: true
      }
    }
    ,
    destroyed: function () {
      var me = this;

      //오픈그래프 역할일 경우 캔버스에 엘리먼트 등록 삭제.
      if (me.elementRole == 'opengraph-element') {
        if (me.canvasComponent) {
          me.canvasComponent.removeElement(me._id);
        }
      }
      //서브 엘리먼트 역할일 경우 서브엘리먼트 등록
      else if (this.elementRole == 'sub-elements') {
        if (this.parentElementComponent) {
          this.parentElementComponent.removeSubShapes(me._id);
        }
      }
      //서브 컨트롤러 역할일 경우 서브컨트롤러 등록
      else if (this.elementRole == 'sub-controller') {
        if (this.parentControllerComponent) {
          this.parentControllerComponent.removeCloneElement();
        }
      }
    }
    ,
    mounted: function () {
      var me = this;
      this.setElementRole();

      //오픈그래프 엘리먼트 역할일 경우 렌더링 수행
      if (this.elementRole == 'opengraph-element') {
        this.drawShape();
        this.emitElement();
      }
      //서브 엘리먼트 역할일 경우 서브엘리먼트 등록
      else if (this.elementRole == 'sub-elements') {
        if (this.parentElementComponent) {
          this.parentElementComponent.addSubShapes(this, me._id);
        }
      }
      //서브 컨트롤러 역할일 경우 서브컨트롤러 등록
      else if (this.elementRole == 'sub-controller') {
        if (this.parentControllerComponent) {
          this.parentControllerComponent.addCloneElement(this);
        }
      }
    }
    ,
    methods: {
      addGeometry: function (geometryComponenet, id) {
        this.geometrys[id] = geometryComponenet;
      }
      ,
      removeGeometry: function (id) {
        delete this.geometrys[id];
      }
      ,
      addSubShapes: function (subShapeComponenet, id) {
        this.subshapes[id] = subShapeComponenet;
      }
      ,
      removeSubShapes: function (id) {
        delete this.subshapes[id];
      }
      ,
      addSubContollers: function (subControllerComponenet, id) {
        this.subcontrollers[id] = subControllerComponenet;
      }
      ,
      removeSubContollers: function (id) {
        delete this.subcontrollers[id];
      }
      ,
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
      }
      ,
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
          me.canvasComponent.addElement(this, me._id);
        }
      }
      ,
      updateShape: function () {
        //아이디가 업데이트 된 경우
        var me = this;
        if (me._id != me.element.id) {
          me.element = me.canvasComponent.canvas.updateId(me.element, me._id);
        }
        //아이디를 보전하며 다시 그린다.
        this.drawShape();
      }
      ,
      drawShape: function () {
        var me = this;
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

          //shape 속성 달기
          me.bindShapeProperties(shape);

          //shape 이벤트 달기
          me.bindShapeEvents(shape);

          //style 복사하기.
          var style = JSON.parse(JSON.stringify(me._style));

          //shape.TYPE
          //if(OG.Constants.SHAPE_TYPE.TEXT)
          switch (shape.TYPE) {
            case OG.Constants.SHAPE_TYPE.GEOM:
            case OG.Constants.SHAPE_TYPE.GROUP:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height], style, me._id, me.parentId);
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
                  fromElement, toElement, style, me.label, fromP, toP, true, me._id);
                console.log('me.element', me.element);
              }
              //vertices 가 있고 연결할 대상이 있는 경우
              else if (fromElement && toElement) {
                me.element = me.canvasComponent.canvas.connect(
                  fromElement, toElement, style, me.label, fromP, toP, true, me._id, shape);
              }
              //그 외 연결할 대상이 없는 경우
              else {
                me.element = me.canvasComponent.canvas.drawShape(null, shape, null, style, me._id, null, true);
              }
              //스타일은 복사하여 pops 에 영향을 주지 않도록 한다.
              //console.log('me._style' , me._style);

              break;
            case OG.Constants.SHAPE_TYPE.HTML:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], style, me._id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.IMAGE:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], style, me._id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.TEXT:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], style, me._id, me.parentId);
              break;
            case OG.Constants.SHAPE_TYPE.SVG:
              me.element = me.canvasComponent.canvas.drawShape([me.x, me.y], shape, [me.width, me.height, me.angle], style, me._id, me.parentId);
              break;
          }
          this.bindElementEvents();
          this.emitElement();
        }
      },
      /**
       * 도형 프로퍼티를 등록한다.
       * @param shape
       */
      bindShapeProperties: function (shape) {
        shape.SELECTABLE = this.selectable;
        shape.MOVABLE = this.movable;
        shape.RESIZABLE = this.resizable;
        shape.RESIZEX = this.resizex;
        shape.RESIZEY = this.resizey;
        shape.CONNECTABLE = this.connectable;
        shape.ENABLE_FROM = this.enableFrom;
        shape.ENABLE_TO = this.enableTo;
        shape.SELF_CONNECTABLE = this.selfConnectable;
        shape.CONNECT_CLONEABLE = this.connectCloneable;
        shape.CONNECT_REQUIRED = this.connectRequired;
        shape.CONNECT_STYLE_CHANGE = this.connectStyleChange;
        shape.DELETABLE = this.deletable;
        shape.LABEL_EDITABLE = this.labelEditable;
        shape.COPYABLE = this.copyable;
        shape.AXIS = this.axis;
      },
      /**
       * 도형 이벤트 프로퍼티를 등록한다.
       * TODO 컴포넌트 등록된 도형이 아닌경우, props 로 표현하여 리턴하는 메소드가 필요하다.
       * @param shape
       */
      bindShapeEvents: function (shape) {
        var me = this;
        shape.onResize = function (offset) {
          me.$emit('resize', me, offset);
        };
        shape.onDrawLabel = function (text) {
          me.$emit('drawLabel', me, text);
        };
        shape.onLabelChanged = function (text, beforeText) {
          me.$emit('labelChanged', me, text, beforeText);
        };
        shape.onBeforeRemoveShape = function () {
          var result;
          me.$emit('beforeRemoveShape', me, function (emitResult) {
            result = emitResult;
          });
          return result;
        };
        shape.onRemoveShape = function () {
          me.$emit('removeShape', me);
        };
        shape.onDrawShape = function () {
          me.$emit('drawShape', me);
        };
        shape.onBeforeLabelChange = function (text, beforeText) {
          var result;
          me.$emit('beforeLabelChange', me, text, beforeText, function (emitResult) {
            result = emitResult;
          });
          return result;
        };
        shape.onRedrawShape = function () {
          me.$emit('redrawShape', me);
        };
        shape.onBeforeConnectShape = function (edge, fromShape, toShape) {
          var result;
          me.$emit('beforeConnectShape',
            me,
            me.canvasComponent.getElementById(edge.id) || edge,
            me.canvasComponent.getElementById(fromShape.id) || fromShape,
            me.canvasComponent.getElementById(toShape.id) || toShape,
            function (emitResult) {
              result = emitResult;
            });
          return result;
        };
        shape.onConnectShape = function (edge, fromShape, toShape) {
          me.$emit('connectShape',
            me,
            me.canvasComponent.getElementById(edge.id) || edge,
            me.canvasComponent.getElementById(fromShape.id) || fromShape,
            me.canvasComponent.getElementById(toShape.id) || toShape
          )
        };
        shape.onDisconnectShape = function (edge, fromShape, toShape) {
          me.$emit('disconnectShape',
            me,
            me.canvasComponent.getElementById(edge.id) || edge,
            me.canvasComponent.getElementById(fromShape.id) || fromShape,
            me.canvasComponent.getElementById(toShape.id) || toShape
          )
        };
        shape.onGroup = function (groupShapeEle) {
          me.$emit('group',
            me,
            me.canvasComponent.getElementById(groupShapeEle.id) || groupShapeEle
          )
        };
        shape.onUnGroup = function () {
          me.$emit('unGroup', me);
        };
        shape.onMoveShape = function (offset) {
          me.$emit('moveShape', me, offset);
        };
        shape.onRotateShape = function (angle) {
          me.$emit('rotateShape', me, angle);
        };
        shape.onDuplicated = function (edge, target, copy) {
          //여기서, 서브컨트롤러를 알아내는 법
          me.$emit('clone',
            me.canvasComponent.getElementById(edge.id) || edge,
            me.canvasComponent.getElementById(target.id) || target,
            me.canvasComponent.getElementById(copy.id) || copy
          );
        };
        shape.onPasteShape = function (copied, pasted) {
          me.$emit('pasteShape',
            me.canvasComponent.getElementById(copied.id) || copied,
            me.canvasComponent.getElementById(pasted.id) || pasted
          );
        };
        /**
         * 자신에게 도형들이 그룹으로 들어왔을때의 이벤트
         * @param groupElement
         * @param elements
         */
        shape.onAddToGroup = function (groupElement, elements, eventOffset) {
          var addedComponents = [];
          for (var i = 0; i < elements.lenght; i++) {
            addedComponents.push(me.canvasComponent.getElementById(elements[i].id) || elements[i]);
          }
          me.$emit('addToGroup',
            me.canvasComponent.getElementById(groupElement.id) || groupElement,
            addedComponents,
            eventOffset
          );
        };
        /**
         * 자신이 그룹속으로 들어갔을 때의 이벤트
         * @param groupElement
         * @param element
         */
        shape.onAddedToGroup = function (groupElement, element, eventOffset) {
          me.$emit('addedToGroup',
            me.canvasComponent.getElementById(groupElement.id) || groupElement,
            me.canvasComponent.getElementById(element.id) || element,
            eventOffset
          );
        };
        shape.onSelectShape = function () {
          me.$emit('selectShape', me);
        };
        shape.onDeSelectShape = function () {
          me.$emit('deSelectShape', me);
        };
      },
      /**
       * 클릭, 더블클릭 이벤트를 반영한다.
       */
      bindElementEvents: function () {
        var me = this;
        if (me.element && !$(me.element).data('vue-event')) {
          $(me.element).data('vue-event', true);
          $(me.element).bind('click', function () {
            me.$emit('click', me);
          });
          $(me.element).bind('dblclick', function () {
            me.$emit('dblclick', me);
          });
        }
      },
      /**
       * 서브 컨트롤러의 정보를 얻어와 shpae 의 컨트롤을 반환한다.
       * @return {Array}
       */
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
              me.addCloneableController(controllerComponent, controllers);
            } else {
              me.addClickController(controllerComponent, controllers);
            }
          }
          return controllers;
        }
      }
      ,
      addClickController: function (controllerComponent, controllers) {
        var me = this;
        controllers.push({
          image: controllerComponent.image,
          action: function (element) {
            controllerComponent.$emit('click', me);
          }
        })
      }
      ,
      addCloneableController: function (controllerComponent, controllers) {
        var me = this;
        //컨트롤러의 clone 이벤트 트리거 등록. (카피된 대상:generateShape 에 이벤트를 등록한다.)
        var generateShape = controllerComponent.cloneComponent.generateShape();
        generateShape.onDuplicated = function (edge, target, copy) {
          controllerComponent.$emit('clone',
            me.canvasComponent.getElementById(edge.id) || edge,
            me.canvasComponent.getElementById(target.id) || target,
            me.canvasComponent.getElementById(copy.id) || copy);
        };
        controllers.push({
          image: controllerComponent.image,
          create: {
            shape: generateShape,
            width: controllerComponent.cloneComponent.width,
            height: controllerComponent.cloneComponent.height,
            style: JSON.parse(JSON.stringify(controllerComponent.cloneComponent._style))
          }
        })
      }
      ,
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
              style: JSON.parse(JSON.stringify(me.subshapes[key].subStyle))
            })
          }
          return subShape;
        }
      }
      ,
      uuid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
      }
      ,
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


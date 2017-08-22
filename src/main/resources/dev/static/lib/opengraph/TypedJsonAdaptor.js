/**
 * TypedJsonAdaptor JsonDefinition 변환기
 *
 * @class
 *
 * @author <a href="mailto:sppark@uengine.org">Seungpil Park</a>
 */
var TypedJsonAdaptor = function () {
  this.json = null;
};
TypedJsonAdaptor.prototype = {

  getPrevEdgeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['targetRef'] == id) {
        ids.push(flow['sourceRef'] + '-' + flow['targetRef']);
      }
    });
    return ids;
  },
  getNextEdgeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['sourceRef'] == id) {
        ids.push(flow['sourceRef'] + '-' + flow['targetRef']);
      }
    });
    return ids;
  },
  getPrevShapeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['targetRef'] == id) {
        ids.push(flow['sourceRef']);
      }
    });
    return ids;
  },
  getNextShapeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['sourceRef'] == id) {
        ids.push(flow['targetRef']);
      }
    });
    return ids;
  },

  importData: function (data) {
    var me = this;
    var definition = data.definition;
    var canvasViewPort = definition['canvasViewPort'];
    var canvasWidth, canvasHeight, backdoor, scale;
    if (canvasViewPort) {
      canvasWidth = canvasViewPort['canvasWidth'] ? canvasViewPort['canvasWidth'] : 1000;
      canvasHeight = canvasViewPort['canvasHeight'] ? canvasViewPort['canvasHeight'] : 800;
      scale = canvasViewPort['scale'] ? canvasViewPort['scale'] : 1;
    }

    var json = {
      opengraph: {
        '@width': canvasWidth,
        '@height': canvasHeight,
        '@scale': scale,
        cell: []
      }
    };

    //childActivities, roles ,sequenceFlows  를 제외한 데이터는 캔버스 데이터로 등록.
    var canvasData = {};
    for (var key in data) {
      if (key != 'childActivities' && key != 'roles' && key != 'sequenceFlows') {
        canvasData[key] = data[key];
      }
    }
    json.opengraph['@data'] = escape(OG.JSON.encode(canvasData));

    var makeChild = function (items, childType) {
      console.log(items);
      $.each(items, function (i, item) {
          let view;
          if (childType == 'shape') {
            view = item['elementView'];
          }
          else if (childType == 'sequence') {
            view = item['relationView'];
          }
          else if (childType == 'role') {
            view = item['elementView'];
          }

          var cell = {};
          var tempShape = eval('new ' + view.shapeId + '()');
          if (!tempShape) {
            return;
          }

          cell['@id'] = view.id;
          if (view.parent) {
            cell['@parent'] = view.parent;
          }
          cell['@shapeType'] = tempShape.TYPE;
          cell['@shapeId'] = view.shapeId;
          cell['@x'] = view.x;
          cell['@y'] = view.y;
          cell['@width'] = view.width;
          cell['@height'] = view.height;

          if (childType == 'shape') {
            var prevShapeIds = me.getPrevShapeIds(sequenceFlows, view.id);
            var nextShapeIds = me.getNextShapeIds(sequenceFlows, view.id);
            if (prevShapeIds.length > 0) {
              cell['@from'] = prevShapeIds.toString();
            }
            if (nextShapeIds.length > 0) {
              cell['@to'] = nextShapeIds.toString();
            }

            var prevEdgeIds = me.getPrevEdgeIds(sequenceFlows, view.id);
            var nextEdgeIds = me.getNextEdgeIds(sequenceFlows, view.id);
            if (prevEdgeIds.length > 0) {
              cell['@fromEdge'] = prevEdgeIds.toString();
            }
            if (nextEdgeIds.length > 0) {
              cell['@toEdge'] = nextEdgeIds.toString();
            }
          }

          if (view.label) {
            cell['@label'] = view.label;
          }
          if (view.fromLabel) {
            cell['@fromLabel'] = view.fromLabel;
          }
          if (view.toLabel) {
            cell['@toLabel'] = view.toLabel;
          }
          if (view.angle && view.angle !== 0) {
            cell['@angle'] = view.angle;
          }
          if (view.value) {
            cell['@value'] = view.value;
          }
          if (view.style) {
            cell['@style'] = view.style;
          }
          if (view.geom) {
            cell['@geom'] = view.geom;
          }
          cell['@childs'] = [];

          //elementView , relationView 를 제외한 값을 데이터로.
          if (childType == 'shape' || childType == 'role') {
            var elementData = {};
            for (var prop in item) {
              if (prop != 'elementView') {
                elementData[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(elementData));
          } else {
            var elementData = {};
            for (var prop in item) {
              if (prop != 'relationView') {
                elementData[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(elementData));
          }

          json.opengraph.cell.push(cell);

          // cell['@id'] = $(item).attr('id');
          // if ($(item).parent().attr('id') === $(node).attr('id')) {
          //   cell['@parent'] = $(node).attr('id');
          // } else {
          //   cell['@parent'] = $(item).parent().attr('id');
          // }
          // cell['@shapeType'] = shape.TYPE;
          // cell['@shapeId'] = shape.SHAPE_ID;
          // cell['@x'] = envelope.getCentroid().x;
          // cell['@y'] = envelope.getCentroid().y;
          // cell['@width'] = envelope.getWidth();
          // cell['@height'] = envelope.getHeight();
          // if (style) {
          //   cell['@style'] = escape(OG.JSON.encode(style));
          // }
          //
          // if (shape.TYPE === OG.Constants.SHAPE_TYPE.EDGE) {
          //   if ($(item).attr('_from')) {
          //     cell['@from'] = $(item).attr('_from');
          //   }
          //   if ($(item).attr('_to')) {
          //     cell['@to'] = $(item).attr('_to');
          //   }
          // } else {
          //   prevShapeIds = CANVAS.getPrevShapeIds(item);
          //   nextShapeIds = CANVAS.getNextShapeIds(item);
          //   if (prevShapeIds.length > 0) {
          //     cell['@from'] = prevShapeIds.toString();
          //   }
          //   if (nextShapeIds.length > 0) {
          //     cell['@to'] = nextShapeIds.toString();
          //   }
          // }
          //
          // if ($(item).attr('_fromedge')) {
          //   cell['@fromEdge'] = $(item).attr('_fromedge');
          // }
          // if ($(item).attr('_toedge')) {
          //   cell['@toEdge'] = $(item).attr('_toedge');
          // }
          // if (shape.label) {
          //   cell['@label'] = escape(shape.label);
          // }
          // if (shape.fromLabel) {
          //   cell['@fromLabel'] = escape(shape.fromLabel);
          // }
          // if (shape.toLabel) {
          //   cell['@toLabel'] = escape(shape.toLabel);
          // }
          // if (shape.angle && shape.angle !== 0) {
          //   cell['@angle'] = shape.angle;
          // }
          // if (shape instanceof OG.shape.ImageShape) {
          //   cell['@value'] = shape.image;
          // } else if (shape instanceof OG.shape.HtmlShape) {
          //   cell['@value'] = escape(shape.html);
          // } else if (shape instanceof OG.shape.TextShape) {
          //   cell['@value'] = escape(shape.text);
          // } else if (shape instanceof OG.shape.EdgeShape) {
          //   vertices = geom.getVertices();
          //   cell['@value'] = '';
          //   for (var i = 0, leni = vertices.length; i < leni; i++) {
          //     cell['@value'] = cell['@value'] + vertices[i];
          //     if (i < vertices.length - 1) {
          //       cell['@value'] = cell['@value'] + ','
          //     }
          //   }
          // }
          // if (geom) {
          //   cell['@geom'] = escape(geom.toString());
          // }
          // if (item.data) {
          //   cell['@data'] = escape(OG.JSON.encode(item.data));
          // } else {
          //   cell['@data'] = escape(OG.JSON.encode(item.shape.data));
          // }
          // if (shape.textList) {
          //   cell['@textList'] = escape(OG.JSON.encode(shape.textList));
          // }
          // if (item.dataExt) {
          //   cell['@dataExt'] = escape(OG.JSON.encode(item.dataExt));
          // }
          // if (shape.LoopType) {
          //   cell['@loopType'] = shape.LoopType;
          // }
          // if (shape.TaskType) {
          //   cell['@taskType'] = shape.TaskType;
          // }
          // if (shape.exceptionType) {
          //   cell['@exceptionType'] = shape.exceptionType;
          // }
        }
      );
    };

    var roles = definition['roles'];
    var childActivities = definition['childActivities'][1];
    var sequenceFlows = definition['sequenceFlows'];
    //makeChild(roles, 'role');
    makeChild(childActivities, 'shape');
    makeChild(sequenceFlows, 'sequence');
    //childShape(rootGroup, true);
    this.json = json;
    console.log(json);
    return json;

  },
  exportData: function () {
    return this.json;
  }
}
;
TypedJsonAdaptor.prototype.constructor = TypedJsonAdaptor;

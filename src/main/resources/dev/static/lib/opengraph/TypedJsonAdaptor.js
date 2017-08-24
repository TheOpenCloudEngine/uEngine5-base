/**
 * TypedJsonAdaptor JsonDefinition 변환기
 *
 * @class
 *
 * @author <a href="mailto:sppark@uengine.org">Seungpil Park</a>
 */
var TypedJsonAdaptor = function (canvas, shapeIdJavaMappings) {
  this.json = null;
  this.canvas = canvas;

  this.shapeIdJavaMappings = shapeIdJavaMappings;
};
TypedJsonAdaptor.prototype = {
  getJavaClassByShapeId: function (shapeId) {
    var javaClass = undefined;
    let shapeIdJavaMappings = this.shapeIdJavaMappings;
    for (var key in shapeIdJavaMappings) {
      if (shapeIdJavaMappings[key].shapeId == shapeId) {
        javaClass = shapeIdJavaMappings[key].java;
      }
    }
    return javaClass;
  },

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
  importFromElementView: function (data) {
    var me = this;
    var definition = data.definition;
    var canvasViewPort = definition['canvasViewPort'];
    var canvasWidth, canvasHeight, scale;
    if (canvasViewPort) {
      canvasWidth = canvasViewPort['canvasWidth'] ? canvasViewPort['canvasWidth'] : 2000;
      canvasHeight = canvasViewPort['canvasHeight'] ? canvasViewPort['canvasHeight'] : 1800;
      scale = canvasViewPort['scale'] ? canvasViewPort['scale'] : 1;
    } else {
      canvasWidth = 2000;
      canvasHeight = 1800;
      scale = 1;
    }

    var json = {
      opengraph: {
        '@width': canvasWidth,
        '@height': canvasHeight,
        '@scale': scale,
        cell: []
      }
    };

    //childActivities, roles ,sequenceFlows, opengraph  를 제외한 데이터는 캔버스 'properties' 데이터로 등록.
    var canvasData = {
      properties: {}
    };
    for (var key in data) {
      if (key != 'childActivities' && key != 'roles' && key != 'sequenceFlows' && key != 'opengraph') {
        canvasData.properties[key] = data[key];
      }
    }
    json.opengraph['@data'] = escape(OG.JSON.encode(canvasData));

    var makeChild = function (items, childType) {
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


          if (view.parent) {
            cell['@parent'] = view.parent;
          }
          cell['@shapeType'] = tempShape.TYPE;
          cell['@shapeId'] = view.shapeId;
          cell['@x'] = view.x;
          cell['@y'] = view.y;
          cell['@width'] = view.width;
          cell['@height'] = view.height;

          if (childType == 'shape' || childType == 'role') {
            cell['@id'] = view.id;

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
          else if (childType == 'sequence') {
            cell['@id'] = item['sourceRef'] + '-' + item['targetRef'];

            if (view.from) {
              cell['@from'] = view.from;
            }
            if (view.to) {
              cell['@to'] = view.to;
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

          if (view.LoopType) {
            cell['@loopType'] = view.LoopType;
          }
          if (view.TaskType) {
            cell['@taskType'] = view.TaskType;
          }
          if (view.exceptionType) {
            cell['@exceptionType'] = view.exceptionType;
          }

          cell['@childs'] = [];

          //elementView , relationView 를 제외한 값을 'properties' 데이터로.
          var shapeData = {
            properties: {}
          };
          if (childType == 'shape' || childType == 'role') {
            for (var prop in item) {
              if (prop != 'elementView') {
                shapeData.properties[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(shapeData));
          }
          else {
            for (var prop in item) {
              if (prop != 'relationView') {
                shapeData.properties[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(shapeData));
          }

          json.opengraph.cell.push(cell);
        }
      );
    };

    var roles = definition['roles'];
    var childActivities = definition['childActivities'][1];
    var sequenceFlows = definition['sequenceFlows'];
    makeChild(roles, 'role');
    makeChild(childActivities, 'shape');
    makeChild(sequenceFlows, 'sequence');
    //childShape(rootGroup, true);
    return json;
  },

  importJson: function (data) {
    var json = data.definition.opengraph;
    //예전 방식일 경우
    if (!json) {
      json = this.importFromElementView(data);
    }else{
      json = JSON.parse(json);
    }

    var me = this;
    delete json.opengraph['@data'];
    me.canvas.loadJSON(json);

    var definition = data.definition;
    //childActivities, roles ,sequenceFlows, opengraph  를 제외한 데이터는 캔버스 'properties' 데이터로 등록.
    var canvasData = {
      properties: {}
    };
    for (var key in data) {
      if (key != 'childActivities' && key != 'roles' && key != 'sequenceFlows' && key != 'opengraph') {
        canvasData.properties[key] = data[key];
      }
    }
    //me.canvas.getRootGroup().data = canvasData;

    let elements = me.canvas.getRenderer().getAllNotEdges();
    var findByLaneName = function (name) {
      var lane = undefined;
      $.each(elements, function (c, element) {
        if (me.canvas.getRenderer().isLane(element)) {
          if (element.shape.data.properties.name == name) {
            lane = element;
          }
        }
      });
      return lane;
    };

    var makeChild = function (items, childType) {
      $.each(items, function (i, item) {
          var id, element;
          if (childType == 'shape') {
            id = item.tracingTag;
          }
          else if (childType == 'role') {
            var laneElement = findByLaneName(item.name);
            if (laneElement) {
              id = laneElement.id;
            }
          }
          else if (childType == 'sequence') {
            id = item['sourceRef'] + '-' + item['targetRef'];
          }

          element = me.canvas.getElementById(id);
          if (!element) {
            return;
          }

          //elementView , relationView 를 제외한 값을 'properties' 데이터로.
          var shapeData = {
            properties: {}
          };
          if (childType == 'shape' || childType == 'role') {
            for (var prop in item) {
              if (prop != 'elementView') {
                shapeData.properties[prop] = item[prop];
              }
            }
          }
          else {
            for (var prop in item) {
              if (prop != 'relationView') {
                shapeData.properties[prop] = item[prop];
              }
            }
          }

          //기존 오픈그래프 엘리먼트의 데이터가 틀려졌다면 도형 업데이트
          var needToRedraw = false, oldVal, newVal;
          if (!element.shape.data) {
            needToRedraw = true;
          } else {
            oldVal = JSON.stringify(element.shape.data);
            newVal = JSON.stringify(shapeData);
            if (oldVal != newVal) {
              needToRedraw = true;
            }
          }
          if (needToRedraw) {
            element.shape.data = shapeData;
            me.canvas.getRenderer().redrawShape(element);
          }
        }
      );
    };

    var roles = definition['roles'];
    var childActivities = definition['childActivities'][1];
    var sequenceFlows = definition['sequenceFlows'];
    makeChild(roles, 'role');
    makeChild(childActivities, 'shape');
    makeChild(sequenceFlows, 'sequence');
  },
  exportJson: function () {
    var me = this;
    var opengraph = me.canvas.toJSON();
    var data = {
      definition: {
        _type: 'org.uengine.kernel.ProcessDefinition',
        childActivities: [
          'java.util.ArrayList',
          []
        ],
        roles: [],
        sequenceFlows: [],
        opengraph: JSON.stringify(opengraph)
      }
    };
    let allShapes = me.canvas.getAllShapes();
    $.each(allShapes, function (i, element) {
      var object = {}, properties, relatedElements;
      if (element.shape instanceof OG.HorizontalLaneShape) {
        properties = element.shape.data.properties;
        object = properties;
        data.definition.roles.push(object);
      }

      else if (element.shape instanceof OG.EdgeShape) {
        relatedElements = me.canvas.getRelatedElementsFromEdge(element);
        if (relatedElements.from) {
          object.sourceRef = relatedElements.from.id
          object.targetRef = relatedElements.to.id
        }
        data.definition.sequenceFlows.push(object);
      }

      else {
        var shapeId = element.shape.SHAPE_ID;
        let javaClass = me.getJavaClassByShapeId(shapeId);
        properties = element.shape.data.properties;
        object = properties;
        object['_type'] = javaClass;
        data.definition.childActivities[1].push(object);
      }
    });

    return data;
  }
}
;
TypedJsonAdaptor.prototype.constructor = TypedJsonAdaptor;
